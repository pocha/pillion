package com.getpillion;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.getpillion.common.PlaceSelectFragment;
import com.getpillion.models.Route;
import com.getpillion.models.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;

public class NewRouteActivity extends SherlockFragmentActivity  {


	//private SlidingMenu menu = null;
    @InjectView(R.id.office) EditText office;
    @InjectView(R.id.home) EditText home;
    @InjectView(R.id.homeStartTime)
    EditText homeStartTime;
    @InjectView(R.id.officeStartTime) EditText officeStartTime;

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

    @OnTouch(R.id.homeStartTime) boolean setHomeStartTime(View v, MotionEvent event) {
        setTime(homeStartTime,event);
        return true;
    }

    @OnTouch(R.id.officeStartTime) boolean setOfficeStartTime(View v, MotionEvent event){
        setTime(officeStartTime,event);
        return true;
    }

    private Builder timeDialog;
    private TimePicker timePicker;

    private void setTime(final EditText clickedView, MotionEvent event){
        if (event.getAction() != MotionEvent.ACTION_UP)
            return;

        timePicker = new TimePicker(this);
        timePicker.setIs24HourView(false);
        Log.d("TimePicker",clickedView.getText().toString());
        Matcher matcher = Pattern.compile("^(\\d+):(\\d+)(A|P)M$").matcher(clickedView.getText().toString());
        matcher.find();
        Log.d("TimePicker","3rd group value " + matcher.group(3));
        timePicker.setCurrentHour( (matcher.group(3).equals("P")) ? Integer.parseInt(matcher.group(1)) + 12 : Integer.parseInt(matcher.group(1)) );
        timePicker.setCurrentMinute(Integer.parseInt(matcher.group(2)));
        
        timeDialog = new AlertDialog.Builder(this)
                .setTitle("Select Time")
                .setMessage("Touch on hour/minute field to launch keyboard")
                .setPositiveButton(android.R.string.ok, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("Picker", timePicker.getCurrentHour() + ":"
                                + timePicker.getCurrentMinute());
                        int hour = timePicker.getCurrentHour();
                        int minute = timePicker.getCurrentMinute();
                        if (hour > 12) {
                            clickedView.setText((hour - 12) + ":" + ((minute < 10) ? "0" + minute : minute) + "PM");
                        } else {
                            clickedView.setText(hour + ":" + ((minute < 10) ? "0" + minute : minute) + "AM");
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Log.d("Picker", "Cancelled!");
                            }
                        });
        timeDialog.setView(timePicker).show();
    }

    @OnClick(R.id.saveRoute) void onSubmit(View v){
        //To-Do validate data

        //check if position exists else create new position
        User user = new User();
        user.save();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                Constant.PREFS_NAME, 0);
        Editor sharedPrefEditor = sharedPref.edit();
        sharedPrefEditor.putLong("userId",user.getId());
        //sharedPrefEditor.commit();

        Log.d("NewRouteActivity","dumping intent extra offerRide " + getIntent().getBooleanExtra("offerRide",false));
        Log.d("NewRouteActivity","dumping intent extra requestRide " + getIntent().getBooleanExtra("requestRide",false));


        if (getIntent().getBooleanExtra("offerRide",false)) {
            new Route(home.getText().toString(), office.getText().toString(),
                    Helper.formatAmPmTimetoSqlTime(homeStartTime.getText().toString()),
                    true, user);
            new Route(office.getText().toString(), home.getText().toString(),
                    Helper.formatAmPmTimetoSqlTime(officeStartTime.getText().toString()),
                    true, user);
        }

        if (getIntent().getBooleanExtra("requestRide",false)) {
            new Route(home.getText().toString(), office.getText().toString(),
                    Helper.formatAmPmTimetoSqlTime(homeStartTime.getText().toString()),
                    false, user);
            new Route(office.getText().toString(), home.getText().toString(),
                    Helper.formatAmPmTimetoSqlTime(officeStartTime.getText().toString()),
                    false, user);
        }


        //send position & route data to the server through AsyncTask/SyncAdapter, get respective Ids & update the objects

        sharedPrefEditor.putBoolean("appInitialized", true);
        sharedPrefEditor.commit();

        Intent intent = new Intent(NewRouteActivity.this, AllRoutesActivity.class);
        //clear top stack so that position cant go back
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
		setContentView(R.layout.activity_new_route);
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
