package com.getpillion.models;

import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by pocha on 11/10/14.
 */


public class Ride {
    public Long Id;
    public Route route;
    public enum Status {
        requested,accepted,rejected,cancelled, //for request ride;
        scheduled//for created ride
    }
    public Status status;


    public Ride(String from, String to, Time time, Status status){
        this.route = new Route(from,to,time);
        //this.routeId = route.id;
        this.status = status;
    }

    public Ride (Route route, Status status){
        this.route = route;
        this.status = status;
    }

    public static ArrayList<Ride> getAll(){
        ArrayList<Ride> rides = new ArrayList<Ride>();

        if( Math.random() < 0.5 ) {//return nothing or return dummy rides randomly
            //creating dummy rides as requester
            rides.add(new Ride("Brigade Gardenia","RMZ Ecospace", Time.valueOf("8:00:00"), Status.requested));
            rides.add(new Ride("Brigade Gardenia","RMZ Ecospace", Time.valueOf("12:00:00"),Status.accepted));
            rides.add(new Ride("Brigade Gardenia","RMZ Ecospace", Time.valueOf("16:00:00"),Status.rejected));
            rides.add(new Ride("Brigade Gardenia","RMZ Ecospace", Time.valueOf("22:00:00"),Status.cancelled));
            //creating dummy rides as creator
            rides.add(new Ride("Brigade Gardenia","RMZ Ecospace", Time.valueOf("9:00:00"),Status.scheduled));
            rides.add(new Ride("Brigade Gardenia","RMZ Ecospace", Time.valueOf("13:00:00"),Status.cancelled));
        }
        return rides;
    }

    public static Status getRandomStatus(){
        float rand = (float)Math.random();
        Log.d("Ride.java","Inside getRandomStatus. Vaue of rand " + rand);

        if (rand < (float)1/6)
            return Status.requested;
        else if (rand < (float)2/6)
            return Status.cancelled;
        else if (rand < (float)3/6)
            return Status.accepted;
        else if (rand < (float)4/6)
            return Status.rejected;
        else if (rand < (float)5/6)
            return Status.scheduled;
        else return null;
    }
}
