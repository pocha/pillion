package com.getpillion.models;

import com.orm.SugarRecord;

/**
 * Created by pocha on 25/09/14.
 */
public class Vehicle extends SugarRecord<Vehicle> {
    public Long globalId;
    public String model;
    public String color;
    public String number;

    public Vehicle(){}

    public Vehicle (String model, String color, String number) {
        this.model = model;
        this.color = color;
        this.number = number;
    }
}
