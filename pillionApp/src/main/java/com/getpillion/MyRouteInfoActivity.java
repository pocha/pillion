package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuItem;
import com.getpillion.common.Helper;
import com.getpillion.common.PlaceSelectFragment;
import com.getpillion.models.Route;
import com.getpillion.models.User;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;

public class MyRouteInfoActivity extends ExtendMeSherlockWithMenuActivity {

    private Route route = null;
    private boolean userHasVehicle = false;

    @NotEmpty(messageId = R.string.non_empty_field)
    @InjectView(R.id.from) EditText from;

    @NotEmpty(messageId = R.string.non_empty_field)
    @InjectView(R.id.to) EditText to;


    @InjectView(R.id.isOffered)
    CheckBox isOffered;
    @InjectView(R.id.saveRoute)
    Button saveRoute;
    @InjectView(R.id.deleteRoute) Button deleteRoute;
    @InjectView(R.id.scheduleRide) Button scheduleRide;
    @InjectView(R.id.primaryButtonLayout)
    LinearLayout primaryButtonLayout;

    @OnClick(R.id.saveRoute) void saveRoute(View v){

        if (Helper.fieldsHaveErrors(this))
            return;

        Route newRoute = new Route();
        newRoute.origin = from.getText().toString();
        newRoute.dest = to.getText().toString();
        newRoute.isOffered = isOffered.isChecked();
        newRoute.owner = User.findById(User.class, sharedPref.getLong("userId", 0L));

        if (route == null){
            newRoute.save();
            Intent intent = new Intent(MyRouteInfoActivity.this,ScheduleRideActivity.class);
            intent.putExtra("routeId", newRoute.getId());
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"Route saved. Why not schedule a Ride on the route.",Toast.LENGTH_LONG);
            return;
        }
        else {
            if(route.update(newRoute))
                route.save();
        }
        Helper.returnControlToCallingActivity(this);
    }

    @OnClick(R.id.deleteRoute) void deleteRoute(View v){
        route.delete();
        Helper.returnControlToCallingActivity(this);
    }

    @OnClick(R.id.scheduleRide) void scheduleRide(View v){
        Intent intent = new Intent(MyRouteInfoActivity.this,ScheduleRideActivity.class);
        intent.putExtra("routeId", route.getId());
        startActivity(intent);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_route_info);
        ButterKnife.inject(this);


        if (!getIntent().hasExtra("routeId")){
            //hide schedule & delete route buttons as this is new Route view
            primaryButtonLayout.setVisibility(View.GONE);
            deleteRoute.setVisibility(View.GONE);
        }
        else { //edit route view
            route = Route.findById(Route.class, getIntent().getExtras().getLong("routeId"));
            //populate data
            from.setText(route.origin);
            to.setText(route.dest);
            isOffered.setChecked(route.isOffered);
            if (!route.isOffered) { //hide schedule route layout
                primaryButtonLayout.setVisibility(View.GONE);
            }
        }

    }

    @OnTouch(R.id.from) boolean setHomeLocation(View v, MotionEvent event){
        return setLocation(R.id.from,event);
    }
    @OnTouch(R.id.to) boolean setOfficeLocation(View v, MotionEvent event){
        return setLocation(R.id.to,event);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }


}