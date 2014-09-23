package com.getpillion;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootAlarm extends BroadcastReceiver {

	AlarmManager am;
	Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		setRepeatingAlarm();
	}
	
	public void setRepeatingAlarm() {
		Intent intent = new Intent(context, TimeAlarm.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				(2 * 60 * 60 * 1000), pendingIntent);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				pendingIntent);
	}

}
