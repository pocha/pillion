package com.getpillion;

import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.ConnectionDetector;
import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.getpillion.common.TimeDateFilterFragment;
import com.getpillion.models.Ride;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import uk.co.senab.bitmapcache.BitmapLruCache;

public class AllRidesActivity extends ExtendMeSherlockWithMenuActivity implements ActionBar.TabListener {

	//private AdView adView;
	AlarmManager am;
	int startIndex = 0;

	private Handler mHandler;
	private Handler fHandler;
	ConnectionDetector cd;

//	private AsyncListView mListView;
	private BitmapLruCache mCache;

	private String FB_USER_ID = "";
	String friendFacebookID = "";

	private ProgressDialog progress;
	private List<String> packageList = new ArrayList<String>();

	private SlidingMenu menu = null;
    private ActionBar.Tab offeringTab, seekingTab;


    private RideAdapter adapter;
    private ArrayList<Ride> rides;
    private Tab selectedTab = null;

    @OnTouch(R.id.filterTime) boolean launchFilterTimeDialog(View v, MotionEvent event){
        if (event.getAction() != MotionEvent.ACTION_UP)
            return true;
        TimeDateFilterFragment p = TimeDateFilterFragment.newInstance((EditText)v);
        p.show(getSupportFragmentManager(),"not sure what this tag suppose to do");

        return true;
    }
    @InjectView(R.id.fromFilter) EditText from;
    @OnTextChanged(R.id.fromFilter) void updateRoutesFrom(CharSequence text){
        updateRoutes(text);
    }
    @InjectView(R.id.toFilter) EditText to;
    @OnTextChanged(R.id.toFilter) void updateRoutesTo(CharSequence text){
        updateRoutes(text);
    }
    //TODO implement time filtering

    private void updateRoutes(CharSequence text){
        if (text.length() < 3 && text.length() != 0) //2nd condition takes care of clearing filter if position has cleared the input field
            return;
        getData();
    }

    @InjectView(R.id.noRoutesFound)
    TextView noRoutesFound;
    //@InjectView(R.id.loading) TextView loading;
    @InjectView(R.id.scheduleRide)
    Button scheduleRide;
    @OnClick(R.id.scheduleRide) void takeUserToMyRoutes(View v){
        Intent intent = new Intent(AllRidesActivity.this,MyRoutesActivity.class);
        startActivity(intent);
    }
    @InjectView(R.id.listView) ListView mListView;


    private AsyncTask asyncTask;
    public void getData(){
       /* progress = ProgressDialog.show(AllRoutesActivity.this, "",
                "Loading Routes. Please wait", true, false);*/
        if (asyncTask != null )
            asyncTask.cancel(true);

        //loading.setVisibility(View.VISIBLE);
        noRoutesFound.setText("Loading routes ..");
        rides.clear(); // so that Loading routes become visible
        adapter.notifyDataSetChanged();

        asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                    if (isCancelled())
                        return null;

                    /*ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
                    postParams.add(new BasicNameValuePair("facebookUserID",
                            FB_USER_ID));
                    postParams.add(new BasicNameValuePair("app_id", appID));
                    String url = Constant.SERVER + Constant.USER_APP_VIEW;
                    Helper.postData(url, postParams);*/
                    Thread.sleep(2000);

                    //send filter data to server through Helper.postData
                    String result = "{\"routes\":[" +
                                        "{\"globalId\":1, " +
                                            "\"route\": {" +
                                                "\"globalId\": 1," +
                                                "\"origin\":\"Brigade\", " +
                                                "\"dest\":\"Ecospace\", " +
                                                "\"isOffered\":true" +
                                            "}," +
                                            "\"timestamp\":" + Time.valueOf("08:00:00").getTime() + "," +
                                            "\"vehicle\":{ \"globalId\":1,\"model\":\"Merc\", \"color\":\"black\", \"number\":\"KA51 Q8745\"}," +

                                            "\"owner\":{\"globalId\":1,\"name\":\"Ashish\", \"title\":\"CEO Codelearn\"}," +
                                            "\"date\":\""+ new Date().getTime() +"\"," +
                                            "\"isScheduled\":true," +
                                            "\"users\" : [" +
                                                   "{\"globalId\":2,\"name\":\"Anish\", \"title\":\"CEO Codelearn\"}," +
                                                   "{\"globalId\":3,\"name\":\"Anter\", \"title\":\"Co-founder Channelyst\"}" +
                                                 "]" +
                                         "}," +
                                        "{\"globalId\":2, " +
                                            "\"route\": {" +
                                                "\"globalId\": 2," +
                                                "\"origin\":\"Ecospace\", " +
                                                "\"dest\":\"Adarsh\", " +
                                                "\"isOffered\":true" +
                                            "}," +
                                            "\"timestamp\":" + Time.valueOf("17:00:00").getTime() + "," +
                                            "\"vehicle\":{ \"globalId\":1,\"model\":\"Merc\", \"color\":\"black\", \"number\":\"KA51 Q8745\"}," +
                                            "\"owner\":{\"globalId\":1,\"name\":\"Ashish\", \"title\":\"CEO Codelearn\"}," +
                                           // "\"date\":\""+ new Date() +"\"," +
                                            "\"isScheduled\":false" +
                                        "}" +
                                    "]}";
                    if (Math.random() < 0.5){
                        result = "{\"routes\":[" +
                                    "{\"globalId\":3, " +
                                        "\"route\": {" +
                                            "\"globalId\": 3," +
                                            "\"origin\":\"Brigade\", " +
                                            "\"dest\":\"Ecospace\", " +
                                            "\"isOffered\":true" +
                                        "}," +
                                        "\"timestamp\":" + Time.valueOf("09:00:00").getTime() + "," +
                                        "\"vehicle\":{ \"globalId\":2,\"model\":\"Maruti\", \"color\":\"black\", \"number\":\"KA51 Q8745\"}," +
                                        "\"owner\":{\"globalId\":2,\"name\":\"Anish\", \"title\":\"CEO Codelearn\"}," +
                                        "\"date\":\""+ new Date().getTime() +"\"," +
                                        "\"isScheduled\":true" +
                                    "}," +
                                    "{\"globalId\":4, " +
                                        "\"route\": {" +
                                            "\"globalId\": 4," +
                                            "\"origin\":\"Brigade\", " +
                                            "\"dest\":\"Ecospace\", " +
                                            "\"isOffered\":true" +
                                        "}," +
                                        "\"timestamp\":" + Time.valueOf("18:00:00").getTime() + "," +
                                        "\"vehicle\":{ \"globalId\":2,\"model\":\"Merc\", \"color\":\"black\", \"number\":\"KA51 Q8745\"}," +
                                        "\"owner\":{\"globalId\":2,\"name\":\"Anish\", \"title\":\"Changed title\"}," +
                                        // "\"date\":\""+ new Date() +"\"," +
                                        "\"isScheduled\":false" +
                                     "}" +
                                "]}";
                    }

                    Ride.getRoutesFromJson(result);
                } catch (Exception e) {
                    Log.e("ashish","exception",e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (!isCancelled()) {
                    showRoutes();
                    //progress.hide();
                    //loading.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    public void showRoutes(){
        rides.clear();

        String whereString = "WHERE " + (
                        (selectedTab.getTag() == "offering")
                                ? "Route.is_offered = 1"
                                : "Route.is_offered = 0"
        );

        if (!from.getText().toString().isEmpty())
            whereString += "AND Route.origin like '%"+ from.getText().toString() +"%'";
        if (!to.getText().toString().isEmpty())
            whereString += "AND Route.dest like '%"+ to.getText().toString() +"%'";

        rides.addAll(Ride.findWithQuery(Ride.class,
                        "SELECT * FROM Ride JOIN Route ON Ride.route = Route.id " +
                                whereString +
                                " order by Ride.timestamp")
        );

        /*for (Ride ride : rides){
            Log.d("AllRidesActivity","Route info of ride - " + ride.route.origin + " " + ride.route.dest + " " + ride.timestamp );
        }*/
        adapter.notifyDataSetChanged();
        Log.d("AllRoutesActivity", "Data count in adapter - " + adapter.getCount());
        Helper.setListViewHeightBasedOnChildren(mListView);

        noRoutesFound.setText("No Routes Found");
    }



    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        selectedTab = tab;
        showRoutes();
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(getApplicationContext(),
				Constant.BUGSENSE_API_KEY);
		setContentView(R.layout.route_list);
        ButterKnife.inject(this);

		/*progress = ProgressDialog.show(AllRoutesActivity.this, "",
				"Loading Routes. Please wait", true, false);


		String title = "Top Friends Apps";
		try {
			title = getIntent().getExtras().getString("title");
			if (title == null) {
				title = "Top Friends Apps";
			}
		} catch (Exception ex) {
		}*/
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("All Routes");
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        rides = new ArrayList<Ride>();

        adapter = new RideAdapter(this, rides);
        mListView.setEmptyView(noRoutesFound);
        mListView.setAdapter(adapter);

        offeringTab = actionBar.newTab().setText("Rides Offered");
        offeringTab.setTag("offering");
        offeringTab.setTabListener(this);
        actionBar.addTab(offeringTab);

        seekingTab = actionBar.newTab().setText("Rides Sought");
        seekingTab.setTag("seeking");
        seekingTab.setTabListener(this);
        actionBar.addTab(seekingTab);

        getData();


        /*if ( com.getpillion.common.Session.packageList.size() == 0 ) {
			PackageManager pm = getApplicationContext().getPackageManager();
			List<ApplicationInfo> packages = pm
					.getInstalledApplications(PackageManager.GET_META_DATA);
			for (ApplicationInfo packageInfo : packages) {
				com.getpillion.common.Session.packageList
						.add(packageInfo.packageName);
			}
		}
		
		packageList = com.getpillion.common.Session.packageList;
		//Toast.makeText(getApplicationContext(), "Total apps " + packageList.size(), Toast.LENGTH_SHORT).show();

		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(Constant.PREFS_NAME, 0);
		FB_USER_ID = settings.getString("facebook_user_id", "");


        menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.0f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.menu);
*/

        //cd = new ConnectionDetector(getApplicationContext());

/*		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					String str = (String) msg.obj;
					// System.out.println("------" + str);
					JSONObject jObj = new JSONObject(str);
					if (jObj.getInt("count") == 0) {
						Intent intent = new Intent().setClass(
								AllRoutesActivity.this, NewRouteActivity.class);
						startActivity(intent);
					} else {
						SharedPreferences settings = getApplicationContext()
								.getSharedPreferences(Constant.PREFS_NAME, 0);
						SharedPreferences.Editor editor = settings.edit();
						editor.putString(
								"friends_count_"
										+ settings.getString(
												"facebook_user_id", ""),
								jObj.getString("count"));
						editor.commit();

						String type = "";
						try {
							type = getIntent().getExtras().getString("type");
						} catch (Exception ex) {
						}
						// System.out.println("------" + type);
						if (type.equalsIgnoreCase("friend_app")) {

							Button loadMoreApps = (Button) findViewById(R.id.load_more_app);
							loadMoreApps.setVisibility(View.GONE);

							JSONArray jArr = jObj.getJSONArray("friends");
							final ListView listview = (ListView) findViewById(R.id.listview);
							String[] values = new String[jArr.length()];

							int count = 0;
							for (int i = 0; i < jArr.length(); i++) {
								JSONObject jObj1 = jArr.getJSONObject(i);
								FeedBO feed = new FeedBO();
								feed.setTotalNewApps(jObj1.getInt("new_apps"));
								feed.setIsItNew(jObj1.getInt("new_apps"));
								feed.setAppName(jObj1.getString("name"));
								feed.setAppIcon(jObj1.getString("avatar_url"));
								feed.setAppDeveloper("Total Apps: "
										+ jObj1.getString("total_apps"));
								feed.setAppPackage("FACEBOOK_USER:"
										+ jObj1.getString("facebook_user_id"));
								feed.setFriendView(1);
								values[count] = feed.toString();
								count = count + 1;
							}

							RouteAdapter adapter = new RouteAdapter(
									getApplicationContext(), values, FB_USER_ID);
							listview.setAdapter(adapter);
							progress.dismiss();
							
						} else {
							loadFreindsApps();
						}
					}
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		};

		fHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					String str = (String) msg.obj;
					JSONObject jMainObj = new JSONObject(str);
					startIndex = jMainObj.getInt("next_index");
					JSONArray jArr = jMainObj.getJSONArray("apps");

					final ListView listview = (ListView) findViewById(R.id.listview);

					ArrayList<FeedBO> installedApps = new ArrayList<FeedBO>();
					ArrayList<FeedBO> notinstalledApps = new ArrayList<FeedBO>();

					for (int i = 0; i < jArr.length(); i++) {
						FeedBO feed = new FeedBO();
						try {
						JSONObject jObj = jArr.getJSONObject(i);
						feed.setIsItNew(jObj.getInt("new_app"));
						feed.setAppName(jObj.getString("app_name"));
						feed.setAppIcon(jObj.getString("app_icon_url"));
						feed.setAppDeveloper(jObj.getString("app_header_link"));
						feed.setAppPackage(jObj.getString("app_package_name"));
						feed.setTotalFriends(jObj.getString("total_friends"));
						feed.setFriendNames(jObj.getString("friend_names"));
						feed.setAppID(jObj.getString("app_id"));
						} catch (Exception ex) {
							continue;
						}
						if (isInstalled(feed.getAppPackage())) {
							feed.setIsInstalled("1");
							installedApps.add(feed);
						} else {
							feed.setIsInstalled("0");
							notinstalledApps.add(feed);
						}

						if (friendFacebookID == null
								|| friendFacebookID.equals("")) {
							feed.setAllFriendsMode("1");
						} else {
							feed.setAllFriendsMode("0");
						}
					}

					notinstalledApps.addAll(installedApps);

					String[] values = new String[jArr.length()];
					int count = 0;
					for (int i = 0; i < notinstalledApps.size(); i++) {
						values[count] = notinstalledApps.get(i).toString();
						count = count + 1;
					}

					RouteAdapter adapter = new RouteAdapter(
							getApplicationContext(), values, FB_USER_ID);
					listview.setAdapter(adapter);

					if (jArr.length() == 0) {
						Toast.makeText(
								getApplicationContext(),
								"No New Apps Found. To See more apps, add more friends",
								Toast.LENGTH_LONG).show();
						Intent intent = new Intent(AllRoutesActivity.this,
								NewRouteActivity.class);
						startActivity(intent);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};

		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		setRepeatingAlarm();

		File cacheDir = new File(getCacheDir(), "smoothie");
		cacheDir.mkdirs();

		BitmapLruCache.Builder bbuilder = new BitmapLruCache.Builder();
		bbuilder.setMemoryCacheEnabled(true)
				.setMemoryCacheMaxSizeUsingHeapSize();
		bbuilder.setDiskCacheEnabled(true).setDiskCacheLocation(cacheDir);

		mCache = bbuilder.build();

		mListView = (AsyncListView) findViewById(R.id.listview);
		// BitmapLruCache cache = App.getInstance(this).getBitmapCache();
		ImageLoader loader = new ImageLoader(mCache);

		ItemManager.Builder builder = new ItemManager.Builder(loader);
		builder.setPreloadItemsEnabled(true).setPreloadItemsCount(5);
		builder.setThreadPoolSize(4);

		mListView.setItemManager(builder.build());
*/

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                Intent intent = new Intent(AllRidesActivity.this, RideInfoActivity.class);
                intent.putExtra("rideId", rides.get(position).getId());
                startActivity(intent);
                /*
				// publishOpenGraphAction();
				final String item = (String) parent.getItemAtPosition(position);
				// Toast.makeText(getBaseContext(), item,
				// Toast.LENGTH_LONG).show();
				FeedBO feed = new FeedBO();
				feed.populateFromString(item);

				String packageName = feed.getAppPackage();
				if (packageName.indexOf("FACEBOOK_USER:") >= 0) {
					// Friend list. Click to open the friend apps
					Intent intent = new Intent(AllRoutesActivity.this,
							AllRoutesActivity.class);
					intent.putExtra("friend_facebook_id", packageName);
					intent.putExtra("type", "");
					String[] nameArr = feed.getAppName().split(" ");
					intent.putExtra("title", nameArr[0] + "'s Apps");
					intent.putExtra("no_invite_button", "1");
					startActivity(intent);
				} else {

					showAppInstallDialog(feed.getFriendNames(),
							feed.getAppName(), feed.getAppPackage());
					updateUserView(feed.getAppID());
				}
				*/
			}
		});

		// registerGCM();

		//myPackages();
		
/*		try {
			String no_invite_button = getIntent().getExtras().getString("no_invite_button");
			if ( no_invite_button != null && no_invite_button.equals("1") ) {
				LinearLayout no_invite_button_div = (LinearLayout)findViewById(R.id.invite_friend_div);
				no_invite_button_div.setVisibility(LinearLayout.GONE);
			}
		} catch (Exception e) { }
		
		

		String initialized = settings.getString("app_initialized", "");
		if (initialized.equals("")) {
			uploadApps();
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("app_initialized", "1");
			editor.commit();
			// System.out.println(">>>>>NI");
		} else {
			fetchFriendsCount();
			// System.out.println(">>>>>I");
		}
*/
	}

/*	public void updateUserView(final String appID) {
		try {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {

						ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
						postParams.add(new BasicNameValuePair("facebookUserID",
								FB_USER_ID));
						postParams.add(new BasicNameValuePair("app_id", appID));
						String url = Constant.SERVER + Constant.USER_APP_VIEW;
						Helper.postData(url, postParams);
					} catch (Exception e) {

					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {

				}
			}.execute();
		} catch (Exception ex) {
		}
	}

	public void publishOpenGraphAction() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					String url = "https://graph.facebook.com/me/friendsandroidapp:found?access_token="
							+ Session.getActiveSession().getAccessToken()
							+ "method=POST&app=http%3A%2F%2Fsamples.ogp.me%2F130344400507373";
					Helper.getData(url);
				} catch (Exception e) {

				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

			}
		}.execute();

	}

	public void setRepeatingAlarm() {
		Intent intent = new Intent(this, TimeAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				(2 * 60 * 60 * 1000), pendingIntent);
	}

	private void registerGCM() {
		GCMRegistrar.checkDevice(getApplicationContext());
		GCMRegistrar.checkManifest(getApplicationContext());
		final String regId = GCMRegistrar
				.getRegistrationId(getApplicationContext());
		if (regId.equals("")) {
			GCMRegistrar.register(getApplicationContext(), Constant.SENDER_ID);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String type = "";
		try {
			type = getIntent().getExtras().getString("type");
		} catch (Exception ex) {
		}
		if (!type.equals("friend_app")) {

		} else {
			menu.add(0, 0, 0, "Invite Friend").setIcon(R.drawable.add_friend1)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		try {
			if (item.getItemId() == android.R.id.home) {
				menu.toggle();
				return true;
			} else {
				String type = "";
				try {
					type = getIntent().getExtras().getString("type");
				} catch (Exception ex) {
				}
				if (!type.equals("friend_app")) {
					loadFreindsApps();
				} else {
					Intent intent = new Intent(AllRoutesActivity.this,
							NewRouteActivity.class);
					startActivity(intent);
				}
				return true;
			}
		} catch (Exception ex) {
			return true;
		}
	}

	public void fetchFriendsCount() {

		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(Constant.PREFS_NAME, 0);
		final String facebookUserID = settings
				.getString("facebook_user_id", "");
		String totalFriendsCount = settings.getString("friends_count_"
				+ facebookUserID, "");

		String type = "";
		try {
			type = getIntent().getExtras().getString("type");
		} catch (Exception ex) {
		}

		if (totalFriendsCount.equals("") || type.equalsIgnoreCase("friend_app")) {
			// No friends yet. Fetch it. App will look good with atleast one

			String serverValsKey = "fetchFriendsCount_" + facebookUserID;
			String serverValStr = "";

			if (serverValStr != null && !serverValStr.equals("")) {
				System.out.println(">>>>>>>FC: from cache<<<<<<<");
				Message msg = new Message();
				msg.obj = serverValStr;
				mHandler.sendMessage(msg);
			} else {
				new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
						try {
							ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
							postParams.add(new BasicNameValuePair(
									"facebookUserID", facebookUserID));

							String serverValsKey = "fetchFriendsCount_"
									+ facebookUserID;
							String serverValStr = "";

							String str = "";
							if (serverValStr != null
									&& !serverValStr.equals("")) {
								System.out.println(">>>>>>>FC: from cache");
								str = serverValStr;
							} else {
								String url = Constant.SERVER
										+ Constant.FRIENDS_COUNT;
								str = Helper.postData(url, postParams);
								
							}

							// System.out.println(facebookUserID + "::" + str);
							Message msg = new Message();
							msg.obj = str;
							mHandler.sendMessage(msg);
						} catch (Exception e) {
							progress.dismiss();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						// progress.dismiss();
					}
				}.execute();
			}
		} else {
			loadFreindsApps();
		}
	}

	// Upload Apps if this is the first time
	public void upploadUserApps(final String packageStr) {

		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(Constant.PREFS_NAME, 0);
		final String facebookUserID = settings
				.getString("facebook_user_id", "");
		String appUploaded = settings.getString("apps_uploaded_"
				+ facebookUserID, "");

		if (appUploaded.equals("")) {

			final ProgressDialog progress = ProgressDialog.show(
					AllRoutesActivity.this, "", "Initializing. Please wait", true,
					false);

			try {
				FileOutputStream fos = openFileOutput(
						Constant.PACKAGE_FILE_NAME, Context.MODE_PRIVATE);
				fos.write(packageStr.getBytes());
				fos.close();
			} catch (Exception ex) {
			}

			// No Apps. UploadApps
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
						postParams.add(new BasicNameValuePair("facebookUserID",
								facebookUserID));
						postParams.add(new BasicNameValuePair("packages",
								packageStr));
						String url = Constant.SERVER + Constant.USER_APPS;
						// System.out.println("----------------" + packageStr);
						Helper.postData(url, postParams);

					} catch (Exception e) {
						progress.dismiss();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					progress.dismiss();
					fetchFriendsCount();
				}
			}.execute();
		}
	}

	private void uploadApps() {

		final PackageManager pm = getPackageManager();
		// get a list of installed apps.
		List<ApplicationInfo> packages = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);

		int count = 0;
		List<String> packageList = new ArrayList<String>();
		for (ApplicationInfo packageInfo : packages) {
			if (!isSystemPackage(packageInfo)) {
				count = count + 1;
				packageList.add(packageInfo.packageName);
			}
		}

		String[] apps = packageList.toArray(new String[packageList.size()]);
		Arrays.sort(apps);
		// System.out.println("Total apps: " + apps.length);
		String packageStr = "";
		for (int i = 0; i < apps.length; i++) {
			if (packageStr.equals("")) {
				packageStr = packageStr + apps[i];
			} else {
				packageStr = packageStr + "," + apps[i];
			}
		}

		String md5Hash = md5Digest(packageStr);
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(Constant.PREFS_NAME, 0);
		final String facebookUserID = settings
				.getString("facebook_user_id", "");
		String appPackageMD5 = settings.getString("apps_md5_" + facebookUserID,
				"");

		// System.out.println(appPackageMD5 + "::" + md5Hash);

		if (!md5Hash.equals(appPackageMD5)) {
			upploadUserApps(packageStr);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("apps_md5_" + facebookUserID, md5Hash);
			editor.commit();
		} else {
			fetchFriendsCount();
		}
	}

	public String md5Digest(String text) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(text.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			int messageDigestLenght = messageDigest.length;
			for (int i = 0; i < messageDigestLenght; i++) {
				String hashedData = Integer
						.toHexString(0xFF & messageDigest[i]);
				while (hashedData.length() < 2)
					hashedData = "0" + hashedData;
				hexString.append(hashedData);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return ""; // if text is null then return nothing
	}

	private boolean isSystemPackage(ApplicationInfo pkgInfo) {
		return ((pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}

	public void loadFreindsAppsBtn(View v) {
		loadFreindsApps();
	}

	public void inviteFriend(View v) {
		Intent intent = new Intent(AllRoutesActivity.this,
				NewRouteActivity.class);
		startActivity(intent);
	}

	public void loadFreindsApps() {
		// System.out.println("--------HERE");
		SharedPreferences settings = getApplicationContext()
				.getSharedPreferences(Constant.PREFS_NAME, 0);
		final String facebookUserID = settings
				.getString("facebook_user_id", "");
		String type = "";
		try {
			type = getIntent().getExtras().getString("type");
		} catch (Exception ex) {
		}
		final String appType = type;

		if (type.equals("my_apps")) {
			loadMyApps();
			return;
		}

		try {
			friendFacebookID = getIntent().getExtras().getString(
					"friend_facebook_id");
		} catch (Exception ex) {
		}
		final String friendFacebookIDVal = friendFacebookID;

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
					postParams.add(new BasicNameValuePair("facebookUserID",
							facebookUserID));
					postParams.add(new BasicNameValuePair("start_index",
							startIndex + ""));
					if (friendFacebookIDVal != null) {
						postParams.add(new BasicNameValuePair(
								"friendFacebookID", friendFacebookIDVal + ""));
					}
					//System.out.println("----->>>>" + friendFacebookIDVal);
					postParams
							.add(new BasicNameValuePair("type", appType + ""));

					String serverValsKey = "loadFreindsApps_"
							+ friendFacebookIDVal + "_" + appType;
					String serverValStr = "";

					String str = "";
					if (serverValStr != null && !serverValStr.equals("")) {
						// System.out.println(">>>>>>>Val: " + serverValStr);
						str = serverValStr;
					} else {
						String url = Constant.SERVER + Constant.FRIENDS_APPS;
						str = Helper.postData(url, postParams);
						
					}

					Message msg = new Message();
					msg.obj = str;
					fHandler.sendMessage(msg);
				} catch (Exception e) {
					progress.dismiss();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progress.dismiss();
			}
		}.execute();

	}

	public void sendRequestDialog(View v) {
		try {
			Bundle params = new Bundle();
			params.putString("message",
					"Super AWESOME way to discover android apps your friends are using!");

			showDialogWithoutNotificationBar(params);
		} catch (Exception ex) {
			Intent intent = new Intent().setClass(AllRoutesActivity.this,
					MainActivity.class);
			startActivity(intent);
		}
	}

	public void addOthers(View v) {

		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intent.EXTRA_SUBJECT, "Checkout Friends Apps!");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"Click on the link below to See the android apps your friends are using!.\n\nhttps://play.google.com/store/apps/details?id=com.appcovery.android.appcoveryapp\n\nFriends App is Super AWESOME way to discover android apps your friends are using!");
		startActivity(Intent.createChooser(intent, "How do you want to share?"));

	}

	public void publishNewsFeed(View v) {
		try {
			Bundle params = new Bundle();

			params.putString("name", "Checkout AppCovery");
			params.putString("caption", "Discover Apps through Friends");
			params.putString("description",
					"AppCovery helps you see Apps your friends are using");
			params.putString("picture",
					"http://s3.amazonaws.com/friendsapps/appcovery.png");
			params.putString(
					"link",
					"https://play.google.com/store/apps/details?id=com.appcovery.android.appcoveryapp");

			showFeedDialogWithoutNotificationBar(params);
		} catch (Exception ex) {
			Intent intent = new Intent().setClass(AllRoutesActivity.this,
					MainActivity.class);
			startActivity(intent);
		}
	}

	public void showDialogWithoutNotificationBar(Bundle params) {
		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
				AllRoutesActivity.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error != null) {
							if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(getApplicationContext(),
										"Request CANCELLED", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Network Error", Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							final String requestId = values
									.getString("request");
							if (requestId != null) {
								Toast.makeText(getApplicationContext(),
										"Request sent " + requestId,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Request CANCELLED", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}

				}).build();
		requestsDialog.show();
	}

	public void showFeedDialogWithoutNotificationBar(Bundle params) {
		WebDialog requestsDialog = (new WebDialog.FeedDialogBuilder(
				AllRoutesActivity.this, Session.getActiveSession(), params))
				.setOnCompleteListener(new OnCompleteListener() {

					@Override
					public void onComplete(Bundle values,
							FacebookException error) {
						if (error != null) {
							if (error instanceof FacebookOperationCanceledException) {
								Toast.makeText(getApplicationContext(),
										"Request CANCELLED", Toast.LENGTH_SHORT)
										.show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Network Error", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}

				}).build();
		requestsDialog.show();
	}

	@Override
	public void onDestroy() {
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance().activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this);
	}

	public void showAppInstallDialog(String friends, String appName,
			final String appPackage) {
		String[] friendsArr = friends.split(",");
		//Toast.makeText(getApplicationContext(), friends + " // " + friendsArr.length, Toast.LENGTH_LONG).show();
		if ( friendsArr.length == 1 || friends.equals("") ) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="
					+ appPackage));
			EasyTracker.getTracker().sendEvent("app",
					"click", appPackage, 0L);

			startActivity(intent);
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Friends Using " + appName);
		builder.setItems(friendsArr, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

			}

		})
				.setPositiveButton("Install App",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse("market://details?id="
										+ appPackage));
								EasyTracker.getTracker().sendEvent("app",
										"click", appPackage, 0L);

								startActivity(intent);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});

		AlertDialog alert = builder.create();
		alert.show();
	}

	

	private void myPackages() {
		populateMyPackages();
		
	}

	private boolean isInstalled(String packageName) {
		boolean found = false;
		for (int i = 0; i < packageList.size(); i++) {
			if (packageName.equalsIgnoreCase(packageList.get(i))) {
				found = true;
				break;
			}
		}
		return found;
	}

	public void loadMyApps() {
		myPackages();
		final ListView listview = (ListView) findViewById(R.id.listview);

		RouteAdapter adapter = new RouteAdapter(this,
				com.getpillion.common.Session.values,
				FB_USER_ID);
		listview.setAdapter(adapter);
		progress.dismiss();

	}

	private void populateMyPackages() {

		if (com.getpillion.common.Session.values == null) {
			PackageManager pm = getApplicationContext().getPackageManager();
			// get a list of installed apps.
			List<ApplicationInfo> packages = pm
					.getInstalledApplications(PackageManager.GET_META_DATA);

			for (ApplicationInfo packageInfo : packages) {
				com.getpillion.common.Session.packageList
						.add(packageInfo.packageName);
			}

			int count = 0;
			int totalSystemPackages = 0;
			for (ApplicationInfo packageInfo : packages) {
				if (!isSystemPackage(packageInfo)) {
					count = count + 1;
				}
			}

			SharedPreferences settings = getApplicationContext()
					.getSharedPreferences(Constant.PREFS_NAME, 0);

			com.getpillion.common.Session.values = new String[count];
			count = 0;
			totalSystemPackages = 0;
			for (ApplicationInfo packageInfo : packages) {

				if (!isSystemPackage(packageInfo)) {
					String shareSetting = settings.getString("no.share."
							+ packageInfo.packageName, "");
					if (shareSetting.equals("0")) {
						FeedBO feed = new FeedBO();
						feed.setIsItNew(0);
						feed.setAppIcon("NA");
						feed.setIconResourceID(packageInfo.packageName + "");

						com.getpillion.common.Session.hm
								.put(packageInfo.packageName + "", packageInfo
										.loadIcon(getPackageManager()));

						feed.setAppName(packageInfo.loadLabel(
								getPackageManager()).toString());
						feed.setAppDeveloper(packageInfo.packageName);
						feed.setAppPackage(packageInfo.packageName);
						feed.setTotalFriends("0");
						feed.setFriendNames("NA");
						feed.setAppID("NA");
						feed.setIsInstalled("1");
						feed.setAllFriendsMode("0");
						com.getpillion.common.Session.values[count] = feed
								.toString();
						count = count + 1;
					}
				} else {
					totalSystemPackages = totalSystemPackages + 1;
				}
			}

			for (ApplicationInfo packageInfo : packages) {

				if (!isSystemPackage(packageInfo)) {
					String shareSetting = settings.getString("no.share."
							+ packageInfo.packageName, "");
					if (!shareSetting.equals("0")) {
						FeedBO feed = new FeedBO();
						feed.setIsItNew(0);
						feed.setAppIcon("NA");
						feed.setIconResourceID(packageInfo.packageName + "");

						com.getpillion.common.Session.hm
								.put(packageInfo.packageName + "", packageInfo
										.loadIcon(getPackageManager()));

						feed.setAppName(packageInfo.loadLabel(
								getPackageManager()).toString());
						feed.setAppDeveloper(packageInfo.packageName);
						feed.setAppPackage(packageInfo.packageName);
						feed.setTotalFriends("0");
						feed.setFriendNames("NA");
						feed.setAppID("NA");
						feed.setIsInstalled("1");
						feed.setAllFriendsMode("0");
						com.getpillion.common.Session.values[count] = feed
								.toString();
						count = count + 1;
					}
				} else {
					totalSystemPackages = totalSystemPackages + 1;
				}
			}
		}
	}

	public void show_toast_message(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
				.show();
	}
*/
}
