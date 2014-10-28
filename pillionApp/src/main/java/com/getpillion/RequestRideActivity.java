package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.getpillion.common.Constant;
import com.getpillion.models.Route;
import com.getpillion.models.RouteUserMapping;
import com.getpillion.models.User;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class RequestRideActivity extends ExtendMeSherlockWithMenuActivity {
    private Route route = null;

    @OnClick(R.id.requestRide) void redirectBackToRouteInfoActivity(View V){
        route = Route.findById(Route.class, getIntent().getExtras().getLong("routeId"));
        User user = User.findById(User.class,sharedPref.getLong("userId",0L));
        Log.d("RequestRideActivity","dumping route id - " + route.getId());
        Log.d("RequestRideActivity","dumping user id - " + user.getId());

        //TODO send request to the server
        RouteUserMapping routeUserMapping = RouteUserMapping.findOrCreate(route,user,false);
        routeUserMapping.status = Constant.REQUESTED;
        routeUserMapping.save();

        Intent intent = new Intent(RequestRideActivity.this, RouteInfoActivity.class);
        intent.putExtra("routeId", route.getId());
        intent.putExtra("isRideCreationSuccess",true);
        intent.putExtra("rideCreationStatus", Constant.REQUESTED);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //this should remove the previous RouteInfoActivity from stack
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ride);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request Ride");
        ButterKnife.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
