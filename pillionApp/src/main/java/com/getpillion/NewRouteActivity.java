package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;
import com.getpillion.common.Helper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class NewRouteActivity extends SherlockFragmentActivity {

	//private SlidingMenu menu = null;
    @InjectView(R.id.office) EditText office;
    @InjectView(R.id.home) EditText home;
    @InjectView(R.id.homeStartTime) EditText homeStartTime;
    @InjectView(R.id.homeStartTimeHint) TextView homeStartTimeHint;
    @InjectView(R.id.officeStartTime) EditText officeStartTime;
    @InjectView(R.id.officeStartTimeHint) TextView officeStartTimeHint;
    @InjectView(R.id.driveToWorkYes) RadioButton driveToWorkYes;
    @InjectView(R.id.driveToWorkNo) RadioButton driveToWorkNo;

    @OnTextChanged(R.id.homeStartTime) void onTextChangedHome(CharSequence text) {
        Helper.autoCompleteTime(text,homeStartTime, homeStartTimeHint);
        //Log.d("NewRouteActivity","inside ontextchanged");
    }
    @OnFocusChange(R.id.homeStartTime) void onFocusChangeHome(boolean isFocussed) {
        Helper.pushCursorToEnd(isFocussed,homeStartTime);
    }
    @OnTextChanged(R.id.officeStartTime) void onTextChangedOffice(CharSequence text) {
        Helper.autoCompleteTime(text, officeStartTime, officeStartTimeHint);
    }
    @OnFocusChange(R.id.officeStartTime) void onFocusChangeOffice(boolean isFocussed) {
        Helper.pushCursorToEnd(isFocussed,officeStartTime);
    }

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
		setContentView(R.layout.activity_new_route);
        ButterKnife.inject(this);

		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setTitle("Home <-> Office Route");
		
		//publishNewsFeed(null);
	}

    public void onSubmit(){
        Intent intent = new Intent(NewRouteActivity.this, AllRoutesActivity.class);
        startActivity(intent);
        finish();
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
										"Request cancelled", Toast.LENGTH_SHORT)
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
										"Request cancelled", Toast.LENGTH_SHORT)
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
