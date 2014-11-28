package com.getpillion;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.demach.konotor.Konotor;
import com.getpillion.common.Constant;
import com.getpillion.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.atomic.AtomicInteger;

//import com.facebook.UiLifecycleHelper;


public class MainActivity extends SherlockFragmentActivity {

	//private UiLifecycleHelper uiHelper;
	ProgressDialog progress = null;
    String error;


	/*@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getSharedPreferences(
                Constant.PREFS_NAME, 0);
        if (sharedPref.getBoolean("appInitialized",false)) { //app already initialized. Take to all routes page
            Intent intent = new Intent(MainActivity.this,AllRidesActivity.class);
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Button startButton = (Button)findViewById(R.id.startButton);


        getSupportActionBar().hide();

        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = ProgressDialog.show(MainActivity.this, "", "Initializing ..", true, false);
                new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... params) {
                        try {
                            initialize();
                        } catch (Exception e) {
                            Log.e(TAG,"inside initialize catch",e);
                            //e.printStackTrace();
                            error = e.getMessage();
                            return false;
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        progress.dismiss();
                        if (result) {
                            Intent intent = new Intent(MainActivity.this, NewRouteActivitySelectMode.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();

            }
        });

		/*authButton.setFragment(this);
		// authButton.setPublishPermissions(Arrays.asList("publish_actions"));
		authButton.setReadPermissions(Arrays.asList("user_birthday", "email"));*/

		//return view;
	}



    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "135560142667";
    static final String TAG = "GCM Demo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    String regid;

    private void initialize() throws Exception{
        //writing code that need to be done during app start
        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    Konotor.getInstance(getApplicationContext()).updateGcmRegistrationId(regid);
                }catch (Exception e){
                    throw new Exception("Could not connect to Google server.");
                }
            }
            else {
                Log.i(TAG,"registration id " + regid);
            }

            // Persist the regID - no need to register again.
            storeRegistrationId(context, regid);

            // You should send the registration ID to your server over HTTP, so it
            // can use GCM/HTTP or CCS to send messages to your app.
            sendRegistrationIdToBackend(regid);

        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
            throw new Exception("No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() throws Exception {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                //finish();
                throw new Exception("This device is not supported");
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() throws Exception{
        /*new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {*/
                String msg = "";

                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend(regid);

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);

                Log.i(TAG, msg);
                //return msg;
            /*}

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);*/
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getApplicationContext().getSharedPreferences(
                Constant.PREFS_NAME, 0);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String regid) throws Exception {
        //get & store global_id of the user

        //check if position exists else create new position
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                Constant.PREFS_NAME, 0);
        User user = null;
        if (sharedPref.getLong("userId",0) == 0)
            user = new User();
        else
            user = User.findById(User.class,sharedPref.getLong("userId",0));

        user.reg_id = regid;
        user.saveWithoutSync();

        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putLong("userId",user.getId());
        sharedPrefEditor.commit();

        //Helper.createSyncAccount(getApplicationContext());
        user.sendForSync("update", true); //this will update global_id as well

    }

/*	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {

			SharedPreferences settings = getSharedPreferences(
					Constant.PREFS_NAME, 0);
			String initialized = settings.getString("app_initialized", "");
			if (initialized.equals("")) {
				ProgressDialog.show(getActivity(), "",
						"Working. Please wait ...", true, false);
				final String accessToken = session.getAccessToken();
				Request.executeMeRequestAsync(session,
                        new Request.GraphUserCallback() {

                            // callback after Graph API response with position
                            // object
                            @Override
                            public void onCompleted(GraphUser position,
                                                    Response response) {
                                if (position != null) {
                                    SharedPreferences settings = getActivity()
                                            .getSharedPreferences(
                                                    Constant.PREFS_NAME, 0);

                                    SharedPreferences.Editor editor = settings
                                            .edit();
                                    editor.putString("facebook_user_id",
                                            position.getId());
                                    editor.commit();

                                    if (settings.getString(
                                            "facebook_access_token_"
                                                    + position.getId(), "").equals(
                                            "")) {
                                        // First time access - register position

                                        editor = settings.edit();
                                        editor.putString(
                                                "facebook_access_token_"
                                                        + position.getId(),
                                                accessToken);
                                        editor.commit();

                                        MainActivity.facebookUserID = position
                                                .getId();
                                        registerUser();
                                    } else {

                                        // Update groups
                                        registerUser();

                                    }
                                }
                            }
                        });
			} else {
				progress = ProgressDialog.show(getActivity(), "",
						"Working. Please wait ...", true, false);
				myPackageList();
				progress.dismiss();
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				if (MainActivity.shareMyApps.equals("1")) {
					MainActivity.shareMyApps = "0";
					intent.putExtra("type", "my_apps");
					intent.putExtra("title", "Select Apps to Share");
				} else if (MainActivity.newApps.equals("1")) {
					MainActivity.newApps = "0";
					intent.putExtra("type", "new");
					intent.putExtra("title", "New Apps");
				} else {
					intent.putExtra("type", "friend_app");
					intent.putExtra("title", "Friends");
					intent.putExtra("show_right_arrow", "1");
				}
				startActivity(intent);
			}
		} else if (state.isClosed()) {

		}
	}
	
	
	public void myPackageList () {
		if ( com.getpillion.common.Session.packageList.size() > 0 ) {
			return;
		}
		PackageManager pm = getPackageManager();
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo packageInfo : packages) {
			com.getpillion.common.Session.packageList
					.add(packageInfo.packageName);
		}
	}
	
	

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void registerUser() {

		final ProgressDialog progress = ProgressDialog.show(getActivity(), "",
				"Working. Please wait ...", true, false);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							1);
					nameValuePairs.add(new BasicNameValuePair("access_token",
							Session.getActiveSession().getAccessToken()));

					String url = Constant.SERVER + Constant.REGISTRATION_API;
					Helper.postData(url, nameValuePairs);

				} catch (Exception e) {
					e.printStackTrace();
					progress.dismiss();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progress.dismiss();
				Intent intent = new Intent(getActivity(), HomeActivity.class);
				if (MainActivity.shareMyApps.equals("1")) {
					MainActivity.shareMyApps = "0";
					intent.putExtra("type", "my_apps");
					intent.putExtra("title", "Select Apps to Share");
				} else if (MainActivity.newApps.equals("1")) {
					MainActivity.newApps = "0";
					intent.putExtra("type", "new");
					intent.putExtra("title", "New Apps");
				} else {
					intent.putExtra("type", "friend_app");
					intent.putExtra("title", "Friends");
				}
				startActivity(intent);
			}
		}.execute();

	}
*/
}
