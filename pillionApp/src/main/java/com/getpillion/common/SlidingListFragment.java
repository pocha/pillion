package com.getpillion.common;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.getpillion.AllRidesActivity;
import com.getpillion.MyProfileActivity;
import com.getpillion.MyRidesActivity;
import com.getpillion.MyRoutesActivity;
import com.getpillion.MyVehiclesActivity;
import com.getpillion.R;

public class SlidingListFragment extends ListFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		/*
		adapter.add(new SampleItem("All Friends Apps", R.drawable.all_friends));
		adapter.add(new SampleItem("Who is using what", R.drawable.question));
		adapter.add(new SampleItem("Male Friends Apps", R.drawable.male));
		adapter.add(new SampleItem("Female Friends Apps", R.drawable.female));
		adapter.add(new SampleItem("All New Apps", R.drawable.new_app));
		adapter.add(new SampleItem("School Friends Apps", R.drawable.school));
		adapter.add(new SampleItem("Office Friends Apps", R.drawable.office));
		adapter.add(new SampleItem("Invite friends", R.drawable.invite_friend));
		*/
		
		adapter.add(new SampleItem("All Routes", R.drawable.office));
		adapter.add(new SampleItem("My Routes", R.drawable.all_friends));
		//adapter.add(new SampleItem("New Apps", R.drawable.new_app1));
		adapter.add(new SampleItem("My Rides", R.drawable.my_apps));
		//adapter.add(new SampleItem("Find More Friends", R.drawable.invite_friend));
		adapter.add(new SampleItem("My Vehicles", R.drawable.like));
		adapter.add(new SampleItem("My Profile", R.drawable.settings));
		
		setListAdapter(adapter);
	}

	private class SampleItem {
		public String tag;
		public int iconRes;
		public boolean enabled = true;

		public SampleItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}
	
	
	public class FeedViewHolder {
		public ImageView icon;
		public TextView title;
	}

	private class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			FeedViewHolder viewHolder;
			try {
				if (convertView == null) {
					convertView = LayoutInflater.from(getContext()).inflate(
							R.layout.row, null);
					viewHolder = new FeedViewHolder();
					viewHolder.title = (TextView) convertView.findViewById(R.id.row_title);
					viewHolder.icon = (ImageView) convertView.findViewById(R.id.row_icon);
                    viewHolder.icon.setVisibility(View.VISIBLE);
					convertView.setTag(viewHolder);
				} else {
					viewHolder = (FeedViewHolder) convertView.getTag();
				}
				
				try {
					viewHolder.icon.setImageResource(getItem(position).iconRes);
				} catch (Exception ex) { }
				viewHolder.title.setText(getItem(position).tag);
				
				return convertView;
			} catch (Exception ex) {
				return null;
			}
		}

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				/*
				if (arg2 == 7) {
					Intent intent = new Intent(getActivity(),InviteFriendActivity.class);
					getActivity().startActivity(intent);
					return;
				}
				Intent intent = new Intent(getActivity(),HomeActivity.class);
				if (arg2 == 0) {
					intent.putExtra("type", "");
					intent.putExtra("title", "All Friends Apps");
				} else if (arg2 == 1) {
					intent.putExtra("type", "friend_app");
					intent.putExtra("title", "Select a Friend to see Apps");
				} else if (arg2 == 2) {
					intent.putExtra("type", "male");
					intent.putExtra("title", "Male Friends Apps");
				} else if (arg2 == 3) {
					intent.putExtra("type", "female");
					intent.putExtra("title", "Female Friends Apps");
				} else if (arg2 == 4) {
					intent.putExtra("type", "new");
					intent.putExtra("title", "All New Apps");
				} else if (arg2 == 5) {
					intent.putExtra("type", "school");
					intent.putExtra("title", "School Friends Apps");
				} else if (arg2 == 6) {
					intent.putExtra("type", "office");
					intent.putExtra("title", "Office Friends Apps");
				}
				getActivity().startActivity(intent);
				*/
				
				
				Intent intent = new Intent(getActivity(),AllRidesActivity.class);
				if (arg2 == 0) {
					/*intent.putExtra("type", "friend_app");
					intent.putExtra("title", "Friends");
					intent.putExtra("show_right_arrow", "1");*/
                    intent = new Intent(getActivity(),AllRidesActivity.class);
				} else if (arg2 == 1) {
					/*intent.putExtra("type", "all");
					intent.putExtra("title", "Top Friends Apps");*/
                    intent = new Intent(getActivity(),MyRoutesActivity.class);
				} else if (arg2 == 3) {
					//intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/appcovery"));
                    intent = new Intent(getActivity(), MyVehiclesActivity.class);
				} else if ( arg2 == 4 ) {
					intent = new Intent(getActivity(), MyProfileActivity.class);
				} else if ( arg2 == 2) {
					/*intent.putExtra("type", "my_apps");
					intent.putExtra("title", "Select Apps to Share");*/
                    intent = new Intent(getActivity(),MyRidesActivity.class);
				}
				startActivity(intent);
			}
		});
	}
}