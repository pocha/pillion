package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.getpillion.models.Ride;
import com.getpillion.models.Route;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyRidesActivity extends ExtendMeSherlockWithMenuActivity {

    private ArrayList<Ride> rides;
    private ListView upcomingRidesView;
    private ListView pastRidesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(),
                Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.activity_my_rides);

        //load all rides as they are all locally stored so no AsyncTask is needed
        rides = Ride.getAll();
        Date today = new Date();
        Log.d("MyRidesActivity","today " + today);
        Time now = Time.valueOf((new SimpleDateFormat("HH:mm:ss")).format(today));
        Log.d("MyRidesActivity","now " + now);

        final ArrayList<Route> upcomingRides = new ArrayList<Route>();
        final ArrayList<Route> pastRides = new ArrayList<Route>();
        for (int i=0; i < rides.size(); i++) {
            Ride ride = rides.get(i);
            Log.d("MyRidesActivity","ride date " + ride.route.date + " comparison value " + Helper.compareDate(ride.route.date, today));
            Log.d("MyRidesActivity","ride time " + ride.route.time + " comparison value " + ride.route.time.getTime() + " " + now.getTime());
            if (Helper.compareDate(ride.route.date,today) > -1 && ride.route.time.getTime() > now.getTime()) {//date & time is ahead
                upcomingRides.add(ride.route);
            }
            else {
                pastRides.add(ride.route);
            }
        }

        //load adapters & attach empty view
        upcomingRidesView = (ListView)findViewById(R.id.upcomingRides);
        upcomingRidesView.setEmptyView(findViewById(R.id.emptyUpcomingRides));
        upcomingRidesView.setAdapter(new RouteAdapter(getApplicationContext(),upcomingRides));
        Helper.setListViewHeightBasedOnChildren(upcomingRidesView);

        pastRidesView = (ListView)findViewById(R.id.pastRides);
        pastRidesView.setEmptyView(findViewById(R.id.emptyPastRides));
        pastRidesView.setAdapter(new RouteAdapter(getApplicationContext(),pastRides));
        Helper.setListViewHeightBasedOnChildren(pastRidesView);

        upcomingRidesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MyRidesActivity.this, RouteInfoActivity.class);
                intent.putExtra("routeId", upcomingRides.get(position).globalId);
                startActivity(intent);
            }
        });

        pastRidesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MyRidesActivity.this, RouteInfoActivity.class);
                intent.putExtra("routeId", pastRides.get(position).globalId);
                startActivity(intent);
            }
        });


    }


}