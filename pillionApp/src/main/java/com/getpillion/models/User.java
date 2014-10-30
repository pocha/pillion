package com.getpillion.models;

import android.util.Log;

import com.getpillion.common.Helper;
import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

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
    public String profilePicUrl;
    public String languages;
    public String interests;


    public User(){}


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
