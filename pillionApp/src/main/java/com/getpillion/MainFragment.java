package com.getpillion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

//import com.facebook.UiLifecycleHelper;
import com.getpillion.common.Constant;


public class MainFragment extends Fragment {

	//private UiLifecycleHelper uiHelper;
	ProgressDialog progress = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);
		Button startButton = (Button) view
				.findViewById(R.id.startButton);


        getActivity().getActionBar().hide();



        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NewRouteActivitySelectMode.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

		/*authButton.setFragment(this);
		// authButton.setPublishPermissions(Arrays.asList("publish_actions"));
		authButton.setReadPermissions(Arrays.asList("user_birthday", "email"));*/

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                Constant.PREFS_NAME, 0);
        if (sharedPref.getBoolean("appInitialized",false)) { //app already initialized. Take to all routes page
            Intent intent = new Intent(getActivity(),AllRoutesActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
		super.onCreate(savedInstanceState);
		/*uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);*/
	}

/*	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {

			SharedPreferences settings = getActivity().getSharedPreferences(
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
				getActivity().startActivity(intent);
			}
		} else if (state.isClosed()) {

		}
	}
	
	
	public void myPackageList () {
		if ( com.getpillion.common.Session.packageList.size() > 0 ) {
			return;
		}
		PackageManager pm = getActivity().getPackageManager();
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
				getActivity().startActivity(intent);
			}
		}.execute();

	}
*/
}
