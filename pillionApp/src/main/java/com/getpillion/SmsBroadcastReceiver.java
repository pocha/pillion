/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.getpillion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.getpillion.common.Constant;
import com.getpillion.models.User;


/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Log.d("SmsBroadcastReceiver","inside SMSBroadcastReceiver");
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constant.PREFS_NAME, 0);
        String randomHash = sharedPref.getString("UnconfirmedPhoneNoHash",null);
        if (randomHash == null)
            return;

        Bundle pudsBundle = intent.getExtras();
        Object[] pdus = (Object[]) pudsBundle.get("pdus");
        SmsMessage messages = SmsMessage.createFromPdu((byte[]) pdus[0]);
        Log.i("SmsBroadcastReceiver",  messages.getMessageBody());
        if(messages.getMessageBody().contains(randomHash)) {
            //store number
            User user = User.findById(User.class, sharedPref.getLong("userId", 0L));
            user.phone = sharedPref.getString("UnconfirmedPhoneNo",null);
            user.save();
            Editor sharedPrefEditor = sharedPref.edit();
            sharedPrefEditor.putString("ConfirmedPhoneNo",user.phone);
            sharedPrefEditor.commit();
            abortBroadcast();
        }
    }
}