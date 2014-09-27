package com.getpillion;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.getpillion.common.Constant;
import com.getpillion.common.Helper;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TimeAlarm extends BroadcastReceiver {

	private Context context;
	private Handler fHandler;
	private Handler myHandler;
	private PackageManager pm;
	private SharedPreferences settings;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		pm = context.getPackageManager();

		fHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					
					String str = (String) msg.obj;
					JSONObject jMainObj = new JSONObject(str);
					JSONArray jArr = jMainObj.getJSONArray("apps");

					int count = 0;
					for (int i = jArr.length() - 1; i >= 0; i--) {
						JSONObject jObj = jArr.getJSONObject(i);
						if (jObj.getInt("new_app") == 1) {
							count = count + 1;
						}
					}

					if (count > 0) {
						generateNotificationForNewApps(count
								+ "+ new friends apps found",
								"Click here to see the apps");
					}
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		};
		
		
		
		myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				try {
					
					String str = (String) msg.obj;
					JSONObject jMainObj = new JSONObject(str);
					JSONArray jArr = jMainObj.getJSONArray("apps");
					
					
					// get a list of installed apps.
					List<ApplicationInfo> packages = pm
							.getInstalledApplications(PackageManager.GET_META_DATA);

					List<String> packageList = new ArrayList<String>();
					for (ApplicationInfo packageInfo : packages) {
						if (!isSystemPackage(packageInfo)) {
							//System.out.println(packageInfo.packageName);
							packageList.add(packageInfo.packageName);
						}
					}
					
					

					int count = 0;
					String diffPackages = "";
					for ( int j=0; j<packageList.size(); j++) {
						boolean found = false;
						for (int i = jArr.length() - 1; i >= 0; i--) {
							JSONObject jObj = jArr.getJSONObject(i);
							if ( jObj.getString("app_package_name").equals(packageList.get(j))) {
								found = true;
								break;
							}
						}
						if ( !found ) {
							//System.out.println(">>>>" + packageList.get(j));
							
							if ( diffPackages.equals("")) {
								diffPackages = packageList.get(j);
							} else {
								diffPackages = diffPackages + "," + packageList.get(j);
							}
							
							String notShared = settings.getString("no.share." + packageList.get(j), "");
							//showToastMessage(packageList.get(j) + " // " + notShared + "--");
							if ( !notShared.equals("0")) {
								SharedPreferences.Editor editor = settings.edit();
								editor.putString("no.share." + packageList.get(j), "0");
								editor.commit();
								count = count + 1;
							}
						}
					}
					
					if ( !diffPackages.equals("")) {
						upploadUserApps(diffPackages, "0");
					}

					if (count > 0) {
						generateNotification(count
								+ " new apps found",
								"Click here to share apps with friends");
					}
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			}
		};
		
		settings = context
				.getSharedPreferences(Constant.PREFS_NAME, 0);
		

		SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
		boolean pref_notification = sharedPrefs.getBoolean("pref_notification", false);
		boolean share_app = sharedPrefs.getBoolean("share_app", false);
		
		if ( !pref_notification ) {
			//Toast.makeText(context, "Notification OFF", Toast.LENGTH_SHORT).show();
		} else {
			loadFreindsApps();
		}
		
		if ( !share_app ) {
			//Detect new app and show 
			checkNewApps();
		} else {
			uploadApps();
		}
		
	}
	
	
	public void showToastMessage (String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	
	public void checkNewApps() {
		SharedPreferences settings = context.getSharedPreferences(
				Constant.PREFS_NAME, 0);
		final String facebookUserID = settings
				.getString("facebook_user_id", "");
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
					postParams.add(new BasicNameValuePair("facebookUserID",
							facebookUserID));
					String url = Constant.SERVER + Constant.MY_APPS;
					String str = Helper.postData(url, postParams);

					Message msg = new Message();
					msg.obj = str;
					myHandler.sendMessage(msg);
				} catch (Exception e) {

				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

			}
		}.execute();

	}

	public void loadFreindsApps() {
		SharedPreferences settings = context.getSharedPreferences(
				Constant.PREFS_NAME, 0);
		final String facebookUserID = settings
				.getString("facebook_user_id", "");
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
					postParams.add(new BasicNameValuePair("facebookUserID",
							facebookUserID));
					postParams.add(new BasicNameValuePair("start_index", "0"
							+ ""));
					String url = Constant.SERVER + Constant.FRIENDS_APPS;
					String str = Helper.postData(url, postParams);

					Message msg = new Message();
					msg.obj = str;
					fHandler.sendMessage(msg);
				} catch (Exception e) {

				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

			}
		}.execute();

	}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	private void uploadApps() {

		final PackageManager pm = context.getPackageManager();
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
		String packageStr = "";
		for (int i = 0; i < apps.length; i++) {
			if (packageStr.equals("")) {
				packageStr = packageStr + apps[i];
			} else {
				packageStr = packageStr + "," + apps[i];
			}
		}

		String md5Hash = md5Digest(packageStr);
		SharedPreferences settings = context.getSharedPreferences(
				Constant.PREFS_NAME, 0);
		final String facebookUserID = settings
				.getString("facebook_user_id", "");
		String appPackageMD5 = settings.getString("apps_md5_" + facebookUserID,
				"");
		if (!appPackageMD5.equals(md5Hash)) {

			try {
				FileInputStream fin = context
						.openFileInput(Constant.PACKAGE_FILE_NAME);
				String ret = convertStreamToString(fin);
				fin.close();
				String[] alreadyUploadedPackages = ret.split(",");
				String[] currentPackages = packageStr.split(",");
				String diffPackageStr = "";
				for (int i = 0; i < currentPackages.length; i++) {
					boolean found = false;
					for (int j = 0; j < alreadyUploadedPackages.length; j++) {
						if (currentPackages[i]
								.equals(alreadyUploadedPackages[j])) {
							found = true;
							break;
						}
					}
					if (!found) {
						if (diffPackageStr.equals("")) {
							diffPackageStr = currentPackages[i];
						} else {
							diffPackageStr = diffPackageStr + ","
									+ currentPackages[i];
						}
					}
				}
				if (!diffPackageStr.equals("")) {
					upploadUserApps(diffPackageStr, "1");
					// generateNotification(diffPackageStr);
				}
			} catch (Exception ex) {
				upploadUserApps(packageStr, "1");
			}

			SharedPreferences.Editor editor = settings.edit();
			editor.putString("apps_md5_" + facebookUserID, md5Hash);
			editor.commit();
		} else {
			// generateNotification("No update");
		}
	}

	private void generateNotification(CharSequence from, String message) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra("share_my_apps", "1");
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		Notification notif = new Notification(R.drawable.ic_launcher, message,
				System.currentTimeMillis());
		notif.setLatestEventInfo(context, from, message, contentIntent);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, notif);
	}
	
	private void generateNotificationForNewApps(CharSequence from, String message) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra("type", "friend_app");
		notificationIntent.putExtra("title", "Friends");
		notificationIntent.putExtra("show_right_arrow", "1");
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		Notification notif = new Notification(R.drawable.ic_launcher, message,
				System.currentTimeMillis());
		notif.setLatestEventInfo(context, from, message, contentIntent);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(1, notif);
	}

	// Upload Apps if this is the first time
	public void upploadUserApps(final String packageStr, final String shared) {

		SharedPreferences settings = context.getSharedPreferences(
				Constant.PREFS_NAME, 0);
		final String facebookUserID = settings
				.getString("facebook_user_id", "");
		String appUploaded = settings.getString("apps_uploaded_"
				+ facebookUserID, "");

		if (appUploaded.equals("")) {
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
						postParams.add(new BasicNameValuePair("shared",
								shared));
						String url = Constant.SERVER + Constant.USER_APPS;
						Helper.postData(url, postParams);
						// generateNotification("Data upload successful");
					} catch (Exception e) {

					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {

				}
			}.execute();
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
}
