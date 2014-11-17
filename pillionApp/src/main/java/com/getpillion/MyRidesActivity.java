package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.getpillion.common.Helper;
import com.getpillion.models.Ride;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MyRidesActivity extends ExtendMeSherlockWithMenuActivity implements ActionBar.TabListener {

    private ArrayList<Ride> rides = new ArrayList<Ride>();
    private RideAdapter adapter;
    private ActionBar.Tab upcomingRidesTab, pastRidesTab, selectedTab;


    @InjectView(R.id.noRoutesFound)
    TextView noRoutesFound;
    //@InjectView(R.id.loading) TextView loading;
    @InjectView(R.id.scheduleRide)
    Button scheduleRide;
    @OnClick(R.id.scheduleRide) void takeUserToMyRoutes(View v){
        Intent intent = new Intent(MyRidesActivity.this,MyRoutesActivity.class);
        startActivity(intent);
    }
    @InjectView(R.id.listView) ListView mListView;

    @OnItemClick(R.id.listView) void onOfferedRouteItemClick(int position){
        Intent intent = new Intent(MyRidesActivity.this, RideInfoActivity.class);
        intent.putExtra("rideId", rides.get(position).getId());
        startActivity(intent);
    }

    @InjectView(R.id.filterBottomBar)
    LinearLayout filterBottomBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.route_list);
        ButterKnife.inject(this);

        filterBottomBar.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        rides = new ArrayList<Ride>();

        adapter = new RideAdapter(this, rides);
        mListView.setEmptyView(noRoutesFound);
        noRoutesFound.setText("No Rides found");
        mListView.setAdapter(adapter);

        upcomingRidesTab = actionBar.newTab().setText("Upcoming Rides");
        upcomingRidesTab.setTag("upcoming");
        upcomingRidesTab.setTabListener(this);
        actionBar.addTab(upcomingRidesTab);

        pastRidesTab = actionBar.newTab().setText("Past Rides");
        pastRidesTab.setTag("past");
        pastRidesTab.setTabListener(this);
        actionBar.addTab(pastRidesTab);

        showRides();
    }

    public void showRides() {

        Long today = 0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d = sdf.parse(sdf.format(new Date()));
            Log.d("MyRidesActivity","Today's date - " + d.toString());
            today = d.getTime();
        }catch (Exception e){}

        Long now = 0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date d = sdf.parse(sdf.format(new Date()));
            Log.d("MyRidesActivity","Time now - " + d.toString());
            now = d.getTime();
        }catch (Exception e){}

        rides.clear();
        if (selectedTab.getTag() == "upcoming") {
            rides.addAll(
                    Ride.findWithQuery(Ride.class,
                            "SELECT Ride.* FROM Ride JOIN Route ON Ride.route = Route.id " +
                                    "WHERE Route.owner = ? AND " +
                                    "Ride.date_long IS NULL OR " +
                                    "Ride.date_long > ? OR (Ride.date_long = ? AND Ride.time_long > ?)",
                            String.valueOf(sharedPref.getLong("userId", 0L)),
                            String.valueOf(today),
                            String.valueOf(today),String.valueOf(now)
                    )
            );
        }
        else {
            rides.addAll(
                    Ride.findWithQuery(Ride.class,
                            "SELECT Ride.* FROM Ride JOIN Route ON Ride.route = Route.id " +
                                    "WHERE Route.owner = ? AND " +
                                    "Ride.date_long < ? OR (Ride.date_long = ? AND Ride.time_long <= ?)",
                            String.valueOf(sharedPref.getLong("userId", 0L)),
                            String.valueOf(today),
                            String.valueOf(today),String.valueOf(now)
                    )
            );
        }
        adapter.notifyDataSetChanged();
        Log.d("AllRoutesActivity", "Data count in adapter - " + adapter.getCount());
        Helper.setListViewHeightBasedOnChildren(mListView);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        selectedTab = tab;
        showRides();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }


}