package com.getpillion.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.getpillion.R;
import com.getpillion.models.User;
import com.getpillion.models.WorkHistory;

import java.net.URL;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileFragment extends Fragment {


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        View v = inflater.inflate(R.layout.fragment_profile, null);
        ButterKnife.inject(this, v);
        return v;
    }

    @InjectView(R.id.noProfileData)
    LinearLayout noProfileData;
    @InjectView(R.id.profile)
    ScrollView profile;
    @InjectView(R.id.name) TextView name;
    @InjectView(R.id.profilePic)
    ImageView profilePic;
    @InjectView(R.id.title) TextView title;
    @InjectView(R.id.fieldOfWork) TextView fieldOfWork;
    @InjectView(R.id.workHistory)
    ListView workHistory;
    @InjectView(R.id.noWorkHistory) TextView noWorkHistory;
    @InjectView(R.id.languages) TextView languages;
    @InjectView(R.id.interests) TextView interests;

    public void fillUserData(final User user){
        if (user.name == null)
            return;
        noProfileData.setVisibility(View.GONE);
        profile.setVisibility(View.VISIBLE);

        name.setText(user.name);
        title.setText(user.title);
        fieldOfWork.setText(user.fieldOfWork);
        workHistory.setEmptyView(noWorkHistory);
        workHistory.setAdapter(
                new WorkHistoryAdapter(getActivity(),
                        WorkHistory.find(WorkHistory.class,"user = ?",String.valueOf(user.getId()))
                )
        );
        Helper.setListViewHeightBasedOnChildren(workHistory);

        languages.setText(user.languages);
        interests.setText(user.interests);

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

                try {
                    profilePic.setImageBitmap(image);
                } catch (Exception e){
                    profilePic.setImageDrawable(getResources().getDrawable(R.drawable.action_people));
                    Log.e("MyProfileActivity", "Error fetching user image ", e);
                }
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
                viewHolder.main.setTextAppearance(getActivity(),android.R.style.TextAppearance_Small);
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


}