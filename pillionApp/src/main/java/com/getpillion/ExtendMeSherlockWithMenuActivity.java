package com.getpillion;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.getpillion.common.Helper;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class ExtendMeSherlockWithMenuActivity extends SherlockFragmentActivity {

    private SlidingMenu menu = null;
    public ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = new SlidingMenu(this);
        Helper.createMenu(menu,this);
        getSupportActionBar().setHomeButtonEnabled(true);
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

}
