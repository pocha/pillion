package com.getpillion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.getpillion.common.Constant;
import com.getpillion.models.Ride;
import com.getpillion.models.Route;
import com.getpillion.models.Vehicle;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.sql.Time;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTouch;

public class ScheduleRideActivity extends ExtendMeSherlockWithMenuActivity implements OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    @InjectView(R.id.date)
    EditText date;
    @InjectView(R.id.time)
    EditText time;
    @InjectView(R.id.vehicles)
    Spinner vehicleView;

    Calendar calendar;

    @OnTouch(R.id.date) boolean launchDateDialog(View v, MotionEvent event){
        if (event.getAction() != MotionEvent.ACTION_UP)
            return true;
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        datePickerDialog.setYearRange(1985, 2028);
        datePickerDialog.setCloseOnSingleTapDay(/*isCloseOnSingleTapDay()*/true);
        datePickerDialog.show(getSupportFragmentManager(), "some_date_picker_tag");
        return true;
    }

    private Date userDate;
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        //Toast.makeText(getActivity(), "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
        date.setText(day + " " + new DateFormatSymbols().getMonths()[month-1] + " " + year);
        userDate = new Date(year,month,day);
    }

    @OnTouch(R.id.time) boolean launchTimeDialog(View v, MotionEvent event){
        if (event.getAction() != MotionEvent.ACTION_UP)
            return true;
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
        timePickerDialog.setVibrate(true);
        timePickerDialog.setCloseOnSingleTapMinute(true);
        timePickerDialog.show(getSupportFragmentManager(), "some_time_picker_tag");
        return true;
    }

    private Time userTime;
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        //Toast.makeText(MainActivity.this, "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
        time.setText(hourOfDay + ":" + minute);
        userTime = Time.valueOf(hourOfDay+":"+minute+":00");
    }

    @InjectView(R.id.noVehicleFound) TextView noVehicleFound;
    private Vehicle selectedVehicle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(),
                Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.activity_schedule_ride);
        ButterKnife.inject(this);

        calendar = Calendar.getInstance();
        date.setText(calendar.get(Calendar.YEAR) + " " + calendar.get(Calendar.MONTH)+ " " + calendar.get(Calendar.DAY_OF_MONTH));
        time.setText(calendar.get(Calendar.HOUR_OF_DAY) +":"+ calendar.get(Calendar.MINUTE));

        userDate = new Date();
        userTime = Time.valueOf(time.getText().toString()+":00");

        vehicles.addAll(
                Vehicle.find(Vehicle.class,"user = ?",
                        String.valueOf(sharedPref.getLong("userId",0L))
                )
        );
        vehicleAdapter = new VehicleAdapter(getApplicationContext(),vehicles);
        vehicleView.setEmptyView(noVehicleFound);
        vehicleView.setAdapter(vehicleAdapter);
    }

    @OnItemSelected(R.id.vehicles) void onVehicleSelect(int position){
        selectedVehicle = vehicles.get(position);
    }

    @OnClick(R.id.scheduleRide) void scheduleRide(View v){
        //create new Ride
        Route route = Route.findById(Route.class, getIntent().getExtras().getLong("routeId"));
        Ride ride = new Ride(route,userDate,userTime,selectedVehicle);
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