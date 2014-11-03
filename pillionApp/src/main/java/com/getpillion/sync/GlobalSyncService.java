package com.getpillion.sync;

import android.content.Intent;
import android.os.IBinder;
import android.app.Service;

/**
 * Created by pocha on 03/11/14.
 */
public class GlobalSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static GlobalSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new GlobalSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}