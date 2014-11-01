package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;
import com.getpillion.models.Ride;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MyRidesActivity extends ExtendMeSherlockWithMenuActivity {

    private ArrayList<Ride> oRoutes = new ArrayList<Ride>();
    private ArrayList<Ride> rRoutes = new ArrayList<Ride>();

    @InjectView(R.id.offeredRoutes)
    ListView offeredRoutes;
    @InjectView(R.id.emptyOfferedRoutes)
    TextView emptyOfferedRoutes;
    @InjectView(R.id.requestedRoutes)
    ListView requestedRoutes;
    @InjectView(R.id.emptyRequestedRoutes)
    TextView emptyRequestedRoutes;

    @InjectView(R.id.offeringRideLabel) TextView offeringRideLabel;
    @InjectView(R.id.requestingRideLabel) TextView requestingRideLabel;

    @InjectView(R.id.addRoute)
    Button addRoute;

    @OnItemClick(R.id.offeredRoutes) void onOfferedRouteItemClick(int position){
        showRouteDetail(offeredRoutes,position);
    }
    @OnItemClick(R.id.requestedRoutes) void onRequestedRouteItemClick(int position){
        showRouteDetail(requestedRoutes,position);
    }

    private void showRouteDetail(ListView parent, int position){
        Intent intent = new Intent(MyRidesActivity.this, RideInfoActivity.class);
        intent.putExtra("rideId",((Ride)parent.getAdapter().getItem(position)).getId());
        startActivity(intent);
    }

    @OnClick(R.id.addRoute) void fireNewRouteIntent(View v){
        Intent intent = new Intent(MyRidesActivity.this, MyRouteInfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(),
                Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.activity_my_routes);
        ButterKnife.inject(this);

        offeringRideLabel.setText("Upcoming Rides");
        emptyOfferedRoutes.setText("No upcoming Rides");
        requestingRideLabel.setText("Past Rides");
        emptyRequestedRoutes.setText("No past Rides");
        addRoute.setText("Schedule New Ride");

        Long today = 0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            today = sdf.parse(sdf.format(new Date())).getTime();
        }catch (Exception e){}

        Long now = 0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            now = sdf.parse(sdf.format(new Date())).getTime();
        }catch (Exception e){}

        oRoutes.addAll(
                Ride.findWithQuery(Ride.class,
                        "SELECT * FROM Ride JOIN Route ON Ride.route = Route.id " +
                                "WHERE Route.owner = ? AND Ride.date >= ? AND Ride.timestamp > ?",
                        String.valueOf(sharedPref.getLong("userId", 0L)),
                        String.valueOf(today),
                        String.valueOf(now)
                )
        );
        rRoutes.addAll(
                Ride.findWithQuery(Ride.class,
                        "SELECT * FROM Ride JOIN Route ON Ride.route = Route.id " +
                                "WHERE Route.owner = ? AND Ride.date <= ? OR Ride.timestamp <= ?",
                        String.valueOf(sharedPref.getLong("userId", 0L)),
                        String.valueOf(today),
                        String.valueOf(now)
                )
        );


        offeredRoutes.setEmptyView(emptyOfferedRoutes);
        requestedRoutes.setEmptyView(emptyRequestedRoutes);

        offeredRoutes.setAdapter(new RideAdapter(getApplicationContext(), oRoutes));
        requestedRoutes.setAdapter(new RideAdapter(getApplicationContext(), rRoutes));
    }




}