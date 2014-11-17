package com.getpillion.models;

import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by pocha on 31/10/14.
 */
public class Route extends SyncSugarRecord<Route> {
    public String origin;
    public String dest;

    @SerializedName("is_offered")
    public boolean isOffered = true;

    @SerializedName("user_attributes")
    public User owner = null;
    @Ignore
    public Long user_id = null;

    public Route(){}

    public Route(String origin, String dest, boolean isOffered, User user){
        this.origin = origin.isEmpty() ? "default" : origin;
        this.dest = dest.isEmpty() ? "default" : dest;
        this.isOffered = isOffered;
        this.owner = user;
        this.save();
    }

    public static Route updateFromUpstream(Route upstreamRoute){
        List<Route> routes = Route.find(Route.class,"global_id = ?",String.valueOf(upstreamRoute.globalId));

        Route route = null;
        if (routes.isEmpty()){
            route = upstreamRoute;
        }
        else {
            route = routes.get(0);
            route.origin = upstreamRoute.origin;
            route.dest = upstreamRoute.dest;
            route.isOffered = upstreamRoute.isOffered;
            route.updatedAt = upstreamRoute.updatedAt;
        }
        if (upstreamRoute.owner != null)
            route.owner = User.updateFromUpstream(upstreamRoute.owner);

        route.saveWithoutSync();

        return route;
    }

    @Override
    public String toJson(){
        //these values will be used on the server for the time & date
        this.user_id = this.owner.globalId;

        excludeFields.add("owner");

        return super.toJson();
    }

}
