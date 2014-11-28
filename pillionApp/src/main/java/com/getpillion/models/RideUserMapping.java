package com.getpillion.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by pocha on 15/10/14.
 */
public class RideUserMapping extends SyncSugarRecord<RideUserMapping> {

    @SerializedName("ride_id")
    public Long rideId;
    @SerializedName("user_id")
    public Long userId;
    @Ignore //as we already storing user_id separately. This is bad. Ideally, we should just have user & ride
    public User user; //needed for creation of object from json

    public int status = 0; // User status for this ride (requested, cancelled etc) check possible status value in Constants.java
    @SerializedName("is_owner")
    public Boolean isOwner = false;

    //Route detail
    public String origin;
    public String dest;
    public String distance;


    private void storeFromRoute(Route route){
        this.origin = route.origin;
        this.dest = route.dest;
        this.distance = route.distance;
    }

    public RideUserMapping(){}
    public RideUserMapping(Long rideId, Long userId, Boolean isOwner, int status, Route route){
        this.rideId = rideId;
        this.userId = userId;
        this.isOwner = isOwner;
        this.status = status;
        storeFromRoute(route);
    }


    public static RideUserMapping createOrUpdate(Ride ride, User user, Boolean isOwner, int status, Route route){
        Log.d("RideUserMapping","Inside createOrUpdate. isOwner value " + isOwner);
        List<RideUserMapping> rideUserMappings = RideUserMapping.find(RideUserMapping.class,
                "ride_id = ? and user_id = ?", String.valueOf(ride.getId()), String.valueOf(user.getId()));
        RideUserMapping rideUserMapping = null;
        if (rideUserMappings.isEmpty()){
            //create entry
            rideUserMapping = new RideUserMapping(ride.getId(),user.getId(),isOwner,status, route);
            rideUserMapping.save();
        }
        else {
            rideUserMapping = rideUserMappings.get(0);
            RideUserMapping newObject = new RideUserMapping(ride.getId(),user.getId(),isOwner,status, route);
            if ( rideUserMapping.update(newObject) )
                rideUserMapping.save();
        }

        return rideUserMapping;
    }

    public static RideUserMapping updateFromUpstream(Ride ride, RideUserMapping upstream){
        List<RideUserMapping> rideUserMappings = RideUserMapping.find(RideUserMapping.class,"global_id = ?",String.valueOf(upstream.globalId));
        RideUserMapping rideUserMapping;
        if (rideUserMappings.isEmpty()){
            //data is coming from upstream & can have new user & route. We need to locally create them.
            User user = User.updateFromUpstream(upstream.user);
            /*Route route = Route.updateFromUpstream(upstream.route);
            upstream.route = route;*/

            upstream.userId = user.getId();
            upstream.rideId = ride.getId();
            rideUserMapping = upstream;
        }
        else {
            rideUserMapping = rideUserMappings.get(0);
            upstream.userId = rideUserMapping.userId;
            upstream.rideId = rideUserMapping.rideId;
            rideUserMapping.update(upstream);
        }
        rideUserMapping.saveWithoutSync();
        return rideUserMapping;
    }

    @Override
    public String toJson(){ //replace ride_id & user_id with their respective global_ids

        Ride ride = Ride.findById(Ride.class,this.rideId);
        this.rideId = ride.globalId;

        User user = User.findById(User.class,this.userId);
        this.userId = user.globalId;

        //this.route_id = route.globalId; //not needed

        //server does not need these information. To broadcast to other clients, server is anyway creating it separately
        excludeFields.add("user");
        excludeFields.add("route");

        String json = super.toJson();

        this.rideId = ride.getId();
        this.userId = user.getId();

        return json;
    }


}
