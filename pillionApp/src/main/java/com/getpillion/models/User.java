package com.getpillion.models;

import com.getpillion.common.Constant;
import com.google.gson.annotations.SerializedName;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pocha on 29/09/14.
 */

public class User extends SyncSugarRecord<User> {
    public String name = null;
    public String title = null;
    @SerializedName("field_of_work")
    public String fieldOfWork = null;
    public String phone = null;
    @SerializedName("profile_pic_url")
    public String profilePicUrl = null;
    public String languages = null;
    public String interests = null;

    public String reg_id; //needs to be sent to upstream but never received from upstream, server side logic put in place

    @Ignore
    public int status = Constant.CREATED; //needed to create users array during serialization & deserialization
    @Ignore
    @SerializedName("is_owner")
    public boolean isOwner = false;

    @Ignore
    @SerializedName("work_histories")
    public ArrayList<WorkHistory> workHistory = new ArrayList<WorkHistory>();

    public User(){}


    public static User updateFromUpstream(User upstreamUser){
        List<User> users = User.find(User.class,"global_id = ?",String.valueOf(upstreamUser.globalId));

        User user = null;
        if (users.isEmpty()){
            user = upstreamUser;
        }
        else {
            user = users.get(0);
            user.name = upstreamUser.name;
            user.title = upstreamUser.title;
            user.fieldOfWork = upstreamUser.fieldOfWork;
            user.profilePicUrl = upstreamUser.profilePicUrl;
            user.languages = upstreamUser.languages;
            user.interests = upstreamUser.interests;
            user.updatedAt = upstreamUser.updatedAt;
        }
        user.saveWithoutSync();

        if (upstreamUser.workHistory != null ){
            for (WorkHistory w:upstreamUser.workHistory) {
                WorkHistory.updateFromUpstream(w, user);
            }
        }

        return user;
    }

    @Override
    public String toJson(){
       excludeFields.add("status");
       excludeFields.add("isOwner");
       return super.toJson();
    }
}
