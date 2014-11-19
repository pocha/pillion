package com.getpillion.models;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by pocha on 25/09/14.
 */
public class Vehicle extends SyncSugarRecord<Vehicle> {
    public String model;
    public String color;
    public String number;
    @SerializedName("user_attributes")
    public User user = null; //indicates which user this vehicle belongs to
    @Ignore
    public Long user_id = null;

    public Vehicle(){}

    public static Vehicle updateFromUpstream(Vehicle upstreamVehicle, User user){
        List<Vehicle> vehicles = Vehicle.find(Vehicle.class,"global_id = ?",String.valueOf(upstreamVehicle.globalId));

        Vehicle vehicle = null;
        if (vehicles.isEmpty()){
            vehicle = upstreamVehicle;
            vehicle.user = user;
        }
        else {
            vehicle = vehicles.get(0);
            vehicle.model = upstreamVehicle.model;
            vehicle.color = upstreamVehicle.color;
            vehicle.number = upstreamVehicle.number;
            vehicle.updatedAt = upstreamVehicle.updatedAt;
        }

        vehicle.saveWithoutSync();

        return vehicle;
    }
    @Override
    public String toJson(){
        //these values will be used on the server for the time & date
        this.user_id = this.user.globalId;

        excludeFields.add("user");

        return super.toJson();
    }
}
