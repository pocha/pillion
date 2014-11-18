package com.getpillion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getpillion.common.Constant;
import com.getpillion.common.DatePickerFragment;
import com.getpillion.common.TimePickerFragment;
import com.getpillion.models.Ride;
import com.getpillion.models.RideUserMapping;
import com.getpillion.models.Route;
import com.getpillion.models.User;
import com.getpillion.models.Vehicle;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class ScheduleRideActivity extends ExtendMeSherlockWithMenuActivity  {

    @InjectView(R.id.vehicles)
    Spinner vehicleView;

    public Date userDate;
    private Time userTime;

    private Vehicle selectedVehicle = null;
    private Ride ride = null;
    private Route route = null;

    @InjectView(R.id.profileView)
    LinearLayout profileView;
    @InjectView(R.id.importProfile)
    Button importProfile;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)  {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule_ride);
        ButterKnife.inject(this);

        vehicles.addAll(
                Vehicle.find(Vehicle.class,"user = ?",
                        String.valueOf(sharedPref.getLong("userId",0L))
                )
        );
        vehicleAdapter = new VehicleAdapter(getApplicationContext(),vehicles);
        vehicleView.setAdapter(vehicleAdapter);

        User user = User.findById(User.class, sharedPref.getLong("userId",0L));
        if (user.name == null){
            profileView.setVisibility(View.GONE);
            importProfile.setVisibility(View.VISIBLE);
        }
        else {
            ((TextView)profileView.findViewById(R.id.name)).setText(user.name);
            ((TextView)profileView.findViewById(R.id.title)).setText(user.title);
        }

        if (getIntent().hasExtra("rideId")) { //edit screen
            ride = Ride.findById(Ride.class,getIntent().getExtras().getLong("rideId",0L));
            route = ride.route;
            //set date & time from the ride
            if (ride.dateLong != null)
                ((DatePickerFragment) getSupportFragmentManager().findFragmentById(R.id.datePicker)).setDate(ride.dateLong);
            ((TimePickerFragment) getSupportFragmentManager().findFragmentById(R.id.timePicker)).setTime(ride.timeLong);
            //pre-select vehicle
            int position = 0;
            for (Vehicle v:vehicles){
                if (v.getId() == ride.vehicle.getId()){
                    break;
                }
                position++;
            }
            vehicleView.setSelection(position);
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
        //create or update Ride
        DatePickerFragment d = (DatePickerFragment) getSupportFragmentManager().findFragmentById(R.id.datePicker);
        userDate = d.date;
        TimePickerFragment t = (TimePickerFragment) getSupportFragmentManager().findFragmentById(R.id.timePicker);
        userTime = t.time;

        User user = User.findById(User.class, sharedPref.getLong("userId",0L));
        if (isUpdate) {
            Ride newRide = new Ride(route,userDate,userTime,selectedVehicle,user);
            if (ride.update(newRide)) {
                ride.save();
            }
            RideUserMapping.createOrUpdate(ride, user, true, Constant.SCHEDULED);
            Toast.makeText(ScheduleRideActivity.this,"Ride updated",Toast.LENGTH_LONG).show();
        }
        else {
            ride = new Ride(route, userDate, userTime, selectedVehicle, user);
            ride.save();
            RideUserMapping.createOrUpdate(ride, user, true, Constant.SCHEDULED);
            Toast.makeText(ScheduleRideActivity.this,"New Ride created",Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(ScheduleRideActivity.this,RideInfoActivity.class);
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
                        Intent intent = new Intent(ScheduleRideActivity.this,RideInfoActivity.class);
                        intent.putExtra("rideId",ride.getId());
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

    @OnItemSelected(R.id.vehicles) void onVehicleSelect(int position){
        selectedVehicle = vehicles.get(position);
    }

    @OnClick(R.id.scheduleRide) void scheduleRide(View v){
        saveRide(false);
    }

    private ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
    private VehicleAdapter vehicleAdapter;

    private class VehicleAdapter extends ArrayAdapter<Vehicle> {
        private final Context context;
        private List<Vehicle> vehicles;

        public VehicleAdapter(Context context, List<Vehicle> vehicles) {
            super(context, R.layout.row, vehicles);
            this.context = context;
            this.vehicles = vehicles;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position,convertView,parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.main = (TextView) convertView.findViewById(R.id.row_title);
                viewHolder.main.setTextAppearance(getApplicationContext(),android.R.style.TextAppearance_Small);
                viewHolder.main.setTextColor(getResources().getColor(R.color.BottomButton));
                viewHolder.secondary = (TextView) convertView.findViewById(R.id.secondaryText);
                viewHolder.secondary.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Vehicle vehicle = vehicles.get(position);
            viewHolder.position = position;
            viewHolder.main.setText(vehicle.color + " " + vehicle.model);
            viewHolder.secondary.setText(vehicle.number);

            return convertView;
        }

        private class ViewHolder {
            public int position;
            public TextView main;
            public TextView secondary;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 1: //profile - nothing doing
                break;
            case 2: //vehicle creation - refresh vehicle list
                vehicles.clear();
                vehicles.addAll(
                        Vehicle.find(Vehicle.class,"user = ?",
                                String.valueOf(sharedPref.getLong("userId",0L))
                        )
                );
                vehicleAdapter.notifyDataSetChanged();
        }
    }

    @OnClick(R.id.importProfile) void myProfileActivityLaunch(View v){
        Intent intent = new Intent(ScheduleRideActivity.this,MyProfileActivity.class);
        intent.putExtra("requestCode",1);
        startActivityForResult(intent,1);
    }

    @OnClick(R.id.addVehicle) void addVehicle(View v){
        Intent intent = new Intent(ScheduleRideActivity.this,VehicleInfoActivity.class);
        intent.putExtra("requestCode",2);
        startActivityForResult(intent,2);
    }

}