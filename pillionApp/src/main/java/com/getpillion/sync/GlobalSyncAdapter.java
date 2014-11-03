package com.getpillion.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.getpillion.models.Route;

import java.util.List;

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
            List<Route> routes = Route.find(Route.class,"is_synced = 0");
            Log.d("udinic","Send data to server");
            for (Route route:routes){
                route.isSynced = true;
                route.save(true);
            }
            Log.d("udinic","Done syncing");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
