package com.getpillion.models;

import com.google.code.linkedinapi.schema.Education;
import com.google.code.linkedinapi.schema.EndDate;
import com.google.code.linkedinapi.schema.Position;
import com.google.code.linkedinapi.schema.StartDate;
import com.orm.dsl.Ignore;

import java.util.List;

/**
 * Created by pocha on 29/10/14.
 */
public class WorkHistory extends SyncSugarRecord<WorkHistory> {
    public User user;
    @Ignore
    public Long user_id = null; //for upstream
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

    public static WorkHistory updateFromUpstream(WorkHistory upstream, User user){
        WorkHistory position = null;
            List<WorkHistory> positions = WorkHistory.find(WorkHistory.class, "global_id = ?", String.valueOf(upstream.globalId));
            if (positions.isEmpty()){
                position = upstream;
                position.user = user;
            }
            else {
                position = positions.get(0);
                position.data = upstream.data;
                position.duration = upstream.duration;
                position.updatedAt = upstream.updatedAt;
            }
            position.saveWithoutSync();

        return position;
    }
    @Override
    public String toJson(){
        //these values will be used on the server for the time & date
        this.user_id = this.user.globalId;

        excludeFields.add("user");

        return super.toJson();
    }
}
