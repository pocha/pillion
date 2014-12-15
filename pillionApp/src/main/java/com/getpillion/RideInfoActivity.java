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
import android.widget.Toast;

import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.getpillion.models.Ride;
import com.getpillion.models.RideUserMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class RideInfoActivity extends ExtendMeSherlockWithMenuActivity {

	//private SlidingMenu menu = null;
    private Ride ride;
    private ProgressDialog progress;
    private TravellerAdapter adapter;
    private List<RideUserMapping> travellers = new ArrayList<RideUserMapping>();
    private Boolean isRideCreationSuccess;
    private int rideCreationStatus = Constant.CREATED;
    private RideUserMapping rideUserMapping = null;
    private int myRideStatus = Constant.CREATED;
    private boolean amIOwner = false;
    private AlertDialog alert;
    private Activity thisActivity;
    private Intent intent;

    @InjectView(R.id.from) TextView from;
    @InjectView(R.id.to) TextView to;
    @InjectView(R.id.status) TextView status;
    @InjectView(R.id.type) TextView type;
    @InjectView(R.id.vehicleInfo) LinearLayout vehicleInfo;
    @InjectView(R.id.vehicle) TextView vehicle;
    @InjectView(R.id.travellers) ListView travellersList;
    @OnItemClick(R.id.travellers) void onTravellerSelect(int position){
        Intent intent = new Intent(RideInfoActivity.this, UserProfileActivity.class);
        intent.putExtra("rideUserMappingId",travellers.get(position).getId());
        startActivity(intent);
    }
    @InjectView(R.id.primaryButton) Button primaryButton;
    @InjectView(R.id.primaryButtonMsg) TextView primaryButtonMsg;
    @InjectView(R.id.primaryButtonLayout) LinearLayout primaryButtonLayout;
    @InjectView(R.id.secondaryButton) Button secondaryButton;
    @InjectView(R.id.secButtonMsg) TextView secButtonMsg;
    @InjectView(R.id.secButtonLayout) LinearLayout secButtonLayout;

    @OnClick(R.id.primaryButton) void setPrimaryButtonListener(View v){

        if (amIOwner){
            if (ride.isOffered){

                switch (myRideStatus) {
                    case Constant.CREATED: //Schedule Ride primary button
                        intent = new Intent(RideInfoActivity.this, ScheduleRideActivity.class);
                        intent.putExtra("rideId",ride.getId());
                        intent.putExtra("type","updateRide");
                        startActivity(intent);
                        break;
                    case Constant.SCHEDULED: //Start Ride primary button
                        rideUserMapping.status = Constant.STARTED;
                        rideUserMapping.save();
                        intent = getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        finish();
                        break;
                    case Constant.STARTED:
                    case Constant.CANCELLED: //reschedule button
                        intent = new Intent(RideInfoActivity.this, ScheduleRideActivity.class);
                        intent.putExtra("rideId",ride.getId());
                        intent.putExtra("type","scheduleRide");
                        startActivity(intent);
                        break;
                }
            }
            else { //only cancel ride secondary button here - so nothing doing

            }
        }
        else {
            if (ride.isOffered){

                switch (myRideStatus) {
                    case Constant.CREATED: //Request Ride primary button
                        //request ride button
                        intent = new Intent(RideInfoActivity.this, RequestRideActivity.class);
                        intent.putExtra("rideId", ride.getId());
                        startActivity(intent);
                        break;
                    case Constant.REQUESTED:
                        //nothing
                        break;
                    case Constant.ACCEPTED: //CheckIn primary button
                        //checkin button
                        //update status in RouteUserMapping to Constant.CANCELLED. Do all below things in AsyncTask
                        rideUserMapping.status = Constant.CHECKED_IN;
                        rideUserMapping.save();
                        intent = new Intent(RideInfoActivity.this, RideInfoActivity.class);
                        intent.putExtra("rideId", ride.getId());
                        startActivity(intent);
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
            else { //all buttons are hidden here so nothing doing

            }
        }
    }
    @OnClick(R.id.secondaryButton) void setSecondaryButtonListener(View v){

        if (amIOwner){
            if (ride.isOffered){
                switch (myRideStatus) {
                    case Constant.CREATED: //nothing as primary button to schedule ride & sec is hidden
                        break;
                    case Constant.SCHEDULED: //modify ride button
                        intent = new Intent(RideInfoActivity.this,ScheduleRideActivity.class);
                        intent.putExtra("rideId", ride.getId());
                        intent.putExtra("type","updateRide");
                        startActivity(intent);
                        break;
                    case Constant.STARTED: //nothing

                        break;
                    case Constant.CANCELLED: //nothing

                        break;
                }
            }
            else { //edit ride button
                if (myRideStatus != Constant.CANCELLED) {
                    intent = new Intent(RideInfoActivity.this,ScheduleRideActivity.class);
                    intent.putExtra("rideId",ride.getId());
                    intent.putExtra("type","updateRide");
                    startActivity(intent);
                }
            }
        }
        else {
            if (ride.isOffered){
                switch (myRideStatus) {
                    case Constant.CREATED:
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
                                        rideUserMapping.status = Constant.CANCELLED;
                                        rideUserMapping.save();
                                        intent = getIntent();
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
            else { //everything hidden nothing doing

            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        travellers.clear();
        if (amIOwner) //show all users to owner
            travellers.addAll(
                    RideUserMapping.find(RideUserMapping.class, "ride_id=?",String.valueOf(ride.getId()))
            );
        else
            travellers.addAll(
                    RideUserMapping.find(RideUserMapping.class,
                        "ride_id=? AND (user_id = ? OR " +
                                "(status != ? AND status != ?))",
                        String.valueOf(ride.getId()), String.valueOf(sharedPref.getLong("userId",0L)),
                        String.valueOf(Constant.REQUESTED), String.valueOf(Constant.REJECTED)
                    ) //show user's entry whatever status it is, but dont show other pending status users to him
            );

        Log.d("RideInfoActivity", "traveller count " + travellers.size());
        adapter.notifyDataSetChanged(); //update traveller's status once the creator comes back to this screen
        Helper.setListViewHeightBasedOnChildren(travellersList);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_info);


        //getSupportActionBar().setHomeButtonEnabled(true);
        thisActivity = this;
        final Long rideId = getIntent().getExtras().getLong("rideId");
        Log.d("RideInfoActivity","Ride id " + rideId);
        ride = Ride.findById(Ride.class, rideId);



        //Fill data
        ButterKnife.inject(this);
        from.setText(ride.origin);
        to.setText(ride.dest);
        type.setText(ride.isOffered ? "Ride Offered" : "Seeking Ride");

        int ownerRideStatus = RideUserMapping.find(RideUserMapping.class, "ride_id =? AND is_owner = 1",
                        String.valueOf(ride.getId())
                    ).get(0).status;


        if (ride.isOffered) {
            if (ownerRideStatus == Constant.CANCELLED) {
                status.setText("The owner has cancelled the ride");
            } else if ( ownerRideStatus == Constant.STARTED && Helper.compareDate(ride.dateLong, new Date()) >= 0) {
                status.setText("The ride is in progress now");
            } else if (ride.dateLong != null) {
                if (Helper.compareDate(ride.dateLong, new Date()) >= 0) {
                    status.setText("SCHEDULED at " + ride.getAmPmTime() + " on " + Helper.niceDate(ride.dateLong));
                } else { // not Constant.SCHEDULED so must have happened in past
                    status.setText("Last ride happened at " + ride.getAmPmTime() + " on " + Helper.niceDate(ride.dateLong));
                }
            } else {// no information of ride present
                status.setText("Ride scheduled at " + ride.getAmPmTime() + " daily but the owner probably never 'started' the ride on the app in the past.");
            }
        }
        else {
            if (ownerRideStatus == Constant.CANCELLED) {
                status.setText("The seeker is no more seeking ride on this route");
            }
            else {
                status.setText("Seeking ride at " + ride.getAmPmTime());
            }
        }
        
        if(ride.vehicleNumber != null)
            vehicle.setText(ride.vehicleColor + " " + ride.vehicleModel + " - " + ride.vehicleNumber);
        else
            vehicleInfo.setVisibility(View.GONE);

        try {
            rideUserMapping = RideUserMapping.find(RideUserMapping.class, "ride_id =? AND user_id =?",
                    String.valueOf(ride.getId()),
                    String.valueOf(sharedPref.getLong("userId", 0L))
            ).get(0);
            //Toast.makeText(this,"global_id of this ride " + rideUserMapping.globalId, Toast.LENGTH_SHORT).show();
            myRideStatus = rideUserMapping.status;
            Toast.makeText(this,"status of this ride " + rideUserMapping.status, Toast.LENGTH_SHORT).show();
            amIOwner = rideUserMapping.isOwner;
        }
        catch (Exception e){ //user not associated with the ride
            Log.d("RideInfoActivity","Boss you are not part of this ride");
            //both myRideStatus & amIOwner stays the default value so nothing doing.
        }

        adapter = new TravellerAdapter(getApplicationContext(),travellers,sharedPref.getLong("userId",0L));
        travellersList.setAdapter(adapter);

        if (amIOwner) {
            if (ride.isOffered) { //I am owner & I am offering ride. Option to accept requests
                switch (myRideStatus) {
                    case Constant.CREATED:
                        secButtonLayout.setVisibility(View.GONE);

                        primaryButtonLayout.setVisibility(View.VISIBLE);
                        primaryButton.setText("Create Ride");
                        primaryButtonMsg.setText("Route created during app installation. Create ride by adding date & vehicle information.");
                        break;
                    case Constant.SCHEDULED: //show start ride button
                        secondaryButton.setText("Edit Details / Cancel Ride");
                        secButtonMsg.setText("");

                        primaryButtonLayout.setVisibility(View.VISIBLE);
                        primaryButton.setText("Start Ride");
                        primaryButtonMsg.setText("Starting ride will send an update to your fellow riders that you have STARTED.");
                        break;
                    case Constant.STARTED:
                        secButtonLayout.setVisibility(View.GONE);

                        primaryButtonLayout.setVisibility(View.VISIBLE);
                        primaryButton.setText("Create New Ride");
                        primaryButtonMsg.setText("You have STARTED the ride");
                        break;
                    case Constant.CANCELLED:
                        secButtonLayout.setVisibility(View.GONE);

                        primaryButtonLayout.setVisibility(View.VISIBLE);
                        primaryButton.setText("Create New Ride");
                        primaryButtonMsg.setText("You have CANCELLED the ride");
                        break;
                }
            }
            else { //I am owner & I am seeking ride. Option to cancel ride
                if (myRideStatus != Constant.CANCELLED) {
                    secondaryButton.setText("Edit Details / Cancel Ride");
                    secButtonMsg.setText("");
                    primaryButtonLayout.setVisibility(View.GONE);
                }
                else {//ride already cancelled - hide all buttons
                    primaryButtonLayout.setVisibility(View.GONE);
                    secButtonLayout.setVisibility(View.GONE);
                }
            }
        }
        else {
            if (ride.isOffered) { //I am not owner & ride is offered. Option to send request
                switch (myRideStatus) {
                    case Constant.CREATED:
                        secButtonLayout.setVisibility(View.GONE);
                        primaryButton.setText("Request Ride");
                        primaryButtonMsg.setText("You will be taken to payment page. Your money will get refunded if you/vehicle owner cancel your request or if the ride does not happen.");
                        primaryButtonLayout.setVisibility(View.VISIBLE);
                        break;
                    case Constant.REQUESTED:
                        secondaryButton.setText("Cancel Ride Request");
                        secButtonMsg.setText("You can cancel your request any time & your money will get refunded into your account.");
                        primaryButtonLayout.setVisibility(View.VISIBLE);
                        primaryButton.setEnabled(false);
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
                        primaryButton.setEnabled(false);
                        primaryButtonMsg.setText("You have CANCELLED the ride request");
                        break;
                    case Constant.REJECTED:
                        secButtonLayout.setVisibility(View.GONE);

                        primaryButtonLayout.setVisibility(View.VISIBLE);
                        primaryButton.setEnabled(false);
                        primaryButtonMsg.setText("Your ride request has been REJECTED by the vehicle owner. The money has been credited in your account.");
                        break;
                    case Constant.CHECKED_IN:
                        secButtonLayout.setVisibility(View.GONE);

                        primaryButtonLayout.setVisibility(View.VISIBLE);
                        primaryButton.setEnabled(false);
                        primaryButtonMsg.setText("You have checked in at the pickup point. Wait for the ride owner to turn-up.");
                        break;
                }
            }
            else { //I am not owner & ride is sought - nothing doing for now
                primaryButtonLayout.setVisibility(View.GONE);
                secButtonLayout.setVisibility(View.GONE);
            }
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



/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
*/


	
/*	public void sendRequestDialog(View v) {
		try {
			Bundle params = new Bundle();
			params.putString("message",
					"AppCovery helps you see Apps your friends are using");

			showDialogWithoutNotificationBar(params);
		} catch (Exception ex) {
			intent = new Intent().setClass(RouteInfoActivity.this,
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
			intent = new Intent().setClass(RouteInfoActivity.this,
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

		intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		intent.putExtra(Intent.EXTRA_SUBJECT, "Checkout AppCovery!");
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"Discover Apps through Friends.\n\nhttps://play.google.com/store/apps/details?id=com.appcovery.android.appcoveryapp\n\nAppCovery helps you see Apps your friends are using.");
		startActivity(Intent.createChooser(intent, "How do you want to share?"));

	}*/
	
}
