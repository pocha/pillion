/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.getpillion.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.demach.konotor.Konotor;
import com.getpillion.R;
import com.getpillion.RideInfoActivity;
import com.getpillion.common.Constant;
import com.getpillion.models.Ride;
import com.getpillion.models.RideUserMapping;
import com.getpillion.models.User;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import java.util.Random;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";



    @Override
    protected void onHandleIntent(Intent intent)  {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                //sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                //sendNotification("Deleted messages on server: " + extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*for (int i = 0; i < 5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());*/

                Log.d("GcmIntentService","Got data from upstream " + extras.toString());

                if(Konotor.getInstance(getApplicationContext()).isKonotorMessage(intent))
                {
                    Log.d("Konotor","Got konotor message");
                    Konotor.getInstance(getApplicationContext()).handleGcmOnMessage(intent);
                    return;
                }

                String simpleClassName = extras.getString("model_name");
                String json = extras.getString("json");
                Log.d("GcmIntentService","json received for update - " + json + " for " + simpleClassName);
                Gson gson = new Gson();
                Intent targetIntent = null;
                String msg = null;
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                        Constant.PREFS_NAME, 0);


                if (simpleClassName.equals("Ride")) { //check if ride timing or vehicle info got updated
                    try {
                        Ride updatedRide = gson.fromJson(json, Ride.class);
                        Ride ride = Ride.find(Ride.class,"global_id = ?",String.valueOf(updatedRide.globalId)).get(0);
                        Ride.updateFromUpstream(updatedRide);
                        targetIntent = new Intent(this, RideInfoActivity.class);
                        targetIntent.putExtra("rideId",ride.getId());
                        msg = "Ride creator has updated the ride";
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                if (simpleClassName.equals("User")) {
                    User.updateFromUpstream(gson.fromJson(json, User.class));
                    //lets not send notification for user info update
                }
                if (simpleClassName.equals("RideUserMapping")) { //this is important
                    RideUserMapping rideUserMapping = gson.fromJson(json, RideUserMapping.class);
                    //we need the ride
                    Ride ride = Ride.find(Ride.class,"global_id = ?", String.valueOf(rideUserMapping.rideId)).get(0);
                    targetIntent = new Intent(this, RideInfoActivity.class);
                    targetIntent.putExtra("rideId", ride.getId());

                    RideUserMapping myEntry = RideUserMapping.find(RideUserMapping.class,"ride_id = ? AND user_id = ?",
                                                                    String.valueOf(ride.getId()),
                                                                    String.valueOf(sharedPref.getLong("userId",0L))
                                                           ).get(0);

                    RideUserMapping updated = RideUserMapping.updateFromUpstream(ride, rideUserMapping);

                    //broadcast only for ride requester. The flow will not go into the subsequent if because of the user's status
                    if (updated.userId == sharedPref.getLong("userId", 0L)) {
                        switch (updated.status) {
                            case Constant.ACCEPTED: //show to requester
                                msg = "Your ride request is approved";
                                break;
                            case Constant.REJECTED: //show to the requester - request rejected
                                msg = "Sorry, your request has been rejected";
                                break;
                        }
                    }

                    //broadcast for everyone
                    if (myEntry.status != Constant.REQUESTED &&
                            myEntry.status != Constant.REJECTED &&
                            myEntry.status != Constant.CANCELLED)
                    {
                        switch (updated.status) {
                            case Constant.REQUESTED: //show only to owner
                                if (myEntry.isOwner)
                                    msg = "New ride request";
                                break;
                            case Constant.CANCELLED:
                                if (updated.isOwner) {
                                    msg = "Owner has cancelled the ride"; //for other active users
                                } else {
                                    msg = "One traveller backed out"; //for owner
                                }
                                break;
                            case Constant.CHECKED_IN: //show to all - {traveller} has reached his start point
                                msg = "One traveller has reached his pickup point";
                                break;
                            case Constant.STARTED: //show to all - ride has been started
                                msg = "Ride has started.";
                                break;
                        }

                    }
                }
                if (simpleClassName.equals("WorkHistory")) { //lets just leave this
                }

                Log.d("GcmIntentService","message to the user - " + msg);

                if (msg != null)
                    sendNotification(targetIntent,msg);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Intent targetIntent, String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                targetIntent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_white)
                        .setContentTitle(msg)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(
                                                msg
                                        )
                        )
                        .setPriority(2)
                        .setContentText(greetingMessages[new Random().nextInt(greetingMessages.length)]);

        mBuilder.setContentIntent(contentIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_ALL;
        //notification.defaults |= Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private static String[] greetingMessages = {"Ta da","Knock Knock","Heya","Hola buddy","Yo dude",
            "Smells like new request", "Excuse me","Vandu Nimisha","Enna Rascala (kidding :-|)",
            "Dayavittu kshamishi", "Yatrigan kripya dhyan de"
            };
}