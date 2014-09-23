package com.getpillion;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.getpillion.common.Constant;
import com.bugsense.trace.BugSenseHandler;
import com.google.analytics.tracking.android.EasyTracker;



public class MainActivity extends SherlockFragmentActivity {

	private MainFragment mainFragment;
	public static String facebookUserID;
	
	
	public static String shareMyApps = "";
	public static String newApps = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
	    getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setTitle("Login");
		
		try {
			shareMyApps = getIntent().getExtras().getString("share_my_apps", "");
		} catch (Exception ex) { }
		catch (NoSuchMethodError e) { }
		
		try {
			newApps = getIntent().getExtras().getString("new_apps", "");
		} catch (Exception ex) { }
		catch (NoSuchMethodError e) { }

	    if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        mainFragment = new MainFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, mainFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        mainFragment = (MainFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }
	    
	    
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

	
	
	
	
	
	
}
