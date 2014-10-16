package com.getpillion.models;

import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private Long timestamp; //will never be null

    public String vehicle = null;
    public boolean isOffered = true;
    public User owner;
    public Long date = null; //declaring date as Long because SugarRecord does not seem to take null for date
    public boolean isScheduled = false;

    public Route(){}

    public Route(String from, String to, Time time, boolean isOffered, User owner) {
        Log.d("ashish","Entered new route creation");
        this.origin = from.isEmpty() ? "Brigade Gardenia" : from; //values are provided only for testing purpose
        this.dest = to.isEmpty() ? "Ecospace" : to;
        this.timestamp = (time == null) ? Time.valueOf("09:00:00").getTime() : time.getTime();
        this.isOffered = isOffered;
        this.owner = owner;
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

}
