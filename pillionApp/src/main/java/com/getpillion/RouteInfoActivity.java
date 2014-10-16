package com.getpillion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.getpillion.models.Ride;
import com.getpillion.models.Route;
import com.getpillion.models.RouteUserMapping;
import com.getpillion.models.User;

import java.sql.Time;
import java.util.List;

public class RouteInfoActivity extends ExtendMeSherlockWithMenuActivity {

	//private SlidingMenu menu = null;
    private Route route;
    private ProgressDialog progress;
    private TravellerAdapter adapter;
    private List<RouteUserMapping> travellers;
    private Boolean bookingStatus = null;
    private Ride.Status rideStatus = null;
    private AlertDialog alert;
    private Activity thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.activity_route_info);

        //getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Route Info");

        if (getIntent().hasExtra("bookingStatus")) {
            bookingStatus = getIntent().getExtras().getBoolean("bookingStatus");
            Log.d("RouteInfoActivity", "Booking Status " + bookingStatus);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Your request has been sent.")
                    .setMessage("You can access this screen under 'My Rides' inside the top left menu. You can cancel the request anytime you like to before the driver starts the ride & the money will be refunded into your account.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            alert = builder.create();
        }

        thisActivity = this;
        final Long routeId = getIntent().getExtras().getLong("routeId");
        getRouteData(routeId);


    }

    private void getRouteData(final Long routeId){
        try {
            progress = ProgressDialog.show(RouteInfoActivity.this,"",
                    "Loading Route Info. Please wait..", true, false);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {

                        /*ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
                        postParams.add(new BasicNameValuePair("routeId", routeId.toString()));
                        String url = Constant.SERVER + Constant.USER_APP_VIEW;
                        Helper.postData(url, postParams);*/
                        Thread.sleep(3000);
                        route = new Route("Brigade Gardenia, J P Nagar 7th Phase, RBI Layout", "Ecospace, Outer Ring Road, Kadbisnehalli", Time.valueOf("8:00:00"), true, User.returnDummyUser());
                        rideStatus = Ride.getRandomStatus();
                        Log.d("RouteInfoActivity","rideStatus value - " + rideStatus);
                    } catch (Exception e) {

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    ((TextView)findViewById(R.id.from)).setText(route.origin);
                    ((TextView)findViewById(R.id.to)).setText(route.dest);

                    if (route.isScheduled) {
                        ((TextView)findViewById(R.id.status)).setText("Scheduled at " + route.getAmPmTime() + " on " + Helper.niceDate(route.date));
                    }
                    else if (route.date != null ){ // not scheduled so must have happened in past
                        ((TextView)findViewById(R.id.status)).setText("Last ride happened at " + route.getAmPmTime() + " on " + Helper.niceDate(route.date) );
                    }
                    else {// no information of ride present
                        ((TextView)findViewById(R.id.status)).setText("The owner created the route but probably forgot to start his ride on the app. Hence no prior information of the ride available.");
                    }

                    if (route.vehicle != null ){
                        //((TextView)findViewById(R.id.vehicle)).setText(route.vehicle.color + " " + route.vehicle.model);
                    }
                    else {
                        ((LinearLayout)findViewById(R.id.vehicleInfo)).setVisibility(View.GONE);
                    }
                    travellers.clear();
                    travellers = RouteUserMapping.find(RouteUserMapping.class,"route=?", String.valueOf(route.getId()) );
                    Log.d("RouteInfoActivity", "traveller count " + travellers.size());
                    adapter = new TravellerAdapter(getApplicationContext(),travellers);
                    ListView lv =  (ListView)findViewById(R.id.travellers);
                    lv.setAdapter(adapter);

                    Helper.setListViewHeightBasedOnChildren(lv);

                    Button requestButton = (Button) findViewById(R.id.requestRide);
                    TextView requestRideHelperMessage = (TextView) findViewById(R.id.requestRideHelperMessage);
                    LinearLayout checkInButtonContainer = (LinearLayout) findViewById(R.id.checkInButtonContainer);

                    if (rideStatus == null) {
                        requestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(RouteInfoActivity.this, RequestRideActivity.class);
                                intent.putExtra("routeId", routeId);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        switch (rideStatus) {
                            case requested:
                            case accepted:
                                requestButton.setText("Cancel Ride Request");
                                requestRideHelperMessage.setVisibility(View.GONE);
                                requestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                                        builder.setMessage("Want to cancel the ride ?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //To-do send data to server in AsyncTask & wrap the below code in onPostExecute
                                                        Intent intent = new Intent(RouteInfoActivity.this, RouteInfoActivity.class);
                                                        intent.putExtra("routeId", routeId);
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
                                    }
                                });

                                //show checkin button
                                checkInButtonContainer.setVisibility(View.VISIBLE);
                                break;
                            case cancelled:
                                requestButton.setVisibility(View.GONE);
                                requestRideHelperMessage.setText("You have cancelled your request");
                                break;
                            case rejected:
                                requestButton.setVisibility(View.GONE);
                                requestRideHelperMessage.setText("Your request has been rejected");
                        }
                    }

                    progress.dismiss();

                    if (bookingStatus != null) {
                        //show alert dialog once data is populated
                        alert.show();
                    }
                }
            }.execute();
        } catch (Exception ex) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }



	
	public void sendRequestDialog(View v) {
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

	}
	
}
