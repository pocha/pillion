package com.getpillion.models;

/**
 * Created by pocha on 25/09/14.
 */
public class Vehicle {
    public Long id;
    public String model;
    public String color;
    public String number;

    public Vehicle (String model, String color, String number) {
        this.model = model;
        this.color = color;
        this.number = number;
    }
}
