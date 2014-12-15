package com.getpillion;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class ExtendMeSherlockWithMenuActivity extends SherlockFragmentActivity {

    private SlidingMenu menu = null;
    public ProgressDialog progress;
    public SharedPreferences sharedPref;
    public Editor sharedPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BugSenseHandler.initAndStartSession(getApplicationContext(), Constant.BUGSENSE_API_KEY);
        sharedPref = getApplicationContext().getSharedPreferences(
                Constant.PREFS_NAME, 0);
        sharedPrefEditor = sharedPref.edit();
        menu = new SlidingMenu(this);
        Helper.createMenu(menu,this);
        getSupportActionBar().setHomeButtonEnabled(true);
        Helper.createSyncAccount(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == android.R.id.home) {
                menu.toggle();
                return true;
            } else {
                return true;
            }
        } catch (Exception ex) {
            return true;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                menu.toggle();
                return true;
        }
        return super.onKeyDown(keycode, e);
    }

}
