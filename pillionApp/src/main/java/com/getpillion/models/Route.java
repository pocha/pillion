package com.getpillion.models;

import android.util.Log;

import com.getpillion.common.Helper;
import com.google.gson.Gson;
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
public class Route extends SugarRecord<Route> {
    public Long globalId = null;
    public String origin;
    public String dest;

    @Ignore
    public Date time = null; //field not supported in SugarORM

    //this is for storing the time in db as java.sql.Time is not supported by SugarORM
    public Long timestamp; //will never be null

    public Vehicle vehicle = null;
    public boolean isOffered = true;
    //public User owner;
    public Long date = null; //declaring date as Long because SugarRecord does not seem to take null for date
    public boolean isScheduled = false;
    public boolean isMyRoute = false;

    public Route(){}

    public Route(String from, String to, Time time, boolean isOffered, User owner) {
        Log.d("ashish","Entered new route creation");
        this.origin = from.isEmpty() ? "Brigade Gardenia" : from; //values are provided only for testing purpose
        this.dest = to.isEmpty() ? "Ecospace" : to;
        this.timestamp = (time == null) ? Time.valueOf("09:00:00").getTime() : time.getTime();
        this.isOffered = isOffered;
        //this.owner = owner;
        RouteUserMapping.findOrCreate(this,owner,true);
        this.isMyRoute = true;
        /*this.date = new Date();
        this.isScheduled = Math.random() < 0.5;
        if (Math.random() < 0.5) {
            this.vehicle = new Vehicle("Mercedez","black","KA51 Q8745");
        }
        for (int i=0; i < Math.random()*3; i++){
            //this.users.add(User.returnDummyUser());
            new RouteUserMapping(this,User.returnDummyUser());
        }*/

        Log.d("ashish","New route created - " + this.toString());
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
            Gson gson = new Gson();
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
        Route route = null;
        try {
            List<Route> routes = Route.find(Route.class, "global_id = ?", String.valueOf(jsonRoute.getLong("globalId")));
            if (routes.isEmpty()) {//create new route
                route = new Route();
                route.globalId = jsonRoute.getLong("globalId");
            }
            else {
                route = routes.get(0);
            }
            route.origin = Helper.updateFromJsonField(route.origin,jsonRoute.optString("origin"));
            route.dest = Helper.updateFromJsonField(route.dest,jsonRoute.optString("dest"));
            route.timestamp = Helper.updateFromJsonField(route.timestamp,jsonRoute.optLong("timestamp"));
            Log.d("Route.java","updated route timestamp " + route.timestamp + " route timestamp " + jsonRoute.optLong("timestamp"));
            if (jsonRoute.getJSONObject("vehicle") != null){
                route.vehicle = Vehicle.createOrUpdateFromJson(jsonRoute.getJSONObject("vehicle"));
            }
            route.isOffered = Helper.updateFromJsonField(route.isOffered,jsonRoute.optBoolean("isOffered"));
            route.isScheduled = Helper.updateFromJsonField(route.isScheduled,jsonRoute.optBoolean("isScheduled"));
            route.date = Helper.updateFromJsonField(route.date,jsonRoute.optLong("date"));

            route.save();

            if (jsonRoute.optJSONObject("owner") != null) {
                //add owner to RouteUserMapping if not already added
                RouteUserMapping.findOrCreate(route,User.createOrUpdateFromJson(jsonRoute.getJSONObject("owner")),true);
            }

            if (jsonRoute.optJSONArray("users") != null) {//parse users for the ride, can only do after route.save else route id wont be there
                JSONArray users = jsonRoute.getJSONArray("users");
                for (int i=0; i < users.length(); i++)
                    RouteUserMapping.createOrUpdateFromJson(route,users.getJSONObject(i));
            }

        }
        catch (JSONException e){
            Log.d("Route.java","Error extracting json in createOrUpdateRouteFromJSON " + e.toString() + "\n jsonRoute - " + jsonRoute.toString());
            e.printStackTrace();
        }
    }

}
