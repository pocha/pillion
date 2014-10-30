package com.getpillion;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.getpillion.common.Constant;
import com.getpillion.common.PlaceSelectFragment;
import com.getpillion.models.Route;
import com.getpillion.models.RouteUserMapping;
import com.getpillion.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;


public class RequestRideActivity extends ExtendMeSherlockWithMenuActivity {
    private Route route = null;

    @OnClick(R.id.requestRide) void redirectBackToRouteInfoActivity(View V){
        //store pickup & drop point in sharedPref so that it can be populated on next load
        sharedPrefEditor.putString("pickUpLocation",pickUp.getText().toString());
        sharedPrefEditor.putString("dropLocation", drop.getText().toString());
        sharedPrefEditor.putString("seekerDistance", distance.getText().toString());
        sharedPrefEditor.commit();

        route = Route.findById(Route.class, getIntent().getExtras().getLong("routeId"));
        User user = User.findById(User.class,sharedPref.getLong("userId",0L));
        Log.d("RequestRideActivity","dumping route id - " + route.getId());
        Log.d("RequestRideActivity","dumping position id - " + user.getId());
        //TODO send request to the server
        RouteUserMapping routeUserMapping = RouteUserMapping.findOrCreate(route,user,false);
        routeUserMapping.status = Constant.REQUESTED;
        routeUserMapping.save();

        Intent intent = new Intent(RequestRideActivity.this, MyProfileActivity.class);
        intent.putExtra("routeId", route.getId());
        intent.putExtra("isRideCreationSuccess",true);
        intent.putExtra("rideCreationStatus", Constant.REQUESTED);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //this should remove the previous RouteInfoActivity from stack
        startActivity(intent);
        finish();
    }

    @InjectView(R.id.pickUp) TextView pickUp;
    @InjectView(R.id.drop) TextView drop;
    @InjectView(R.id.distance) TextView distance;
    @InjectView(R.id.cost) TextView cost;

    @OnTouch(R.id.pickUp) boolean setHomeLocation(View v, MotionEvent event){
        return setLocation(R.id.pickUp,event);
    }
    @OnTouch(R.id.drop) boolean setOfficeLocation(View v, MotionEvent event){
        return setLocation(R.id.drop,event);
    }
    @OnTextChanged(R.id.pickUp) void triggerDistanceCalcPickUp(CharSequence text){
        //if (focused)
            triggerDistanceCalc();
    }
    @OnTextChanged(R.id.drop) void triggerDistanceCalcDrop(CharSequence text){
        //if (focused)
            triggerDistanceCalc();
    }

    private AsyncTask asyncTask = null;
    private void triggerDistanceCalc(){
        if (pickUp.getText().toString().isEmpty() || drop.getText().toString().isEmpty())
            return;
        if (asyncTask != null)
            asyncTask.cancel(true); //cancel any previous asyncTask running

        distance.setText("calculating ...");
        asyncTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    Thread.sleep(1000); //ensures that this asyncTask does not do http request if position is still typing as it would get CANCELLED
                    if (!this.isCancelled()) {
                        return getDistanceFromGoogle(pickUp.getText().toString(), drop.getText().toString());
                    }
                } catch (Exception e) {
                    Log.e("ashish","exception",e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                if (!this.isCancelled()) {
                    try {
                        Log.d(LOG_TAG, "Got data from google - " + result.toString());
                        distance.setText(result.getString("text"));
                        cost.setText( "INR " + (Math.round(result.getInt("value") / 1000) * 3) );
                    }
                    catch (JSONException e){
                        Log.e(LOG_TAG,"JSON Error in onPostExecute ",e);
                    }
                }
            }
        }.execute();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ride);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request Ride");
        ButterKnife.inject(this);

        //populate pickup & drop point from shared pref (saved on previous load)
        if (sharedPref.getString("pickUpLocation",null) != null)
            pickUp.setText(sharedPref.getString("pickUpLocation",null));
        if (sharedPref.getString("dropLocation",null) != null)
            drop.setText(sharedPref.getString("dropLocation",null));
        if (sharedPref.getString("seekerDistance",null) != null)
            distance.setText(sharedPref.getString("seekerDistance",null));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private static final String LOG_TAG = "PlaceSelectFragment";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api";
    private static final String TYPE_AUTOCOMPLETE = "/distancematrix";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyBc2jwTqRGdQaVfAaOEueX8hYO7FK_za4k";

    private JSONObject getDistanceFromGoogle(String origin, String destination) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            //sb.append("&components=country:in");
            sb.append("&origins=" + URLEncoder.encode(origin, "utf8"));
            sb.append("&destinations=" + URLEncoder.encode(destination, "utf8"));


            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        //Log.d(LOG_TAG,"json result from server - " + jsonResults.toString());
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            return jsonObj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return null;
    }

}
