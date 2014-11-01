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
import com.getpillion.models.Ride;
import com.getpillion.models.RideUserMapping;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class RideInfoActivity extends ExtendMeSherlockWithMenuActivity {

	//private SlidingMenu menu = null;
    private Ride ride;
    private ProgressDialog progress;
    private TravellerAdapter adapter;
    private List<RideUserMapping> travellers;
    private Boolean isRideCreationSuccess;
    private int rideCreationStatus = Constant.NO_STATUS;
    private RideUserMapping myRouteStatus = null;
    private int rideStatus = Constant.NO_STATUS;
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

        if (ride.route.isOffered){
            if (ride.route.owner.getId() == sharedPref.getLong("userId",0L)) {

                switch (rideStatus) {
                    case Constant.NO_STATUS:
                        //TODO schedule ride activity
                        break;
                    case Constant.SCHEDULED:
                        //button to start ride
                        //TODO send data to server through AsyncTask & wait for completion before refreshing
                        //update status in RouteUserMapping to Constant.CANCELLED. Do all below things in AsyncTask
                        myRouteStatus.status = Constant.STARTED;
                        myRouteStatus.save();
                        Intent intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                        break;
                    case Constant.STARTED:
                        //nothing
                    case Constant.CANCELLED:
                        //nothing
                }

            }
            else {

                    switch (rideStatus) {
                        case Constant.NO_STATUS:
                            //request ride button
                            Intent intent = new Intent(RideInfoActivity.this, RequestRideActivity.class);
                            intent.putExtra("rideId", ride.getId());
                            startActivity(intent);
                            break;
                        case Constant.REQUESTED:
                            //nothing
                            break;
                        case Constant.ACCEPTED:
                            //checkin button
                            //TODO send data to server through AsyncTask & wait for completion before refreshing
                            //update status in RouteUserMapping to Constant.CANCELLED. Do all below things in AsyncTask
                            myRouteStatus.status = Constant.CHECKED_IN;
                            myRouteStatus.save();
                            startActivity(new Intent(RideInfoActivity.this, RideInfoActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                            finish();
                            break;
                        case Constant.REJECTED:
                            //nothing
                            break;
                        case Constant.CANCELLED:
                            //nothing
                            break;
                        case Constant.CHECKED_IN:
                            //nothing
                    }

            }
        }
    }
    @OnClick(R.id.secondaryButton) void setSecondaryButtonListener(View v){
        if (ride.route.isOffered){
            if (ride.route.owner.getId() == sharedPref.getLong("userId",0L)) {

                    switch (rideStatus) {
                        case Constant.NO_STATUS:
                            //nothing as schedule ride main button
                            break;
                        case Constant.SCHEDULED:
                            //button to cancel ride
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setMessage("Want to cancel the ride ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //TODO send data to server through AsyncTask & wait for completion before refreshing
                                            //update status in RouteUserMapping to Constant.CANCELLED. Do all below things in AsyncTask
                                            myRouteStatus.status = Constant.CANCELLED;
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
                        case Constant.STARTED:
                            //nothing
                        case Constant.CANCELLED:
                            //nothing
                    }

            }
            else {

                    switch (rideStatus) {
                        case Constant.NO_STATUS:
                            //nothing as request ride main button
                            break;
                        case Constant.REQUESTED:
                        case Constant.ACCEPTED:
                            //cancel ride request button
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setMessage("Want to cancel the ride ?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //update status in RouteUserMapping to Constant.CANCELLED
                                            myRouteStatus.status = Constant.CANCELLED;
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
                        case Constant.REJECTED:
                            //nothing
                            break;
                        case Constant.CANCELLED:
                            //nothing
                            break;
                        case Constant.CHECKED_IN:
                            //nothing
                    }

            }
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.activity_ride_info);

        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Route Info");
        thisActivity = this;
        final Long rideId = getIntent().getExtras().getLong("rideId");
        ride = Ride.findById(Ride.class, rideId);

        //Fill data
        ButterKnife.inject(this);
        from.setText(ride.route.origin);
        to.setText(ride.route.dest);
        if (ride.isScheduled) {
            status.setText("SCHEDULED at " + ride.getAmPmTime() + " on " + Helper.niceDate(ride.date));
        }
        else if (ride.date != null ){ // not Constant.SCHEDULED so must have happened in past
            status.setText("Last ride happened at " + ride.getAmPmTime() + " on " + Helper.niceDate(ride.date) );
        }
        else {// no information of ride present
            status.setText("The owner created the route but probably forgot to start his ride on the app. Hence no prior information of the ride available.");
        }
        if(ride.vehicle != null)
            vehicle.setText(ride.vehicle.color + " " + ride.vehicle.model);
        else
            vehicleInfo.setVisibility(View.GONE);

        travellers = RideUserMapping.find(RideUserMapping.class, "ride=?", String.valueOf(ride.getId()));
        Log.d("RouteInfoActivity", "traveller count " + travellers.size());
        adapter = new TravellerAdapter(getApplicationContext(),travellers);
        travellersList.setAdapter(adapter);
        Helper.setListViewHeightBasedOnChildren(travellersList);

        try {
            myRouteStatus = RideUserMapping.find(RideUserMapping.class, "ride=? AND user=?",
                    String.valueOf(ride.getId()), String.valueOf(sharedPref.getLong("userId", 0L))).get(0);
            rideStatus = myRouteStatus.status;
        }
        catch (Exception e){
            //nothing doing. rideStatus stays null
        }

        if (ride.route.isOffered) {

            if (ride.route.owner.getId() == sharedPref.getLong("userId",0L)) { //schedule, approve, start, cancel

                    switch (rideStatus) {
                        case Constant.NO_STATUS:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButton.setVisibility(View.VISIBLE);
                            primaryButton.setText("Schedule Ride");
                            primaryButtonMsg.setText("Starting the ride on this route shortly? Schedule the ride on this route.");
                            break;
                        case Constant.SCHEDULED: //show start ride button
                            secondaryButton.setText("Cancel Ride");
                            secButtonMsg.setText("");

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setText("Start Ride");
                            primaryButtonMsg.setText("Starting ride will send an update to your fellow riders that you have STARTED.");
                        case Constant.STARTED:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setText("Start Ride");
                            primaryButton.setBackgroundColor(getResources().getColor(R.color.BottomButtonDisabled));
                            primaryButtonMsg.setText("You have STARTED the ride");
                            break;
                        case Constant.CANCELLED:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setText("Start Ride");
                            primaryButton.setBackgroundColor(getResources().getColor(R.color.BottomButtonDisabled));
                            primaryButtonMsg.setText("You have CANCELLED the ride");
                            break;
                    }


            }
            else { //request, Constant.ACCEPTED/Constant.CANCELLED, cancel, checkIn

                    switch (rideStatus) {
                        case Constant.NO_STATUS:
                            secButtonLayout.setVisibility(View.GONE);
                            primaryButton.setText("Request Ride");
                            primaryButtonMsg.setText("You will be taken to payment page. Your money will get refunded if you/vehicle owner cancel your request or if the ride does not happen.");
                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            break;
                        case Constant.REQUESTED:
                            secondaryButton.setText("Cancel Ride Request");
                            secButtonMsg.setText("You can cancel your request any time & your money will get refunded into your account.");
                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setBackgroundColor(getResources().getColor(R.color.BottomButtonDisabled));
                            primaryButtonMsg.setText("The ride owner has not yet approved your request");
                            break;
                        case Constant.ACCEPTED:
                            secondaryButton.setText("Cancel Ride Request");
                            secButtonMsg.setText("You can cancel your request any time & your money will get refunded into your account.");
                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            break;
                        case Constant.CANCELLED:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setBackgroundColor(getResources().getColor(R.color.BottomButtonDisabled));
                            primaryButtonMsg.setText("You have CANCELLED the ride request");
                            break;
                        case Constant.REJECTED:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setBackgroundColor(getResources().getColor(R.color.BottomButtonDisabled));
                            primaryButtonMsg.setText("Your ride request has been REJECTED by the vehicle owner. The money has been credited in your account.");
                            break;
                        case Constant.CHECKED_IN:
                            secButtonLayout.setVisibility(View.GONE);

                            primaryButtonLayout.setVisibility(View.VISIBLE);
                            primaryButton.setBackgroundColor(getResources().getColor(R.color.BottomButtonDisabled));
                            primaryButtonMsg.setText("You have checked in at the pickup point. Wait for the ride owner to turn-up.");
                            break;
                    }

            }
        }
        else {
            primaryButtonLayout.setVisibility(View.GONE);
            secButtonLayout.setVisibility(View.GONE);
        }




        if (getIntent().hasExtra("isRideCreationSuccess")) {
            rideCreationStatus = getIntent().getIntExtra("rideCreationStatus", 0);
            Log.d("RouteInfoActivity", "Booking Status " + rideCreationStatus);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            isRideCreationSuccess = getIntent().getBooleanExtra("isRideCreationSuccess",false);

            switch(rideCreationStatus){
                case Constant.REQUESTED:
                    if (isRideCreationSuccess) {
                        builder.setTitle("Your request has been sent.")
                                .setMessage("You can access this screen under 'My Rides' inside the top left menu. You can cancel the request anytime you like to before the driver starts the ride & the money will be refunded into your account.");
                    }
                    else {
                        builder.setTitle("Your request could not be sent")
                                .setMessage("Something unexpected happened while trying to process your request.");
                    }
                    break;
                case Constant.SCHEDULED:
                    if (isRideCreationSuccess) {
                        builder.setTitle("Your ride has been Constant.SCHEDULED")
                                .setMessage("You can access this screen under 'My Rides' inside the top left menu. You should hit 'start ride' button when you are starting the ride.");
                    }
                    else {
                        builder.setTitle("Your ride could not be Constant.SCHEDULED")
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
        //getRouteData(rideId);

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
										"Request Constant.CANCELLED", Toast.LENGTH_SHORT)
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
										"Request Constant.CANCELLED", Toast.LENGTH_SHORT)
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
										"Request Constant.CANCELLED", Toast.LENGTH_SHORT)
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
										"Request Constant.CANCELLED", Toast.LENGTH_SHORT)
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
