package com.getpillion.models;

import android.util.Log;

import com.getpillion.R;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;

/**
 * Created by pocha on 29/09/14.
 */

public class User extends SugarRecord<User> {
    public Long globalId;
    public String name;
    public String title;
    public String fieldOfWork;
    public String phone;
    public int profilePic;
    @Ignore
    public static ArrayList<User> dummyUsers = new ArrayList<User>();

    public User(){}

    public User(String name, String title, String fieldOfWork, String phone, int profilePic) {
        this.name = name;
        this.title = title;
        this.fieldOfWork = fieldOfWork;
        this.phone = phone;
        this.profilePic = profilePic;
        this.save();
    }

    public static User returnDummyUser() {
        Log.d("ashish","Entering dummy user");
        if (dummyUsers.isEmpty()) {
            dummyUsers = new ArrayList<User>();
            dummyUsers.add(new User("Ashish", "CEO Codelearn", "IT, Software", "9538384545", R.drawable.action_people));
            dummyUsers.add(new User("Nishant", "CEO Reviewgist", "IT, Software", "9538384545", R.drawable.action_people));
            dummyUsers.add(new User("Lakshmi", "CEO of House at Codelearn", "IT, Software", "9538384545", R.drawable.action_people));
            dummyUsers.add(new User("Amit", "CEO of everything else", "IT, Software", "9538384545", R.drawable.action_people));
        }
        User user = dummyUsers.get((int)(Math.random()*3.99));
        Log.d("ashish","User returned - " + user.name);
        return user;
    }
}
