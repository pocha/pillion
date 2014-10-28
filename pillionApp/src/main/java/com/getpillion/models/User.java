package com.getpillion.models;

import android.util.Log;

import com.getpillion.R;
import com.getpillion.common.Helper;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public static User createOrUpdateFromJson(JSONObject jsonUser){
        User user = null;
        try {
            List<User> users = User.find(User.class,"global_id = ?",String.valueOf(jsonUser.getLong("globalId")));
            if (users.isEmpty()){
                user = new User();
                user.globalId = jsonUser.getLong("globalId");
            }
            else
                user = users.get(0);

            user.name = Helper.updateFromJsonField(user.name , jsonUser.optString("name"));
            user.title = Helper.updateFromJsonField(user.title , jsonUser.optString("title"));
            user.fieldOfWork = Helper.updateFromJsonField(user.fieldOfWork , jsonUser.optString("fieldOfWork"));
            user.phone = Helper.updateFromJsonField(user.phone , jsonUser.optString("phone"));
            user.save();
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.d("User.java","Error in createOrUpdateFromJSON " + e.toString());
        }
        return user;
    }
}
