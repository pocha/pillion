package com.getpillion.models;

import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;

import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Field;
/**
 * Created by pocha on 03/11/14.
 */
public class SyncSugarRecord<T> extends SugarRecord<T> implements Serializable {
    @SerializedName("global_id")
    public Long globalId = null;

    @SerializedName("is_updated")
    public boolean isUpdated = false; //TODO set this flag true to show whats updated to the user

    @SerializedName("updated__at")
    public Long updatedAt = null;

    @SerializedName("is_deleted")
    public boolean isDeleted = false;

    @SerializedName("is_synced")
    public boolean isSynced = false;

    @Ignore
    public ArrayList<String> excludeFields = new ArrayList<String>(
            Arrays.asList("id","isUpdated","isDeleted","updatedAt","isSynced","tableName", "excludeFields")
    );


    @Override
    public void save(){
        Log.d("SyncSugarRecord", "inside the overridden save for " + this.getClass().getSimpleName());
        //check if the record is deleted from upstream
        if (isDeleted){
            this.deleteWithoutSync();
            return;
        }
        super.save();

        try {
            sendForSync("update",false);
        }catch (Exception e){ //dummy as exception is only thrown for doSync
            Log.e("SyncSugarRecord","Error in sendForSync", e);
        }

    }

    @Override
    public void delete(){
        super.delete();
        try {
            sendForSync("delete",false);
        }catch (Exception e){
            Log.e("SyncSugarRecord","Error in sendForSync", e);
        }
    }

    public void sendForSync(String syncType, boolean isSynchronous) throws Exception{
        //call requestSync of SyncAdapter
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off

        bundle.putString("className",this.getClass().getName());
        bundle.putString("simpleClassName",this.getClass().getSimpleName());
        bundle.putLong("id",this.getId());
        //bundle.putString("objectJson",this.toJson());
        //TODO change this to Parcelable later
        bundle.putString("syncType",syncType);
        if (isSynchronous)
            doSync(bundle);
        else {
            ContentResolver.requestSync(Helper.mAccount, Constant.AUTHORITY, bundle);
            //ContentResolver.setIsSyncable(Helper.mAccount, Constant.AUTHORITY, 1);
        }

        Log.d("SyncSugarRecord", "end of overridden save");

    }


    public static Object myFindById(String simpleClassName, Long id){
        Object obj = null;
        if (simpleClassName.equals("Route")) {
            obj = Route.findById(Route.class,id);
        }
        if (simpleClassName.equals("Ride")) {
            obj = Ride.findById(Ride.class,id);
        }
        if (simpleClassName.equals("Vehicle")) {
            obj = Vehicle.findById(Vehicle.class,id);
        }
        if (simpleClassName.equals("User")) {
            obj = User.findById(User.class,id);
        }
        if (simpleClassName.equals("RideUserMapping")) {
            obj = RideUserMapping.findById(RideUserMapping.class,id);
        }
        if (simpleClassName.equals("WorkHistory")) {
            obj = WorkHistory.findById(WorkHistory.class,id);
        }
        return obj;
    }


    public static void doSync(Bundle extras) throws Exception {

        Log.d("udinic","Send data to server");
        String className = extras.getString("className");
        Class<?> objectClass = Class.forName(className);
        Log.d("udinic","className " + extras.getString("className"));
        Log.d("udinic","objectClass " + objectClass);


        Object obj = null;
        String simpleClassName = extras.getString("simpleClassName");
        Long id = extras.getLong("id");
        obj = SyncSugarRecord.myFindById(simpleClassName,id);
        //objectClass.getMethod("myFindById", String.class , Long.TYPE).invoke(obj, simpleClassName, extras.getLong("id"));

        Long global_id = (Long)objectClass.getField("globalId").get(obj);
        Log.d("udinic","globalId recovered " + global_id);
        String objJson = (String)objectClass.getMethod("toJson").invoke(obj);
        Log.d("udinic","json created from the object - " + objJson);
       

        String postParams = objJson;

        String jsonOutput = null;
        String syncType = extras.getString("syncType");

        if (syncType.equals("update")) {
            if (global_id == null)
               jsonOutput = Helper.postData(Constant.NEW_RECORD_URL + "/" + extras.getString("simpleClassName"), postParams);
            else
               jsonOutput = Helper.postData(Constant.UPDATE_RECORD_URL + "/" + extras.getString("simpleClassName"), postParams);
        }
        else { //delete
            Helper.postData(Constant.DELETE_RECORD_URL + "/" + extras.getString("simpleClassName"), postParams);
        }

        if (jsonOutput != null) {
            Log.d("SyncSugarRecord","Data received from server - " + jsonOutput);
            JSONObject json = new JSONObject(jsonOutput);

            objectClass.getField("globalId").set(obj, json.getLong("global_id"));
            objectClass.getField("updatedAt").set(obj, json.getLong("updated__at"));

            objectClass.getMethod("saveWithoutSync").invoke(obj);

            Object fresh_obj = SyncSugarRecord.myFindById(simpleClassName,id);
            Log.d("SyncSugarRecord","Saved object in json - " + objectClass.getMethod("toJson").invoke(fresh_obj));
        }

        Log.d("udinic","Done syncing");

    }

    public void saveWithoutSync(){
        super.save();
    }
    public void deleteWithoutSync(){ super.delete();}

    public String toJson(){
        if (this.globalId == null) {
            //global id not found, see if it has been updated in the background
            this.globalId = this.findById(this.getClass(),this.getId()).globalId;
            Log.d("SyncSugarRecord","updated globalId from db " + this.globalId);
        }

        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new ExcludeFields())
                //.serializeNulls() //<-- uncomment to serialize NULL fields as well
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                //.disableInnerClassSerialization() //had no effect on output. Taking care of it in Exclusion Strategy
                .create();
        String json = gson.toJson(this);

        return json.replaceAll("\"global_id\":","\"id\":");
    }

    public class ExcludeFields implements ExclusionStrategy {

        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        public boolean shouldSkipField(FieldAttributes f) {
            for (String field:excludeFields){
                if (f.getName().equals(field))
                    return true;
            }
            /*if (f.getDeclaredClass().getSimpleName().equals("User"))
                return true;*/

            return false;
            /*return  f.getName().equals("id") ||
                    f.getName().equals("isUpdated") ||
                    f.getName().equals("isDeleted") ||
                    f.getName().equals("updatedAt") || //we need to keep this. this will be updated from the server
                    f.getName().equals("tableName") ||
                    f.getName().equals("isSynced");*/
        }
    }

    public boolean update(Object newInstance){
        boolean isSave = false;
        for (Field f: newInstance.getClass().getDeclaredFields()){
            try {
                Log.d("SyncSugarRecord","Superclass of field "+ f.getName() + " is " + f.getType().getSuperclass());
                if (f.getAnnotation(Ignore.class) == null &&
                        !SyncSugarRecord.class.equals(f.getType().getSuperclass()) &&
                        !f.get(newInstance).equals(f.get(this))
                    ) {
                        Log.d("SyncSugarRecord","Updated field " + f.getName() + " found for " + this.getClass().getSimpleName());
                        f.set(this,f.get(newInstance));
                        isSave = true;
                    }
            }
            catch (Exception e){
                Log.e("SyncSugarRecord","Error while updating " + this.getClass().getSimpleName(),e);
            }
        }
        return isSave;
    }

}
