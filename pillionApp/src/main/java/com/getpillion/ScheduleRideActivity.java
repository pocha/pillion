package com.getpillion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getpillion.common.Constant;
import com.getpillion.common.DatePickerFragment;
import com.getpillion.common.Helper;
import com.getpillion.common.TimePickerFragment;
import com.getpillion.models.Ride;
import com.getpillion.models.RideUserMapping;
import com.getpillion.models.Route;
import com.getpillion.models.User;
import com.getpillion.models.Vehicle;

import java.sql.Time;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import eu.inmite.android.lib.validations.form.annotations.NotEmpty;

public class ScheduleRideActivity extends ExtendMeSherlockWithMenuActivity  {

    public Date userDate;
    private Time userTime;

    private Ride ride = null;
    private Route route = null;

    @InjectView(R.id.profileView)
    LinearLayout profileView;
    @OnClick(R.id.profileView) void loadMyProfile(View v){
        Intent intent = new Intent(ScheduleRideActivity.this, MyProfileActivity.class);
        startActivity(intent);
    }

    @InjectView(R.id.updateRide)
    Button updateRide;
    @InjectView(R.id.deleteRide)
    Button deleteRide;
    private ScheduleRideActivity thisActivity;
    @InjectView(R.id.scheduleRide)
    Button scheduleRide;
    private RideUserMapping rideUserMapping = null;
    @InjectView(R.id.primaryButtonLayout)
    LinearLayout primaryButtonLayout;

    @NotEmpty(messageId = R.string.non_empty_field)
    @InjectView(R.id.color) TextView vehicleColor;
    @NotEmpty(messageId = R.string.non_empty_field)
    @InjectView(R.id.model) TextView vehicleModel;
    @NotEmpty(messageId = R.string.non_empty_field)
    @InjectView(R.id.number) TextView vehicleNumber;



    @Override
    public void onStart(){
        super.onStart();
        User user = User.findById(User.class, sharedPref.getLong("userId",0L));
        if (user.name == null){
            ((TextView)profileView.findViewById(R.id.name)).setText("You (profile missing)");
            ((TextView)profileView.findViewById(R.id.title)).setText("Owner");
        }
        else {
            ((TextView)profileView.findViewById(R.id.name)).setText(user.name);
            ((TextView)profileView.findViewById(R.id.title)).setText("Owner");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule_ride);
        ButterKnife.inject(this);

        //populate vehicle info from shared pref (saved on previous load)
        if (sharedPref.getString("vehicleModel",null) != null)
            vehicleModel.setText(sharedPref.getString("vehicleModel",null));
        if (sharedPref.getString("vehicleColor",null) != null)
            vehicleColor.setText(sharedPref.getString("vehicleColor",null));
        if (sharedPref.getString("vehicleNumber",null) != null)
            vehicleNumber.setText(sharedPref.getString("vehicleNumber",null));


        if (getIntent().hasExtra("rideId")) { //edit screen
            ride = Ride.findById(Ride.class,getIntent().getExtras().getLong("rideId",0L));
            User me = User.findById(User.class,sharedPref.getLong("userId",0L));
            route = Route.findOrCreate(ride.origin, ride.dest, ride.distance, ride.isOffered, me);
            //set date & time from the ride
            if (ride.dateLong != null)
                ((DatePickerFragment) getSupportFragmentManager().findFragmentById(R.id.datePicker)).setDate(ride.dateLong);
            ((TimePickerFragment) getSupportFragmentManager().findFragmentById(R.id.timePicker)).setTime(ride.timeLong);

            if (getIntent().getExtras().getString("type").equals("updateRide")) {
                updateRide.setVisibility(View.VISIBLE);
                deleteRide.setVisibility(View.VISIBLE);
                primaryButtonLayout.setVisibility(View.GONE);
            }
            else {
                //nothing doing as update & delete are hidden by default
            }
        }
        else {
            route = Route.findById(Route.class, getIntent().getExtras().getLong("routeId"));
        }
        thisActivity = this;
    }

    public void saveRide(boolean isUpdate){
        if (Helper.fieldsHaveErrors(this))
            return;
        //create or update Ride
        DatePickerFragment d = (DatePickerFragment) getSupportFragmentManager().findFragmentById(R.id.datePicker);
        userDate = d.date;
        TimePickerFragment t = (TimePickerFragment) getSupportFragmentManager().findFragmentById(R.id.timePicker);
        userTime = t.time;

        User user = User.findById(User.class, sharedPref.getLong("userId",0L));
        Vehicle vehicle = new Vehicle(vehicleModel.getText().toString(),
                                        vehicleColor.getText().toString(),
                                        vehicleNumber.getText().toString());
        //store pickup & drop point in sharedPref so that it can be populated on next load
        sharedPrefEditor.putString("vehicleModel", vehicle.model);
        sharedPrefEditor.putString("vehicleColor", vehicle.color);
        sharedPrefEditor.putString("vehicleNumber", vehicle.number);
        sharedPrefEditor.commit();

        if (isUpdate) {
            Ride newRide = new Ride(route,userDate,userTime,vehicle,user);
            boolean isRideUpdated = ride.update(newRide);

            if (isRideUpdated) {
                ride.save();
                Toast.makeText(this,"Ride updated",Toast.LENGTH_LONG).show();
            }
            RideUserMapping.createOrUpdate(ride, user, true, Constant.SCHEDULED, route);
        }
        else {
            ride = new Ride(route, userDate, userTime, vehicle, user);
            ride.save();
            RideUserMapping.createOrUpdate(ride, user, true, Constant.SCHEDULED, route);
            Toast.makeText(this,"New Ride created",Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(ScheduleRideActivity.this, MyProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("rideId", ride.getId());
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.updateRide) void onUpdateRideClick(View v){
        saveRide(true);
    }
    @OnClick(R.id.deleteRide) void onDeleteRideClick(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
        builder.setMessage("Want to cancel the ride ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rideUserMapping = RideUserMapping.find(RideUserMapping.class,
                                "ride_id = ? AND user_id = ?",
                                String.valueOf(ride.getId()),
                                String.valueOf(sharedPref.getLong("userId",0L))
                        ).get(0);
                        rideUserMapping.status = Constant.CANCELLED;
                        rideUserMapping.save();
                        Intent intent = new Intent(ScheduleRideActivity.this,MyRidesActivity.class);
                        //intent.putExtra("rideId",ride.getId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Toast.makeText(thisActivity,"Ride cancelled",Toast.LENGTH_LONG).show();
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


    @OnClick(R.id.scheduleRide) void scheduleRide(View v){
        saveRide(false);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 1: //profile - nothing doing
                break;
        }
    }

}