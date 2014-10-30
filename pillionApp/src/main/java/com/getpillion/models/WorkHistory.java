package com.getpillion.models;

import android.util.Log;

import com.getpillion.common.Helper;
import com.google.code.linkedinapi.schema.Education;
import com.google.code.linkedinapi.schema.EndDate;
import com.google.code.linkedinapi.schema.Position;
import com.google.code.linkedinapi.schema.StartDate;
import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pocha on 29/10/14.
 */
public class WorkHistory extends SugarRecord<WorkHistory> {
    public Long globalId;
    public User user;
    public String data;
    public String duration;
    
    public WorkHistory(){}

    public WorkHistory(Position p, User user){
        this.user = user;
        this.data = p.getTitle() + " at " + p.getCompany().getName();
        EndDate endDate = p.getEndDate();
        StartDate startDate = p.getStartDate();
        this.duration = String.valueOf(startDate.getMonth()) + "/" + String.valueOf(startDate.getYear())
                        + " to " +
                String.valueOf(endDate.getMonth()) + "/" + String.valueOf(endDate.getYear());
        this.save();
    }

    public WorkHistory(Education e, User user){
        this.user = user;
        this.data = e.getSchoolName();
        EndDate endDate = e.getEndDate();
        StartDate startDate = e.getStartDate();
        this.duration = String.valueOf(startDate.getMonth()) + "/" + String.valueOf(startDate.getYear())
                + " to " +
                String.valueOf(endDate.getMonth()) + "/" + String.valueOf(endDate.getYear());
        this.save();
    }

    public static WorkHistory createOrUpdateFromJson(JSONObject jsonUser, User user){
        WorkHistory position = null;
        try {
            List<WorkHistory> positions = WorkHistory.find(WorkHistory.class, "global_id = ?", String.valueOf(jsonUser.getLong("globalId")));
            if (positions.isEmpty()){
                position = new WorkHistory();
                position.globalId = jsonUser.getLong("globalId");
                position.user = user;
            }
            else
                position = positions.get(0);

            position.data = Helper.updateFromJsonField(position.data, jsonUser.optString("data"));
            position.duration = Helper.updateFromJsonField(position.duration, jsonUser.optString("duration"));
            position.save();
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.d("LinkedinPosition.java", "Error in createOrUpdateFromJSON " + e.toString());
        }
        return position;
    }
}
