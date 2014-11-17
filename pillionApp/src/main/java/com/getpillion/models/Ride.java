package com.getpillion.models;

import android.util.Log;

import com.getpillion.common.Constant;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pocha on 25/09/14.
 */
public class Ride extends SyncSugarRecord<Ride> {

    //this is for storing the time in db as java.sql.Time is not supported by SugarORM
    @SerializedName("time_long")
    public Long timeLong; //will never be null
    @Ignore
    public String time_stamp = null;

    public Vehicle vehicle = null;
    @Ignore
    private Long vehicle_id = null;

    @SerializedName("date_long")
    public Long dateLong = null; //declaring date as Long because SugarRecord does not seem to take null for date
    @Ignore
    public String ride_date = null;

    @SerializedName("is_scheduled")
    public boolean isScheduled = false;

    public Route route;
    @Ignore
    private Long route_id = null;

    @Ignore
    @SerializedName("ride_user_mappings")
    public ArrayList<RideUserMapping> rideUserMappings = new ArrayList<RideUserMapping>();


    public Ride(){}

    public void update(Route route, Date date, Time time, Vehicle vehicle, User owner) {
        this.route = route;
        this.dateLong = date.getTime();
        this.timeLong = time.getTime();
        this.vehicle = vehicle;
        this.isScheduled = true;
        this.save();
        RideUserMapping.findOrCreate(this, owner, true, Constant.SCHEDULED);
    }

    public Ride (Route route, Date date, Time time, Vehicle vehicle, User owner){
        Log.d("Ride.java","Inside constructor, creating ride with time " + time.toString());
        this.update(route,date,time,vehicle,owner);
    }

    public Ride(String from, String to, Time time, boolean isOffered, User owner) {
        Log.d("ashish","Entered new route creation");
        Log.d("Ride.java","Inside constructor, creating ride with time " + time.toString() + " which will be converted to " + time.getTime());

        this.route = new Route(from,to,isOffered,owner);

        this.timeLong = (time == null) ? Time.valueOf("09:00:00").getTime() : time.getTime();

        Log.d("ashish","New ride created - " + this.toString());
        this.save();

        RideUserMapping.findOrCreate(this, owner, true, Constant.CREATED);
    }

    public String getAmPmTime(){
        Log.d("Ride.java","Timestamp found for conversion to AM PM - " + this.timeLong +
                " Time converted value - " + new Time(this.timeLong) +
                " Time shown - " +  new SimpleDateFormat("hh:mm a").format(new Time(this.timeLong)));
        return new SimpleDateFormat("hh:mm a").format(new Time(this.timeLong));
    }

    public static Ride updateFromUpstream(Ride upstreamRide) throws Exception{
        //get from db
        List<Ride> rides = Ride.find(Ride.class, "global_id = ?", String.valueOf(upstreamRide.globalId));

        Ride ride = null;
        if (rides.isEmpty()) {
            ride = upstreamRide;
        } else {//update values
            ride = rides.get(0);
            ride.updatedAt = upstreamRide.updatedAt;
            ride.isScheduled = upstreamRide.isScheduled;
        }
        ride.timeLong = Time.valueOf(upstreamRide.time_stamp).getTime();
        Log.d("Ride.java","updated time from upstream " + ride.timeLong);
        if (!upstreamRide.ride_date.isEmpty())
            ride.dateLong = new SimpleDateFormat("yyyy-MM-dd").parse(upstreamRide.ride_date).getTime();

        ride.saveWithoutSync(); // we need ride.getId() in RideUserMapping.findOrCreate

        if (upstreamRide.route != null) { //null check is done as if only a few params in ride are updating, the rest may remain empty
            ride.route = Route.updateFromUpstream(upstreamRide.route);
            //no need to create RideUserMapping as it is being taken care in the travellers array below
        }

        if (upstreamRide.vehicle != null) {
            ride.vehicle = Vehicle.updateFromUpstream(upstreamRide.vehicle, ride.route.owner);
        }

        if (upstreamRide.rideUserMappings != null)
            for (RideUserMapping rideUserMapping:upstreamRide.rideUserMappings){
                RideUserMapping.updateFromUpstream(ride, rideUserMapping);
            }

        ride.saveWithoutSync();
        return ride;
    }

    @Override
    public String toJson(){
        //these values will be used on the server for the time & date
        Time time = new Time(this.timeLong);
        this.time_stamp = time.toString();
        if (dateLong != null) {
            Date date = new Date(this.dateLong);
            this.ride_date = new SimpleDateFormat("yyyy-MM-dd").format(date);
        }
        this.route_id = this.route.globalId;
        if (vehicle != null)
            this.vehicle_id = this.vehicle.globalId;

        excludeFields.add("timeLong");
        excludeFields.add("dateLong");
        excludeFields.add("route");
        excludeFields.add("vehicle");
        excludeFields.add("users");


        return super.toJson();
    }




}
