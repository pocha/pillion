package com.getpillion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.getpillion.common.Constant;
import com.getpillion.models.Route;
import com.getpillion.models.User;

import java.sql.Time;
import java.util.ArrayList;

public class RouteInfoActivity extends ExtendMeSherlockWithMenuActivity {

	//private SlidingMenu menu = null;
    private Route route;
    private ProgressDialog progress;
    private TravellerAdapter adapter;
    private ArrayList<User> travellers;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
		setContentView(R.layout.activity_route_info);

		//getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle("Route Info");

        Long routeId = getIntent().getExtras().getLong("routeId");
        getRouteData(routeId);

        travellers = new ArrayList<User>();
        adapter = new TravellerAdapter(getApplicationContext(),travellers);
        ((ListView)findViewById(R.id.travellers)).setAdapter(adapter);

        Button requestButton = (Button) findViewById(R.id.requestRide);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteInfoActivity.this, AllRoutesActivity.class);
                startActivity(intent);
            }
        });
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
                        route = new Route("Brigade Gardenia, J P Nagar 7th Phase, RBI Layout", "Ecospace, Outer Ring Road, Kadbisnehalli", Time.valueOf("8:00:00"));
                    } catch (Exception e) {

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    ((TextView)findViewById(R.id.from)).setText(route.from);
                    ((TextView)findViewById(R.id.to)).setText(route.to);

                    if (route.isScheduled) {
                        ((TextView)findViewById(R.id.status)).setText("Scheduled at " + route.time + " on " + route.date);
                    }
                    else if (route.date != null ){ // not scheduled so must have happened in past
                        ((TextView)findViewById(R.id.status)).setText("Last ride happened at " + route.time + " on " + route.date );
                    }
                    else {// no information of ride present
                        ((TextView)findViewById(R.id.status)).setText("No prior information of this ride happening is available.");
                    }

                    if (route.vehicle != null ){
                        ((TextView)findViewById(R.id.vehicle)).setText(route.vehicle.color + " " + route.vehicle.model);
                    }
                    else {
                        ((LinearLayout)findViewById(R.id.vehicleInfo)).setVisibility(View.GONE);
                    }
                    //travellers.clear();
                    travellers.addAll(route.users);
                    adapter.notifyDataSetChanged();

                    progress.dismiss();
                }
            }.execute();
        } catch (Exception ex) {
        }
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
