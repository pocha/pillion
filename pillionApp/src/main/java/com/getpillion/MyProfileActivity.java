package com.getpillion;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.getpillion.common.LinkedinDialog;
import com.getpillion.common.ProfileFragment;
import com.getpillion.models.User;
import com.getpillion.models.WorkHistory;
import com.google.code.linkedinapi.schema.Education;
import com.google.code.linkedinapi.schema.Language;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.Position;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MyProfileActivity extends ExtendMeSherlockWithMenuActivity implements IPostExecuteCallback,ActionBar.TabListener {

    @InjectView(R.id.profileContainer)
    LinearLayout profileContainer;

    @OnClick(R.id.loginButton) void SocialNwManagerTakeCharge(View v){
        LinkedinDialog.Login(this,this);
    }

    private User user = null;
    private ActionBar.Tab profileTab, confirmPhoneTab;

    @OnClick(R.id.done) void takeUserBack(View v){
        if (getCallingActivity() != null)
            setResult(getIntent().getIntExtra("requestCode", 0));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);
        ButterKnife.inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        profileTab = actionBar.newTab().setText("Profile");
        profileTab.setTag("profile");
        profileTab.setTabListener(this);
        actionBar.addTab(profileTab);

        confirmPhoneTab = actionBar.newTab().setText("Confirm Phone");
        confirmPhoneTab.setTag("confirmPhone");
        confirmPhoneTab.setTabListener(this);
        actionBar.addTab(confirmPhoneTab);

        user = User.findById(User.class, sharedPref.getLong("userId",0L));
        if (user.name == null){
            profileTab.select();
        }
        else {
            ((ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.profile)).fillUserData(user);
            if ( user.phone == null )
                confirmPhoneTab.select();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (tab.getTag().equals("profile")){
            profileContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager().findFragmentById(R.id.confirmPhone).getView().setVisibility(View.GONE);
        }
        else {
            getSupportFragmentManager().findFragmentById(R.id.confirmPhone).getView().setVisibility(View.VISIBLE);
            profileContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPostExecute(Person p) {
        //delete any previous data
        WorkHistory.deleteAll(WorkHistory.class,"user = ?", String.valueOf(user.getId()));
        /*Long id = user.getId();
        User.findById(User.class,id).delete();

        user = new User();
        user.save();
        user.setId(id); // reset the same id so that all previous user records are still linked
        user.save();*/

        /*sharedPrefEditor.putLong("userId",user.getId());
        sharedPrefEditor.commit();*/

        //store data in db - try catch block to keep continuing execution as you aint sure what fields are present in user profile
        try {user.name = p.getFirstName() + " " + p.getLastName(); } catch (Exception e){ user.name = null;}
        try {user.title = p.getHeadline();} catch (Exception e){ user.title = null;}
        try {user.profilePicUrl = p.getPictureUrl();} catch (Exception e){ user.profilePicUrl = null;}
        try {user.fieldOfWork = p.getIndustry();} catch (Exception e){user.fieldOfWork = null;}

        try {
            for (Position position : p.getPositions().getPositionList()) {
                Log.d("MyProfileActivity","position parsed = " + position.getCompany().getName());
                try { new WorkHistory(position, user); } catch(Exception e){}
            }
        }catch (Exception e){
            Log.e("MyProfileActivity","error parsing position", e);
        }
        
        try {
            for (Education e : p.getEducations().getEducationList()) {
                Log.d("MyProfileActivity","education parsed = " + e.getSchoolName());
                try{ new WorkHistory(e, user);} catch(Exception j){}
            }
        }catch (Exception e){}
        
        try {
            ArrayList<String> languages = new ArrayList<String>();
            for (Language l : p.getLanguages().getLanguageList()) {
                languages.add(l.getLanguage().getName());
            }
            user.languages = languages.toString().replaceAll("(^\\[|\\]$)", "").replace(", ", ",");
        }catch (Exception e){ user.languages = null;}
        
        try{user.interests = p.getInterests();}catch(Exception e){ user.interests = null;}
        
        user.save();
        //TODO send user data to the server

        ((ProfileFragment)getSupportFragmentManager().findFragmentById(R.id.profile)).fillUserData(user);
        
        //name.setText("Welcome " + p.getFirstName() + " "+ p.getLastName());
        /*Log.i("Complete object dump ", p.toString());
        Log.i("FirstName"," : " + p.getFirstName());
        Log.i("LastName"," : " + p.getLastName());
        Log.i("User Summary"," : " + p.getSummary());
        Log.i("Headline"," : " + p.getHeadline());
        Log.i("Interest"," : "+p.getInterests());

        //----------------------------------------------------

        for (Position position:p.getPositions().getPositionList())
        {
            Log.i("position "," : " + position.getTitle());
            Log.i("comp "," : " + position.getCompany().getName());

        }

        for (Education education:p.getEducations().getEducationList())
        {
            Log.i("Degree "," : "+ education.getDegree());
            Log.i("School name "," : "+ education.getSchoolName());

        }
        Log.i("Language "," : ");
        for(Language language:p.getLanguages().getLanguageList())
        {
            Log.i(""," : "+language.getLanguage().getName());
        }
        Log.i("Skill "," : ");
        for(Skill skill:p.getSkills().getSkillList())
        {
            Log.i(""," : "+skill.getSkill().getName());
        }

        Log.i("Interests"," : " + p.getInterests());

        //Log.i("Honor"," : "+p.getHonors());
        //----------------------------------------------------*/
    }

}