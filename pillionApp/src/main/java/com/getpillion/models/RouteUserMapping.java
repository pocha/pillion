package com.getpillion.models;

import com.orm.SugarRecord;

/**
 * Created by pocha on 15/10/14.
 */
public class RouteUserMapping extends SugarRecord<RouteUserMapping> {
    public Route route;
    public User user;
    public RouteUserMapping(){}
    public RouteUserMapping(Route route, User user){
        this.route = route;
        this.user = user;
    }
}
