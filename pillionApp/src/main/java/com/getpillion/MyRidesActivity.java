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

import java.util.ArrayList;

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

        adapter = new RideAdapter(this, rides, "MyRidesActivity");
        mListView.setEmptyView(noRoutesFound);
        noRoutesFound.setText("No Rides found");
        mListView.setAdapter(adapter);

        upcomingRidesTab = actionBar.newTab().setText("You Offering");
        upcomingRidesTab.setTag("you_offering");
        upcomingRidesTab.setTabListener(this);
        actionBar.addTab(upcomingRidesTab);

        pastRidesTab = actionBar.newTab().setText("You Seeking");
        pastRidesTab.setTag("you_seeking");
        pastRidesTab.setTabListener(this);
        actionBar.addTab(pastRidesTab);

        showRides();
    }

    public void showRides() {
        Long userId = sharedPref.getLong("userId",0L);
        rides.clear();
        rides.addAll(Ride.myRides(userId, selectedTab.getTag().toString()));
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