package com.getpillion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.getpillion.common.DatePickerFragment;
import com.getpillion.common.TimePickerFragment;
import com.getpillion.models.Ride;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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
    }

    @OnItemSelected(R.id.vehicles) void onVehicleSelect(int position){
        selectedVehicle = vehicles.get(position);
    }

    @OnClick(R.id.scheduleRide) void scheduleRide(View v){
        //create new Ride
        DatePickerFragment d = (DatePickerFragment) getSupportFragmentManager().findFragmentById(R.id.datePicker);
        userDate = d.date;
        TimePickerFragment t = (TimePickerFragment) getSupportFragmentManager().findFragmentById(R.id.timePicker);
        userTime = t.time;

        Route route = Route.findById(Route.class, getIntent().getExtras().getLong("routeId"));
        User user = User.findById(User.class, sharedPref.getLong("userId",0L));
        Ride ride = new Ride(route,userDate,userTime,selectedVehicle, user);
        ride.save();
        //TODO send data to server
        Intent intent = new Intent(ScheduleRideActivity.this,RideInfoActivity.class);
        intent.putExtra("rideId",ride.getId());
        startActivity(intent);
        finish();
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

    @OnClick(R.id.viewProfile) void myProfileActivityLaunch(View v){
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