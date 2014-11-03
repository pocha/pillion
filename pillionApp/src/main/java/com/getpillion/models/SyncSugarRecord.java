package com.getpillion.models;

import android.content.ContentResolver;
import android.os.Bundle;
import android.util.Log;

import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.orm.SugarRecord;

/**
 * Created by pocha on 03/11/14.
 */
public class SyncSugarRecord<T> extends SugarRecord<T> {
    public boolean isSynced = false;
    public boolean isUpdated = false;

    @Override
    public void save(){
        Log.d("SyncSugarRecord","inside the overridden save");

        super.save();
        //call requestSync of SyncAdapter
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off
        ContentResolver.requestSync(Helper.mAccount, Constant.AUTHORITY, bundle);
        Log.d("SyncSugarRecord","end of overridden save");
        //post sync, update isSynced to true once syncAdapter syncs
    }

    public void save(boolean isSynced){
        this.isSynced = isSynced;
        super.save();
    }
}
