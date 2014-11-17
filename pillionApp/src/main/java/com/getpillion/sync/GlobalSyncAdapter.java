package com.getpillion.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.getpillion.models.SyncSugarRecord;

/**
 * Created by pocha on 03/11/14.
 */
public class GlobalSyncAdapter  extends AbstractThreadedSyncAdapter {
    private final AccountManager mAccountManager;



    public GlobalSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);
        Log.d("udinic", "done with constructor of GlobalSyncAdapter");
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d("udinic", "onPerformSync for account[" + account.name + "]");
        try {
            SyncSugarRecord.doSync(extras);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("udinic","Error count " + syncResult.stats.numParseExceptions);
            syncResult.stats.numParseExceptions++;
        }
    }
}
