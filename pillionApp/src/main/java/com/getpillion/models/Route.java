package com.getpillion.models;

import java.sql.Time;
import java.util.Date;
/**
 * Created by pocha on 25/09/14.
 */
public class Route {
    public String from;
    public String to;
    public Time time;
    public Date lastDate;
    public boolean isScheduled;
    public Vehicle vehicle;

    public Route(String from, String to, Time time) {
        this.from = from;
        this.to = to;
        this.time = time;
    }
}
