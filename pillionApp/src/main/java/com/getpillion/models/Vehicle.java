package com.getpillion.models;

import android.util.Log;

import com.getpillion.common.Helper;
import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

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
            vehicle = Vehicle.findById(Vehicle.class,jsonVehicle.getLong("globalId"));
            if(vehicle == null) {
                vehicle = new Vehicle();
                vehicle.globalId = jsonVehicle.getLong("globalId");
            }

            Log.d("Vehicle.java","Fetching vehicle model for testing  - " + vehicle.model);
            Helper.updateFromJsonField(vehicle.model,jsonVehicle.optString("model"));
            Helper.updateFromJsonField(vehicle.color,jsonVehicle.optString("color"));
            Helper.updateFromJsonField(vehicle.number,jsonVehicle.optString("number"));
            vehicle.save();

            return vehicle;
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
