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
    @Ignore
    @SerializedName("user_attributes")
    public User user; //needed for creation of object from json

    public int status = 0; // User status for this ride (requested, cancelled etc) check possible status value in Constants.java
    @SerializedName("is_owner")
    public Boolean isOwner = false;

    public RideUserMapping(){}
    public RideUserMapping(Long rideId, Long userId, Boolean isOwner, int status){
        this.rideId = rideId;
        this.userId = userId;
        this.isOwner = isOwner;
        this.status = status;
    }


    public static RideUserMapping createOrUpdate(Ride ride, User user, Boolean isOwner, int status){
        Log.d("RideUserMapping","Inside createOrUpdate. isOwner value " + isOwner);
        List<RideUserMapping> rideUserMappings = RideUserMapping.find(RideUserMapping.class,
                "ride_id = ? and user_id = ?", String.valueOf(ride.getId()), String.valueOf(user.getId()));
        RideUserMapping rideUserMapping = null;
        if (rideUserMappings.isEmpty()){
            //create entry
            rideUserMapping = new RideUserMapping(ride.getId(),user.getId(),isOwner,status);
            rideUserMapping.save();
        }
        else {
            rideUserMapping = rideUserMappings.get(0);
            RideUserMapping newObject = new RideUserMapping(ride.getId(),user.getId(),isOwner,status);
            if ( rideUserMapping.update(newObject) )
                rideUserMapping.save();
        }

        return rideUserMapping;
    }

    public static void updateFromUpstream(Ride ride, RideUserMapping upstream){
        List<RideUserMapping> rideUserMappings = RideUserMapping.find(RideUserMapping.class,"global_id = ?",String.valueOf(upstream.globalId));
        RideUserMapping rideUserMapping;
        if (rideUserMappings.isEmpty()){
            //create user to get its id
            User user = User.updateFromUpstream(upstream.user);
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
    }

    @Override
    public String toJson(){ //replace ride_id & user_id with their respective global_ids

        Ride ride = Ride.findById(Ride.class,this.rideId);
        this.rideId = ride.globalId;

        User user = User.findById(User.class,this.userId);
        this.userId = user.globalId;

        //excludeFields.add("user"); //we need this field for new ride request to be broadcasted to the owner

        String json = super.toJson();

        this.rideId = ride.getId();
        this.userId = user.getId();

        return json;
    }


}
