package com.getpillion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.getpillion.common.Constant;
import com.getpillion.common.ProfileFragment;
import com.getpillion.models.RideUserMapping;
import com.getpillion.models.User;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class UserProfileActivity extends ExtendMeSherlockWithMenuActivity implements ActionBar.TabListener {

    private ActionBar.Tab pickupDropTab, profileTab, selectedTab;
    private RideUserMapping rideUserMapping = null;
    private User user = null;
    private Boolean amIOwner = null;

    @InjectView(R.id.pickupDrop)
    LinearLayout pickupDropView;
    @InjectView(R.id.accept)
    Button acceptButton;
    @InjectView(R.id.reject)
    Button rejectButton;
    @InjectView(R.id.call)
    Button callButton;
    @InjectView(R.id.pickUp) TextView pickUp;
    @InjectView(R.id.drop)
    TextView drop;
    @InjectView(R.id.distance) TextView distance;
    @InjectView(R.id.cost) TextView cost;
    @InjectView(R.id.primaryButtonLayout) LinearLayout bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);
        ButterKnife.inject(this);

        rideUserMapping = RideUserMapping.findById(RideUserMapping.class, getIntent().getExtras().getLong("rideUserMappingId"));
        user = User.findById(User.class, rideUserMapping.userId);

        //fill user data in the fragment
        ((ProfileFragment)getSupportFragmentManager().findFragmentById(R.id.profile)).fillUserData(user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        if (rideUserMapping.isOwner) {
            ((ProfileFragment)getSupportFragmentManager().findFragmentById(R.id.profile)).getView().setVisibility(View.VISIBLE);
            pickupDropView.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            return;
        }

        //show tabs only for regular users
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        profileTab = actionBar.newTab().setText("Profile");
        profileTab.setTag("profile");
        profileTab.setTabListener(this);
        actionBar.addTab(profileTab);

        pickupDropTab = actionBar.newTab().setText("Pickup Drop");
        pickupDropTab.setTag("pickupDrop");
        pickupDropTab.setTabListener(this);
        actionBar.addTab(pickupDropTab);

        //fill in user's route data
        //Route route = rideUserMapping.route;
        pickUp.setText(rideUserMapping.origin);
        drop.setText(rideUserMapping.dest);
        distance.setText(rideUserMapping.distance + " km");
        cost.setText(" .. I am too exhausted to implement this now. Sorry :( ..");

        //modify button content
        if (rideUserMapping.status == Constant.REJECTED) {
            rejectButton.setText("Rejected");
            rejectButton.setEnabled(false);
        }
        else if (rideUserMapping.status == Constant.ACCEPTED) {
            acceptButton.setText("Accepted");
            acceptButton.setEnabled(false);
        }

        try {
            amIOwner = RideUserMapping.find(RideUserMapping.class, "ride_id = ? AND user_id = ?",
                    String.valueOf(rideUserMapping.rideId),
                    String.valueOf(sharedPref.getLong("userId", 0L))
            ).get(0).isOwner;
        }catch (Exception e){
            //I am not part of the ride. amIOwner stays null
        }


    }

    @OnClick(R.id.accept) void onAcceptButtonClick(View v){
        if (amIOwner != null && amIOwner)
            if (rideUserMapping.status != Constant.ACCEPTED) {
                rideUserMapping.status = Constant.ACCEPTED;
                rideUserMapping.save();
                Toast.makeText(UserProfileActivity.this,"You have accepted the request",Toast.LENGTH_LONG).show();
            }
        else
           Toast.makeText(UserProfileActivity.this,"Nice try. Only ride creator can do that ;-)",Toast.LENGTH_LONG).show();

        finish();
    }
    @OnClick(R.id.reject) void onRejectButtonClick(View v){
        if (amIOwner!= null && amIOwner)
            if (rideUserMapping.status != Constant.REJECTED) {
                rideUserMapping.status = Constant.REJECTED;
                rideUserMapping.save();
                Toast.makeText(UserProfileActivity.this,"You have rejected the ride request",Toast.LENGTH_LONG).show();
            }
        else
            Toast.makeText(UserProfileActivity.this,"Nice try. Only ride creator can do that ;-)",Toast.LENGTH_LONG).show();

        finish();
    }
    @OnClick(R.id.call) void onCallButtonClick(View v){
        if (amIOwner != null) {//person viewing is part of the ride
            String uri = "tel:" + user.phone.trim() ;
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);
        }
        else
            Toast.makeText(UserProfileActivity.this,"Sorry. You need to be part of this ride to use that feature.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        selectedTab = tab;
        if (tab.getTag() == "pickupDrop"){
            ((ProfileFragment)getSupportFragmentManager().findFragmentById(R.id.profile)).getView().setVisibility(View.GONE);
            pickupDropView.setVisibility(View.VISIBLE);
        }
        else {
            ((ProfileFragment)getSupportFragmentManager().findFragmentById(R.id.profile)).getView().setVisibility(View.VISIBLE);
            pickupDropView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }


}