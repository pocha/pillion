package com.getpillion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.bugsense.trace.BugSenseHandler;
import com.getpillion.common.Constant;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NewRouteActivitySelectMode extends SherlockFragmentActivity {

    @InjectView(R.id.both)
    RadioButton both;
    @InjectView(R.id.onlyOfferRide)
    RadioButton onlyOfferRide;
    @InjectView(R.id.needRide)
    RadioButton needRide;

    @OnClick(R.id.button) void onSubmit(View v){
        Log.d("NewRouteActivity","inside onclick");
        Intent intent = new Intent(NewRouteActivitySelectMode.this, NewRouteActivityOnStart.class);
        if (both.isChecked() || onlyOfferRide.isChecked())
            intent.putExtra("offerRide", true);

        if (both.isChecked() || needRide.isChecked())
            intent.putExtra("requestRide", true);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(getApplicationContext(),
                Constant.BUGSENSE_API_KEY);
        setContentView(R.layout.activity_new_route_select_mode);
        ButterKnife.inject(this);
    }
}