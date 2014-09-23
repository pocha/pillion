package com.getpillion.gcm;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	// Google project id
    public static final String SENDER_ID = "528993769112";
    public static final String SERVER_URL = "http://testapi.qples.com/api/version2/register_gcm";
    /**
     * Tag used on log messages.
     */
    public static final String TAG = "FriendsApp";
 
    public static final String DISPLAY_MESSAGE_ACTION =
            "com.appcovery.android.appcoveryapp.DISPLAY_MESSAGE";
 
    public static final String EXTRA_MESSAGE = "message";
    
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
