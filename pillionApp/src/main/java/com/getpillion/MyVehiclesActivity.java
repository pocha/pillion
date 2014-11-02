package com.getpillion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;
import com.getpillion.models.Vehicle;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MyVehiclesActivity extends ExtendMeSherlockWithMenuActivity {

   @InjectView(R.id.vehicles)
   ListView vehicles;
   @InjectView(R.id.noVehiclesFound)
   TextView noVehiclesFound;
   @InjectView(R.id.addVehicle)
   Button addVehicle;

   private ArrayList<Vehicle> vehiclesData = new ArrayList<Vehicle>();
   private VehicleAdapter vehicleAdapter;

   @OnClick(R.id.addVehicle) void createVehicle(View v){
       Intent intent = new Intent(MyVehiclesActivity.this, VehicleInfoActivity.class);
       startActivityForResult(intent,0);
   }
   @OnItemClick(R.id.vehicles) void navigateToVehicleInfo(int position){
       Intent intent = new Intent(MyVehiclesActivity.this, VehicleInfoActivity.class);
       intent.putExtra("vehicleId",vehiclesData.get(position).getId());
       startActivityForResult(intent,1);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        vehiclesData.clear();
        vehiclesData.addAll(
                Vehicle.find(Vehicle.class,"user = ?",
                        String.valueOf(sharedPref.getLong("userId",0L))
                )
        );
        vehicleAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(),
                Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.activity_my_vehicles);
        ButterKnife.inject(this);

        vehicles.setEmptyView(noVehiclesFound);
        vehiclesData.addAll(Vehicle.find(
                Vehicle.class, "user = ?", String.valueOf(sharedPref.getLong("userId", 0L))
        ));
        vehicleAdapter = new VehicleAdapter(getApplicationContext(),vehiclesData);
        vehicles.setAdapter(vehicleAdapter);
    }

    private class VehicleAdapter extends ArrayAdapter<Vehicle> {
        private final Context context;
        private List<Vehicle> vehicles;

        public VehicleAdapter(Context context, ArrayList<Vehicle> vehicles) {
            super(context, R.layout.route, vehicles);
            this.context = context;
            this.vehicles = vehicles;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.route, parent, false);
                convertView.findViewById(R.id.fromTo).setVisibility(View.GONE);
                convertView.findViewById(R.id.to).setVisibility(View.GONE);
                //convertView.findViewById(R.id.time).setVisibility(View.GONE);


                viewHolder = new ViewHolder();
                viewHolder.colorModel = (TextView) convertView.findViewById(R.id.from);
                viewHolder.number = (TextView) convertView.findViewById(R.id.time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final Vehicle vehicle = vehicles.get(position);
            viewHolder.position = position;
            viewHolder.colorModel.setText(vehicle.color + " " + vehicle.model);
            viewHolder.number.setText(vehicle.number);

            return convertView;
        }

        private class ViewHolder {
            public int position;
            public TextView colorModel;
            public TextView number;
        }
    }
}