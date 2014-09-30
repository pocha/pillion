package com.getpillion.models;

import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
/**
 * Created by pocha on 25/09/14.
 */
public class Route {
    public Long id;
    public String from;
    public String to;
    public Time time;
    public Date date;
    public boolean isScheduled = false;
    public Vehicle vehicle = null;
    public ArrayList<User> users;

    public Route(String from, String to, Time time) {
        Log.d("ashish","Entered new route creation");
        this.from = from;
        this.to = to;
        this.time = time;
        this.date = new Date();
        this.isScheduled = Math.random() < 0.5;
        this.users = new ArrayList<User>();
        if (this.isScheduled) {
            this.vehicle = new Vehicle("Mercedez","black","KA51 Q8745");
        }
        for (int i=0; i < Math.random()*3; i++){
            this.users.add(User.returnDummyUser());
        }
        Log.d("ashish","New route created - " + this.toString());
    }
}
