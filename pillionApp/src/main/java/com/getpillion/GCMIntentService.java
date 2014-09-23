package com.getpillion;

import static com.getpillion.gcm.CommonUtilities.SENDER_ID;
import static com.getpillion.gcm.CommonUtilities.displayMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appcovery.android.appcoveryapp.R;
import com.getpillion.gcm.ServerUtilities;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

	public GCMIntentService() {
		super(SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {
		/*
		Toast toast = Toast.makeText(context, "GCM after registertion "
				+ registrationId, Toast.LENGTH_LONG);
		toast.show();
		final SharedPreferences settings = getSharedPreferences(
				Constant.PREF_NAME, 0);
		String email = settings.getString("email", "");
		*/
		ServerUtilities.register(context, MainActivity.facebookUserID, registrationId);
	}

	/**
	 * Method called on device un registred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		displayMessage(context, getString(R.string.gcm_unregistered));
		ServerUtilities.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		try {
			String message = intent.getExtras().getString("message");
			String assetID = intent.getExtras().getString("asset_id");
			String groupID = intent.getExtras().getString("group_id");
			String type = intent.getExtras().getString("type");
			String datetime = intent.getExtras().getString("datetime");
			
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(datetime);
			long timeInMills = date.getTime();
			
			Date now = new Date();
			long currentTimeInMills = now.getTime();
			
			long diff = currentTimeInMills - timeInMills;
			diff = (diff / (1000*60));
			
			if ( diff > 10 ) {
				//Notification generated 10 mins before - ignore it
			} else {
				generateNotification(context, message, assetID, groupID, type);
			}
		} catch (Exception ex) {
			
		}
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
		// notifies user
		generateNotification(context, message, "", "", "");
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {
		//Log.i(TAG, "Received error: " + errorId);
		//displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		//Log.i(TAG, "Received recoverable error: " + errorId);
		//displayMessage(context,
		//		getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	public static void generateNotification(Context context, String message,
			String assetID, String groupID, String type) {
		

	}

}