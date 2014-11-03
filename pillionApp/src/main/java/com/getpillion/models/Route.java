package com.getpillion.models;

import android.util.Log;

import com.getpillion.common.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pocha on 31/10/14.
 */
public class Route extends SyncSugarRecord<Route> {
    public Long globalId = null;
    public String origin;
    public String dest;
    public boolean isOffered = true;
    public User owner = null;

    public Route(){}

    public Route(String origin, String dest, boolean isOffered, User user){
        this.origin = origin.isEmpty() ? "default" : origin;
        this.dest = dest.isEmpty() ? "default" : dest;
        this.isOffered = isOffered;
        this.owner = user;
        this.save();
    }

    public static Route createOrUpdateFromJson(JSONObject jsonRoute){
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
            route.origin = Helper.updateFromJsonField(route.origin, jsonRoute.optString("origin"));
            route.dest = Helper.updateFromJsonField(route.dest,jsonRoute.optString("dest"));

            route.isOffered = Helper.updateFromJsonField(route.isOffered,jsonRoute.optBoolean("isOffered"));

            route.save();
        }
        catch (JSONException e){
            Log.d("Route.java","Error extracting json in createOrUpdateRouteFromJSON " + e.toString() + "\n jsonRoute - " + jsonRoute.toString());
            e.printStackTrace();
        }
        return route;
    }

}
