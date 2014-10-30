package com.getpillion.models;

import com.orm.SugarRecord;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by pocha on 15/10/14.
 */
public class RouteUserMapping extends SugarRecord<RouteUserMapping> {
    public Route route;
    public User user;

    public int status = 0;
    public Boolean isOwner = false;

    public RouteUserMapping(){}
    public RouteUserMapping(Route route, User user, Boolean isOwner){
        this.route = route;
        this.user = user;
        this.isOwner = isOwner;
    }

    public static void createOrUpdateFromJson(Route route, JSONObject jsonUser) {
        createOrUpdateFromJson(route,jsonUser,false);
    }

    public static void createOrUpdateFromJson(Route route, JSONObject jsonUser, Boolean isOwner){
        try {
            User user = User.createOrUpdateFromJson(jsonUser);
            findOrCreate(route,user,isOwner);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static RouteUserMapping findOrCreate(Route route, User user, Boolean isOwner){
        List<RouteUserMapping> routeUserMappings = RouteUserMapping.find(RouteUserMapping.class,
                "route = ? and position = ?", String.valueOf(route.getId()), String.valueOf(user.getId()));
        if (routeUserMappings.isEmpty()){
            //create entry
            RouteUserMapping routeUserMapping = new RouteUserMapping(route,user,isOwner);
            routeUserMapping.save();
            return routeUserMapping;
        }
        else
            return routeUserMappings.get(0);
    }


}
