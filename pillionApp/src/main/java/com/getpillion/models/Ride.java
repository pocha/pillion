package com.getpillion.models;

import android.util.Log;

import com.getpillion.common.Helper;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by pocha on 25/09/14.
 */
public class Ride extends SugarRecord<Ride> {
    public Long globalId = null;

    @Ignore
    public Date time = null; //field not supported in SugarORM

    //this is for storing the time in db as java.sql.Time is not supported by SugarORM
    public Long timestamp; //will never be null

    public Vehicle vehicle = null;

    //public User owner;
    public Long date = null; //declaring date as Long because SugarRecord does not seem to take null for date
    public boolean isScheduled = false;
    public Route route;

    public Ride(){}

    public Ride (Route route, Date date, Time time, Vehicle vehicle){
        this.route = route;
        this.date = date.getTime();
        this.timestamp = time.getTime();
        this.vehicle = vehicle;
        this.isScheduled = true;
        this.save();
    }

    public Ride(String from, String to, Time time, boolean isOffered, User owner) {
        Log.d("ashish","Entered new route creation");

        this.route = new Route(from,to,isOffered,owner);

        this.timestamp = (time == null) ? Time.valueOf("09:00:00").getTime() : time.getTime();

        //this.owner = owner;
        RideUserMapping.findOrCreate(this, owner, true);

        /*this.date = new Date();
        this.isScheduled = Math.random() < 0.5;
        if (Math.random() < 0.5) {
            this.vehicle = new Vehicle("Mercedez","black","KA51 Q8745");
        }
        for (int i=0; i < Math.random()*3; i++){
            //this.users.add(User.returnDummyUser());
            new RouteUserMapping(this,User.returnDummyUser());
        }*/

        Log.d("ashish","New ride created - " + this.toString());
        this.save();
    }

    public Time getSqlTime(){
        return new Time(this.timestamp);
    }
    public String getAmPmTime(){
        return new SimpleDateFormat("hh:mm a").format(new Date(this.timestamp));
    }

    public static void getRoutesFromJson(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray routes = json.getJSONArray("routes");
            for (int i=0; i < routes.length(); i++){
                Log.d("Route.java","creating route " + routes.getJSONObject(i).toString());

                createOrUpdateFromJson(routes.getJSONObject(i));
            }
        }catch (JSONException e){
            Log.d("Route.java","getRoutesFromJson() " + jsonString.toString() );
            e.printStackTrace();
        }
    }

    public static void createOrUpdateFromJson(JSONObject jsonRoute){
        Ride ride = null;
        try {
            List<Ride> rides = Ride.find(Ride.class, "global_id = ?", String.valueOf(jsonRoute.getLong("globalId")));
            if (rides.isEmpty()) {//create new route
                ride = new Ride();
                ride.globalId = jsonRoute.getLong("globalId");
            }
            else {
                ride = rides.get(0);
            }

            ride.route = Route.createOrUpdateFromJson(jsonRoute.getJSONObject("route"));

            ride.timestamp = Helper.updateFromJsonField(ride.timestamp,jsonRoute.optLong("timestamp"));
            Log.d("Route.java","updated route timestamp " + ride.timestamp + " route timestamp " + jsonRoute.optLong("timestamp"));
            if (jsonRoute.getJSONObject("vehicle") != null){
                ride.vehicle = Vehicle.createOrUpdateFromJson(jsonRoute.getJSONObject("vehicle"));
            }
            ride.isScheduled = Helper.updateFromJsonField(ride.isScheduled,jsonRoute.optBoolean("isScheduled"));
            ride.date = Helper.updateFromJsonField(ride.date,jsonRoute.optLong("date"));

            ride.save();

            if (jsonRoute.optJSONObject("owner") != null) {
                //add owner to RouteUserMapping if not already added
                User user = User.createOrUpdateFromJson(jsonRoute.getJSONObject("owner"));
                if (ride.route.owner == null ){
                    ride.route.owner = user;
                    ride.route.save();
                }
                RideUserMapping.findOrCreate(ride, user, true);
            }

            if (jsonRoute.optJSONArray("users") != null) {//parse users for the ride, can only do after route.save else route id wont be there
                JSONArray users = jsonRoute.getJSONArray("users");
                for (int i=0; i < users.length(); i++)
                    RideUserMapping.createOrUpdateFromJson(ride, users.getJSONObject(i));
            }

        }
        catch (JSONException e){
            Log.d("Route.java","Error extracting json in createOrUpdateRouteFromJSON " + e.toString() + "\n jsonRoute - " + jsonRoute.toString());
            e.printStackTrace();
        }
    }

}
