package com.getpillion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.getpillion.models.Ride;

import java.util.ArrayList;

public class RideAdapter extends ArrayAdapter<Ride> {
	private final Context context;
	private ArrayList<Ride> values;
	
	private String appName;
	private String appPackage;
	private String shareFlag = "0";
	private CheckBox itemCheckBox = null;
	private String FB_USER_ID;
	SharedPreferences settings = null;

	public RideAdapter(Context context, ArrayList<Ride> values) {
		super(context, R.layout.route, values);
		this.context = context;
		this.values = values;
		/*FB_USER_ID = facebookUserID;
		settings = context
				.getSharedPreferences(Constant.PREFS_NAME, 0);*/
	}
/*
	public void populateData(FeedBO[] values) {
		this.values = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			this.values[i] = values[i].toString();
		}
	}
*/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("RideAdapter","inside RideAdapter getView " + position);
		/*if ( position == (values.length - 1) ) {
			//Toast.makeText(context, "Reached bottom", Toast.LENGTH_SHORT).show();
		}*/
		RideViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.route, parent, false);
			viewHolder = new RideViewHolder();
			viewHolder.from = (TextView) convertView.findViewById(R.id.from);
            viewHolder.to = (TextView) convertView.findViewById(R.id.to);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(viewHolder);
		} else {
			viewHolder = (RideViewHolder) convertView.getTag();
		}

		Ride ride = values.get(position);

        Log.d("RideAdapter","Rendering ride with id " + ride.getId() + ", globalId " + ride.globalId + " and time " + ride.timeLong);
/*		route.populateFromString(selectedVal);
		
    	final CheckBox viewHolderCheckBox = viewHolder.shareCheckbox;

		viewHolder.shareCheckbox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				if (cb.isChecked()) {
					shareFlag = "1";
				} else {
					shareFlag = "0";
				}
				appName = feed.getAppName();
				appPackage = feed.getAppPackage();
				itemCheckBox = viewHolderCheckBox;
				confirmShare();
				// Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		});
*/
		viewHolder.position = position;
        viewHolder.rideId = ride.getId();
		viewHolder.from.setText(ride.route.origin);
		viewHolder.to.setText(ride.route.dest);
		viewHolder.time.setText(ride.getAmPmTime());

/*
		viewHolder.friendNewApp.setVisibility(LinearLayout.GONE);
		if (feed.getAppPackage().indexOf("FACEBOOK_USER:") >= 0) {
			viewHolder.actionDiv.setVisibility(LinearLayout.GONE);
			viewHolder.totalFriendsDiv.setVisibility(LinearLayout.GONE);
			if ( feed.getTotalNewApps() > 0 ) {
				viewHolder.totalNew.setText(feed.getTotalNewApps() + "");
				viewHolder.friendNewApp.setVisibility(LinearLayout.VISIBLE);
			}
		} else if (!feed.getIconResourceID().equalsIgnoreCase("NA")) {
			// Share my apps
			viewHolder.actionDiv.setVisibility(LinearLayout.GONE);
			viewHolder.shareCheckboxDiv.setVisibility(LinearLayout.VISIBLE);
			String shareSetting = settings.getString("no.share." + feed.getAppPackage(), "");
			if ( shareSetting.equals("0")) {
				viewHolder.shareCheckbox.setChecked(false);
			} else {
				viewHolder.shareCheckbox.setChecked(true);
			}
		} else {

			viewHolder.actionDiv.setVisibility(LinearLayout.VISIBLE);
			if (feed.getAllFriendsMode().equals("1")) {
				viewHolder.totalFriendsDiv.setVisibility(LinearLayout.VISIBLE);
			} else {
				viewHolder.totalFriendsDiv.setVisibility(LinearLayout.GONE);
			}
			if (feed.getIsInstalled().equals("1")) {
				viewHolder.installText.setText("Installed");
				viewHolder.googlePlayIcon.setVisibility(View.GONE);
				viewHolder.installIcon.setVisibility(View.VISIBLE);
				viewHolder.installText.setVisibility(View.VISIBLE);
			} else {
				viewHolder.installText.setText("Install");
				viewHolder.installIcon.setVisibility(View.GONE);
				viewHolder.googlePlayIcon.setVisibility(View.VISIBLE);
				viewHolder.installText.setVisibility(View.VISIBLE);
			}
		}
		
		if ( feed.getFriendView() == 1 ) {
			viewHolder.rightArrowDiv.setVisibility(LinearLayout.VISIBLE);
		} else {
			viewHolder.rightArrowDiv.setVisibility(LinearLayout.GONE);
		}
		
		
		if (feed.getIsItNew() == 1) {
			//viewHolder.newApp.setVisibility(View.VISIBLE);
			viewHolder.installText.setTextColor(Color.RED);
			viewHolder.installText.setText("New");
		} else {
			viewHolder.installText.setTextColor(Color.GRAY);
		}

		// new DownloadImageTask(viewHolder.appIcon)
		// .execute(route_list.getAppIcon());

		// new ThumbnailTask(position, viewHolder).execute(route_list.getAppIcon());
		viewHolder.appIcon.setImageDrawable(null);
		if (feed.getAppIcon().equals("NA")) {
			viewHolder.appIcon.setVisibility(View.GONE);
			viewHolder.appIconLocal.setVisibility(View.VISIBLE);
			viewHolder.appIconLocal.setImageDrawable(Session.hm.get(feed
					.getIconResourceID()));
		}
*/
		// imageView.setImageResource(R.drawable.no);
		return convertView;
	}

	public class RideViewHolder {
        public int position;
        public long rideId;
		public TextView from;
        public TextView to;
        public TextView time;
	}

/*	private static class ThumbnailTask extends AsyncTask {
		private int mPosition;
		private RouteViewHolder mHolder;

		public ThumbnailTask(int position, RouteViewHolder holder) {
			mPosition = position;
			mHolder = holder;
		}

		@Override
		protected Object doInBackground(Object... urls) {
			String urldisplay = (String) urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				// e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Object bitmap) {
			if (mHolder.position == mPosition) {
				if (bitmap != null) {
					mHolder.appIcon.setImageBitmap((Bitmap) bitmap);
				}
			}
		}

	}

	public void confirmShare() {
		updateUserAppShareSetting();
	}
	
	public void updateUserAppShareSetting() {
		try {
			new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
						postParams.add(new BasicNameValuePair("facebookUserID",
								FB_USER_ID));
						postParams.add(new BasicNameValuePair("app_package_name", appPackage));
						postParams.add(new BasicNameValuePair("share_flag", shareFlag));
						String url = Constant.SERVER + Constant.USER_APP_SHARE;
						Helper.postData(url, postParams);
						
						SharedPreferences.Editor editor = settings
								.edit();
						editor.putString("no.share." + appPackage, shareFlag);
						editor.commit();
						
					} catch (Exception e) {

					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {

				}
			}.execute();
		} catch (Exception ex) {
		}
	}
*/
}
