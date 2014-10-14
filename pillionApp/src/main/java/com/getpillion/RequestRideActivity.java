package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.actionbarsherlock.view.MenuItem;


public class RequestRideActivity extends ExtendMeSherlockWithMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ride);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Request Ride");

        Button requestButton = (Button) findViewById(R.id.requestRide);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RequestRideActivity.this, RouteInfoActivity.class);
                //Ride ride = new Ride(getIntent().getExtras().getLong("routeId"),Ride.Status.requested);
                intent.putExtra("routeId",getIntent().getExtras().getLong("routeId"));
                intent.putExtra("bookingStatus",true);
                intent.putExtra("bookingError","");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //this should remove the previous RouteInfoActivity from stack
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}
