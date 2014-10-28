package com.getpillion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.getpillion.models.Route;
import com.getpillion.models.RouteUserMapping;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RouteInfoActivity extends ExtendMeSherlockWithMenuActivity {

	//private SlidingMenu menu = null;
    private Route route;
    private ProgressDialog progress;
    private TravellerAdapter adapter;
    private List<RouteUserMapping> travellers;
    private Boolean isRideCreationSuccess;
    private RouteUserMapping.Status rideCreationStatus = null;
    private RouteUserMapping myRouteStatus = null;
    private RouteUserMapping.Status rideStatus = null;
    private AlertDialog alert;
    private Activity thisActivity;

    @InjectView(R.id.from) TextView from;
    @InjectView(R.id.to) TextView to;
    @InjectView(R.id.status) TextView status;
    @InjectView(R.id.vehicleInfo) LinearLayout vehicleInfo;
    @InjectView(R.id.vehicle) TextView vehicle;
    @InjectView(R.id.travellers) ListView travellersList;
    @InjectView(R.id.primaryButton) Button primaryButton;
    @InjectView(R.id.primaryButtonMsg) TextView primaryButtonMsg;
    @InjectView(R.id.primaryButtonLayout) LinearLayout primaryButtonLayout;
    @InjectView(R.id.secondaryButton) Button secondaryButton;
    @InjectView(R.id.secButtonMsg) TextView secButtonMsg;
    @InjectView(R.id.secButtonLayout) LinearLayout secButtonLayout;

    @OnClick(R.id.primaryButton) void setPrimaryButtonListener(View v){

        if (route.isOffered){
            if (route.isMyRoute) {
                if (rideStatus == null) {
                    //TODO schedule ride activity
                }
                else {
                    switch (rideStatus) {
                        case scheduled:
                            //button to start ride
                            //TODO send data to server through AsyncTask & wait for completion before refreshing
                            //update status in RouteUserMapping to cancelled. Do all below things in AsyncTask
                            myRouteStatus.status = RouteUserMapping.Status.started;
                            myRouteStatus.save();
                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                            break;
                        case started:
                            //nothing
                        case cancelled:
                            //nothing
                    }
                }
            }
            else {
                if (rideStatus == null) {
                    //request ride button
                    Intent intent = new Intent(RouteInfoActivity.this, RequestRideActivity.class);
                    intent.putExtra("routeId",route.getId());
                    startActivity(intent);
                }
                else {
                    switch (rideStatus) {
                        case requested:
                            //nothing
                            break;
                        case accepted:
                            //checkin button
                            //TODO send data to server through AsyncTask & wait for completion before refreshing
                            //update status in RouteUserMapping to cancelled. Do all below things in AsyncTask
                            myRouteStatus.status = RouteUserMapping.Status.checkedIn;
                            myRouteStatus.save();
                            Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                            break;
                        case rejected:
                            //nothing
                            break;
                        case cancelled:
                            //nothing
                            break;
                        case checkedIn:
                            //nothing
                    }
                }
            }
        }
    }
    @OnClick(R.id.secondaryButton) void setSecondaryButtonListener(View v){
        if (route.isOffered){
            if (route.isMyRoute) {
                if (rideStatus == null) {
                    //nothing as schedule ride main button
                }
                else {
                    switch (rideStatus) {
                        case scheduled:
                            //button to cancel ride
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setMessage("Want to cancel the ride ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //TODO send data to server through AsyncTask & wait for completion before refreshing
                                            //update status in RouteUserMapping to cancelled. Do all below things in AsyncTask
                                            myRouteStatus.status = RouteUserMapping.Status.cancelled;
                                            myRouteStatus.save();
                                            Intent intent = getIntent();
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            break;
                        case started:
                            //nothing
                        case cancelled:
                            //nothing
                    }
                }
            }
            else {
                if (rideStatus == null) {
                    //nothing as request ride main button
                }
                else {
                    switch (rideStatus) {
                        case requested:
                        case accepted:
                            //cancel ride request button
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setMessage("Want to cancel the ride ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //update status in RouteUserMapping to cancelled
                                            myRouteStatus.status = RouteUserMapping.Status.cancelled;
                                            myRouteStatus.save();
                                            Intent intent = getIntent();
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                            break;
                        case rejected:
                            //nothing
                            break;
                        case cancelled:
                            //nothing
                            break;
                        case checkedIn:
                            //nothing
                    }
                }
            }
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.activity_route_info);

        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Route Info");
        thisActivity = this;
        final Long routeId = getIntent().getExtras().getLong("routeId");
        route = Route.findById(Route.class,routeId);

        //Fill data
        ButterKnife.inject(this);
        from.setText(route.origin);
        to.setText(route.dest);
        if (route.isScheduled) {
            status.setText("Scheduled at " + route.getAmPmTime() + " on " + Helper.niceDate(route.date));
        }
        else if (route.date != null ){ // not scheduled so must have happened in past
            status.setText("Last ride happened at " + route.getAmPmTime() + " on " + Helper.niceDate(route.date) );
        }
        else {// no information of ride present
            status.setText("The owner created the route but probably forgot to start his ride on the app. Hence no prior information of the ride available.");
        }
        if(route.vehicle != null)
            vehicle.setText(route.vehicle.color + " " + route.vehicle.model);
        else
            vehicleInfo.setVisibility(View.GONE);

        travellers = RouteUserMapping.find(RouteUserMapping.class,"route=?", String.valueOf(route.getId()) );
        Log.d("RouteInfoActivity", "traveller count " + travellers.size());
        adapter = new TravellerAdapter(getApplicationContext(),travellers);
        travellersList.setAdapter(adapter);
        Helper.setListViewHeightBasedOnChildren(travellersList);

        try {
            myRouteStatus = RouteUserMapping.find(RouteUserMapping.class,"route=? AND user=?",
                    String.valueOf(route.getId()), String.valueOf(sharedPref.getLong("userId",0L))).get(0);
            rideStatus = myRouteStatus.status;
        }
        catch (Exception e){
            //nothing doing. rideStatus stays null
        }

        if (route.isOffered) {

            if (route.isMyRoute) { //schedule, approve, start, cancel

                if (rideStatus == null) {
                    secButtonLayout.setVisibility(View.GONE);

                    primaryButton.setVisibility(View.VISIBLE);
                    primaryButton.setText("Schedule Ride");
                    primaryButtonMsg.setText("Starting the ride on this route shortly? Schedule the ride on this route.");
                }
                else {
                    switch (rideStatus) { //rideStatus will never be null here
                        case scheduled: //show start ride button
                            secondaryButton.setText("Cancel Ride");
                            secButtonMsg.setText("");

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setText("Start Ride");
                            primaryButtonMsg.setText("Starting ride will send an update to your fellow riders that you have started");
                        case started:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setVisibility(View.GONE);
                            primaryButtonMsg.setText("You have started the ride");
                            break;
                        case cancelled:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setVisibility(View.GONE);
                            primaryButtonMsg.setText("You have cancelled the ride");
                            break;
                    }
                }

            }
            else { //request, accepted/cancelled, cancel, checkIn

                if (rideStatus == null) {
                    secButtonLayout.setVisibility(View.GONE);
                    primaryButton.setText("Request Ride");
                    primaryButtonMsg.setText(secButtonMsg.getText().toString());
                    primaryButtonLayout.setVisibility(View.VISIBLE);
                }
                else {
                    switch (rideStatus) {
                        case requested:
                        case accepted:
                            //secondaryButton.setText("Cancel Ride");
                            //show checkin button
                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            break;
                        case cancelled:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setVisibility(View.GONE);
                            primaryButtonMsg.setText("You have cancelled the ride request");
                            break;
                        case rejected:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setVisibility(View.GONE);
                            primaryButtonMsg.setText("Your ride request has been rejected by the vehicle owner. The money has been credited in your account.");
                            break;
                        case checkedIn:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setVisibility(View.GONE);
                            primaryButtonMsg.setText("You have checked in at the pickup point. Wait for the ride owner to turn-up.");
                            break;
                    }
                }
            }
        }
        else {
            primaryButtonLayout.setVisibility(View.GONE);
            secButtonLayout.setVisibility(View.GONE);
        }




        if (getIntent().hasExtra("isRideCreationSuccess")) {
            rideCreationStatus = (RouteUserMapping.Status)getIntent().getSerializableExtra("rideCreationStatus");
            Log.d("RouteInfoActivity", "Booking Status " + rideCreationStatus.toString());

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            isRideCreationSuccess = getIntent().getBooleanExtra("isRideCreationSuccess",false);

            switch(rideCreationStatus){
                case requested:
                    if (isRideCreationSuccess) {
                        builder.setTitle("Your request has been sent.")
                                .setMessage("You can access this screen under 'My Rides' inside the top left menu. You can cancel the request anytime you like to before the driver starts the ride & the money will be refunded into your account.");
                    }
                    else {
                        builder.setTitle("Your request could not be sent")
                                .setMessage("Something unexpected happened while trying to process your request.");
                    }
                    break;
                case scheduled:
                    if (isRideCreationSuccess) {
                        builder.setTitle("Your ride has been scheduled")
                                .setMessage("You can access this screen under 'My Rides' inside the top left menu. You should hit 'start ride' button when you are starting the ride.");
                    }
                    else {
                        builder.setTitle("Your ride could not be scheduled")
                                .setMessage("Something unexpected happened while trying to process your request.");
                    }
            }

            builder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            alert = builder.create();
            alert.show();
        }

        //This isnt needed as we have the updated route data from the query in AllRoutesActivity. Hence commenting it out
        //getRouteData(routeId);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }



	
/*	public void sendRequestDialog(View v) {
		try {
			Bundle params = new Bundle();
			params.putString("message",
					"AppCovery helps you see Apps your friends are using");

			showDialogWithoutNotificationBar(params);
		} catch (Exception ex) {
			Intent intent = new Intent().setClass(RouteInfoActivity.this,
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
			Intent intent = new Intent().setClass(RouteInfoActivity.this,
					MainActivity.class);
			startActivity(intent);
		}
	}

	public void showDialogWithoutNotificationBar(Bundle params) {
		WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
				RouteInfoActivity.this, Session.getActiveSession(), params))
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
							final String requestId = values
									.getString("request");
							if (requestId != null) {
								Toast.makeText(getApplicationContext(),
										"Request sent ",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Request cancelled", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}

				}).build();
		requestsDialog.show();
	}

	public void showFeedDialogWithoutNotificationBar(Bundle params) {
		WebDialog requestsDialog = (new WebDialog.FeedDialogBuilder(
				RouteInfoActivity.this, Session.getActiveSession(), params))
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
							final String requestId = values
									.getString("request");
							if (requestId != null) {
								Toast.makeText(getApplicationContext(),
										"Request sent " ,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Request cancelled", Toast.LENGTH_SHORT)
										.show();
							}
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

	}*/
	
}
