package com.getpillion;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.view.MenuItem;
import com.getpillion.common.Helper;
import com.getpillion.models.User;
import com.getpillion.models.Vehicle;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class VehicleInfoActivity extends ExtendMeSherlockWithMenuActivity {

    private Vehicle vehicle = null;

    @InjectView(R.id.model) EditText model;
    @InjectView(R.id.color) EditText color;
    @InjectView(R.id.number) EditText number;
    @InjectView(R.id.deleteVehicle)
    Button deleteVehicle;


    @OnClick(R.id.deleteVehicle) void deleteVehicle(View v){
        vehicle.delete();
        Helper.returnControlToCallingActivity(this);
    }
    @OnClick(R.id.saveVehicle) void saveVehicle(View v){

        Vehicle newVehicle = new Vehicle();
        newVehicle.user = User.findById(User.class,sharedPref.getLong("userId",0L));
        newVehicle.model = model.getText().toString();
        newVehicle.color = color.getText().toString();
        newVehicle.number = number.getText().toString();
        if (vehicle == null)
            newVehicle.save();
        else {
            if ( vehicle.update(newVehicle) )
                vehicle.save();
        }
        Helper.returnControlToCallingActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vehicle_info);
        ButterKnife.inject(this);


        if (!getIntent().hasExtra("vehicleId")){
            //hide schedule & delete ride buttons as this is new Route view
            deleteVehicle.setVisibility(View.GONE);
        }
        else { //edit route view
            vehicle = Vehicle.findById(Vehicle.class,getIntent().getExtras().getLong("vehicleId"));
            //populate data
            color.setText(vehicle.color);
            model.setText(vehicle.model);
            number.setText(vehicle.number);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

}