package com.getpillion;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.getpillion.common.Helper;
import com.getpillion.common.LinkedinDialog;
import com.getpillion.models.User;
import com.getpillion.models.WorkHistory;
import com.google.code.linkedinapi.schema.Education;
import com.google.code.linkedinapi.schema.Language;
import com.google.code.linkedinapi.schema.Person;
import com.google.code.linkedinapi.schema.Position;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MyProfileActivity extends ExtendMeSherlockWithMenuActivity implements IPostLoginCallback{


    @OnClick(R.id.loginButton) void SocialNwManagerTakeCharge(View v){
        LinkedinDialog.Login(this,this);
    }

    @InjectView(R.id.noProfileData) LinearLayout noProfileData;
    @InjectView(R.id.profile) ScrollView profile;

    private User user = null;

    @InjectView(R.id.done)
    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_profile);
        ButterKnife.inject(this);
        if (getCallingActivity() != null){
            done.setVisibility(View.VISIBLE);
        }

        //get user data
        user = User.findById(User.class, sharedPref.getLong("userId",0L));
        if (user.name == null) { //no user data present
            profile.setVisibility(View.GONE);
            noProfileData.setVisibility(View.VISIBLE);
        }
        else {
            fillUserData(user);
        }

    }

    @Override
    public void postLoginCallback(Person p) {
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

        fillUserData(user);
        
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

    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.profilePic) ImageView profilePic;
    @InjectView(R.id.title) TextView title;
    @InjectView(R.id.fieldOfWork) TextView fieldOfWork;
    @InjectView(R.id.workHistory) ListView workHistory;
    @InjectView(R.id.noWorkHistory) TextView noWorkHistory;
    @InjectView(R.id.languages) TextView languages;
    @InjectView(R.id.interests) TextView interests;

    private void fillUserData(final User user){
        final ProgressDialog progressDialog = ProgressDialog.show(MyProfileActivity.this, "",
                "Fetching user image", true, false);

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return BitmapFactory.decodeStream(new URL(user.profilePicUrl).openConnection().getInputStream());
                } catch (Exception e) {
                    return BitmapFactory.decodeResource(getResources(),R.drawable.action_people);
                }
            }

            @Override
            protected void onPostExecute(Bitmap image) {
                noProfileData.setVisibility(View.GONE);
                profile.setVisibility(View.VISIBLE);

                name.setText(user.name);
                try {
                    profilePic.setImageBitmap(image);
                } catch (Exception e){
                    profilePic.setImageDrawable(getResources().getDrawable(R.drawable.action_people));
                    Log.e("MyProfileActivity","Error fetching user image ",e);
                }
                title.setText(user.title);
                fieldOfWork.setText(user.fieldOfWork);
                workHistory.setEmptyView(noWorkHistory);
                workHistory.setAdapter(
                        new WorkHistoryAdapter(getApplicationContext(),
                                WorkHistory.find(WorkHistory.class,"user = ?",String.valueOf(user.getId()))
                        )
                );
                Helper.setListViewHeightBasedOnChildren(workHistory);

                languages.setText(user.languages);
                interests.setText(user.interests);

                progressDialog.dismiss();
            }
        }.execute();


    }

    private class WorkHistoryAdapter extends ArrayAdapter<WorkHistory> {
        private final Context context;
        private List<WorkHistory> workHistories;

        public WorkHistoryAdapter(Context context, List<WorkHistory> users) {
            super(context, R.layout.row, users);
            this.context = context;
            this.workHistories = users;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.main = (TextView) convertView.findViewById(R.id.row_title);
                viewHolder.main.setTextAppearance(getApplicationContext(),android.R.style.TextAppearance_Small);
                viewHolder.main.setTextColor(getResources().getColor(R.color.BottomButton));
                viewHolder.secondary = (TextView) convertView.findViewById(R.id.secondaryText);
                viewHolder.secondary.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final WorkHistory workHistory = workHistories.get(position);
            viewHolder.position = position;
            viewHolder.main.setText(workHistory.data);
            viewHolder.secondary.setText(workHistory.duration);

            return convertView;
        }

        private class ViewHolder {
            public int position;
            public TextView main;
            public TextView secondary;
        }
    }

    @OnClick(R.id.done) void takeUserBack(View v){
        Toast.makeText(getApplicationContext(),"Profile successfully updated",Toast.LENGTH_LONG);
        setResult(getIntent().getIntExtra("requestCode", 0));
        finish();
    }
}