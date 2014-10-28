package com.getpillion.models;

import android.util.Log;

import com.getpillion.common.Helper;
import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pocha on 25/09/14.
 */
public class Vehicle extends SugarRecord<Vehicle> {
    public Long globalId;
    public String model;
    public String color;
    public String number;

    public Vehicle(){}

    public Vehicle (String model, String color, String number) {
        this.model = model;
        this.color = color;
        this.number = number;
        this.save();
    }

    public static Vehicle createOrUpdateFromJson(JSONObject jsonVehicle){
        Vehicle vehicle = null;
        try {
            Log.d("Vehicle.java","Json Vehicle received - " + jsonVehicle.toString());
            List<Vehicle> vehicles = Vehicle.find(Vehicle.class,"global_id = ?",String.valueOf(jsonVehicle.getLong("globalId")));
            if(vehicles.isEmpty()) {
                vehicle = new Vehicle();
                vehicle.globalId = jsonVehicle.getLong("globalId");
            }
            else
                vehicle = vehicles.get(0);

            Log.d("Vehicle.java","Fetching vehicle model for testing  - " + vehicle.model);
            vehicle.model = Helper.updateFromJsonField(vehicle.model,jsonVehicle.optString("model"));
            vehicle.color = Helper.updateFromJsonField(vehicle.color,jsonVehicle.optString("color"));
            vehicle.number = Helper.updateFromJsonField(vehicle.number,jsonVehicle.optString("number"));
            vehicle.save();

            return vehicle;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
