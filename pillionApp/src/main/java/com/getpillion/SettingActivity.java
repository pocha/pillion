package com.getpillion;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SettingActivity extends SherlockPreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	SlidingMenu menu = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(getApplicationContext(),
				Constant.BUGSENSE_API_KEY);
		addPreferencesFromResource(R.xml.preferences);
		getSupportActionBar().setHomeButtonEnabled(true);
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent().setClass(SettingActivity.this,
				AllRidesActivity.class);
		intent.putExtra("type", "friend_app");
		intent.putExtra("title", "Friends");
		startActivity(intent);
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	
}