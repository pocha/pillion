package com.getpillion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.getpillion.common.Constant;
import com.getpillion.common.PlaceSelectFragment;
import com.getpillion.common.TimePickerFragment;
import com.getpillion.models.Ride;
import com.getpillion.models.User;

import java.sql.Time;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class NewRouteActivityOnStart extends SherlockFragmentActivity  {


	//private SlidingMenu menu = null;
    @InjectView(R.id.office) EditText office;
    @InjectView(R.id.home) EditText home;


    @OnTouch(R.id.home) boolean setHomeLocation(View v, MotionEvent event){
        return setLocation(R.id.home,event);
    }
    @OnTouch(R.id.office) boolean setOfficeLocation(View v, MotionEvent event){
        return setLocation(R.id.office,event);
    }

    private boolean setLocation(int viewId, MotionEvent event){
        if (event.getAction() != MotionEvent.ACTION_UP)
            return true;
        //launch Dialog
        PlaceSelectFragment p = PlaceSelectFragment.newInstance("some title");
        Bundle bundle = new Bundle();
        bundle.putInt("populateMeInParent",viewId);
        p.setArguments(bundle);
        p.show(getSupportFragmentManager(),"not sure what this tag suppose to do");
        return true; //indicating this function consumed the event & it will not be propagated further

    }



    @OnClick(R.id.saveRoute) void onSubmit(View v){
        //To-Do validate data

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                Constant.PREFS_NAME, 0);
        Editor sharedPrefEditor = sharedPref.edit();
        User user = User.findById(User.class,sharedPref.getLong("userId",0L));

        //sharedPrefEditor.commit();

        Log.d("NewRouteActivity","dumping intent extra offerRide " + getIntent().getBooleanExtra("offerRide",false));
        Log.d("NewRouteActivity","dumping intent extra requestRide " + getIntent().getBooleanExtra("requestRide",false));

        Time homeStartTime = ((TimePickerFragment)getSupportFragmentManager().findFragmentById(R.id.homeStartTime)).time;
        Time officeStartTime = ((TimePickerFragment)getSupportFragmentManager().findFragmentById(R.id.officeStartTime)).time;


        if (getIntent().getBooleanExtra("offerRide",false)) {
            new Ride(home.getText().toString(), office.getText().toString(),
                    homeStartTime,
                    true, user);
            new Ride(office.getText().toString(), home.getText().toString(),
                    officeStartTime,
                    true, user);
        }

        if (getIntent().getBooleanExtra("requestRide",false)) {
            new Ride(home.getText().toString(), office.getText().toString(),
                    homeStartTime,
                    false, user);
            new Ride(office.getText().toString(), home.getText().toString(),
                    officeStartTime,
                    false, user);
        }


        //send position & route data to the server through AsyncTask/SyncAdapter, get respective Ids & update the objects

        sharedPrefEditor.putBoolean("appInitialized", true);
        sharedPrefEditor.commit();

        Intent intent = new Intent(NewRouteActivityOnStart.this, AllRidesActivity.class);
        //clear top stack so that position cant go back
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_new_route_on_start);
        ButterKnife.inject(this);

		getSupportActionBar().setHomeButtonEnabled(false);



		//publishNewsFeed(null);
	}



/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			if (item.getItemId() == android.R.id.home) {
				menu.toggle();
				return true;
			} else {
				return true;
			}
		} catch (Exception ex) {
			return true;
		}
	}

	public void sendRequestDialog(View v) {
		try {
			Bundle params = new Bundle();
			params.putString("message",
					"AppCovery helps you see Apps your friends are using");

			showDialogWithoutNotificationBar(params);
		} catch (Exception ex) {
			Intent intent = new Intent().setClass(NewRouteActivity.this,
					MainActivity.class);
			startActivity(intent);
		}
	}

	public void publishNewsFeed(View v) {
		try {
			Bundle params = new Bundle();

			params.putString("name", "Checkout AppCovery");
			params.putString("caption",
					"Discover Apps through Friends");
			params.putString("description",
					"AppCovery helps you see Apps your friends are using");
			params.putString("picture",
					"http://s3.amazonaws.com/friendsapps/appcovery.png");
			params.putString("link",
					"https://play.google.com/store/apps/details?id=com.appcovery.android.appcoveryapp");

			showFeedDialogWithoutNotificationBar(params);
		} catch (Exception ex) {
			Intent intent = new Intent().setClass(NewRouteActivity.this,
					MainActivity.class);
			startActivity(intent);
		}
	}

	public void showDialogWithoutNotificationBar(Bundle params) {
		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
				NewRouteActivity.this, Session.getActiveSession(), params))
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
							gotoAfterInvite();
						}
					}

				}).build();
		requestsDialog.show();
	}
	
	public void gotoAfterInvite() {
		//Intent intent = new Intent ( InviteFriendActivity.this, AfterInviteActivity.class);
		//startActivity(intent);
		EasyTracker.getTracker().sendEvent("contact", "invite", MainActivity.facebookUserID,0L);
		Toast.makeText(getApplicationContext(),
				"Successfully Posted", Toast.LENGTH_SHORT)
				.show();
	}

	public void showFeedDialogWithoutNotificationBar(Bundle params) {
		WebDialog requestsDialog = (new WebDialog.FeedDialogBuilder(
				NewRouteActivity.this, Session.getActiveSession(), params))
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
							gotoAfterInvite();
						}
					}

				}).build();
		requestsDialog.show();
	}

	public void addOthers(View v) {

		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intent.EXTRA_SUBJECT, "Checkout AppCovery!");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"Discover Apps through Friends.\n\nhttps://play.google.com/store/apps/details?id=com.appcovery.android.appcoveryapp\n\nAppCovery helps you see Apps your friends are using.");
		startActivity(Intent.createChooser(intent, "How do you want to share?"));

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
*/

}
