package com.getpillion.models;

import com.orm.SugarRecord;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by pocha on 15/10/14.
 */
public class RideUserMapping extends SugarRecord<RideUserMapping> {
    public Ride ride;
    public User user;

    public int status = 0; // User status for this ride (requested, cancelled etc) check possible status value in Constants.java
    public Boolean isOwner = false;

    public RideUserMapping(){}
    public RideUserMapping(Ride ride, User user, Boolean isOwner){
        this.ride = ride;
        this.user = user;
        this.isOwner = isOwner;
    }

    public static void createOrUpdateFromJson(Ride ride, JSONObject jsonUser) {
        createOrUpdateFromJson(ride,jsonUser,false);
    }

    public static void createOrUpdateFromJson(Ride ride, JSONObject jsonUser, Boolean isOwner){
        try {
            User user = User.createOrUpdateFromJson(jsonUser);
            findOrCreate(ride,user,isOwner);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static RideUserMapping findOrCreate(Ride ride, User user, Boolean isOwner){
        List<RideUserMapping> rideUserMappings = RideUserMapping.find(RideUserMapping.class,
                "ride = ? and user = ?", String.valueOf(ride.getId()), String.valueOf(user.getId()));
        if (rideUserMappings.isEmpty()){
            //create entry
            RideUserMapping rideUserMapping = new RideUserMapping(ride,user,isOwner);
            rideUserMapping.save();
            return rideUserMapping;
        }
        else
            return rideUserMappings.get(0);
    }


}
