package com.getpillion;

import android.os.Bundle;

import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;

public class MyRoutesActivity extends ExtendMeSherlockWithMenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(),
                Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.route_list);

    }
}