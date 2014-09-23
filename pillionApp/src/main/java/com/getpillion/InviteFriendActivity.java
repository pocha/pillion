package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.appcovery.android.appcoveryapp.R;
import com.getpillion.common.Constant;
import com.bugsense.trace.BugSenseHandler;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class InviteFriendActivity extends SherlockFragmentActivity {

	private SlidingMenu menu = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
		setContentView(R.layout.activity_invite_friend);

		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle("Find More Friends");
		
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.0f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		menu.setMenu(R.layout.menu);
		
		//publishNewsFeed(null);
	}
	
	
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
			Intent intent = new Intent().setClass(InviteFriendActivity.this,
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
			Intent intent = new Intent().setClass(InviteFriendActivity.this,
					MainActivity.class);
			startActivity(intent);
		}
	}

	public void showDialogWithoutNotificationBar(Bundle params) {
		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
				InviteFriendActivity.this, Session.getActiveSession(), params))
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
				InviteFriendActivity.this, Session.getActiveSession(), params))
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

}
