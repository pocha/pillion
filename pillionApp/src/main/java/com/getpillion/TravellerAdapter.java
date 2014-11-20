package com.getpillion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.getpillion.common.Constant;
import com.getpillion.models.RideUserMapping;
import com.getpillion.models.User;

import java.util.List;

public class TravellerAdapter extends ArrayAdapter<RideUserMapping> {
	private final Context context;
    private List<RideUserMapping> rideUsers;

	public TravellerAdapter(Context context, List<RideUserMapping> users) {
        super(context, R.layout.route, users);
        this.context = context;
        this.rideUsers = users;
    }
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.traveller, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final User user = User.findById(User.class,rideUsers.get(position).userId);
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

       //Log.d("TravellerAdapter.java","routeOwner " + routeOwner.name);
        if (rideUsers.get(position).isOwner) {
            if (user.name == null)
                viewHolder.name.setText("You (profile missing)");
            else
                viewHolder.name.setText(user.name + " (you)");
            viewHolder.title.setText("Owner");
        }
        else {
            viewHolder.name.setText(user.name);
            //these only visible to the owner as for others, these entries will not come
            if (rideUsers.get(position).status == Constant.REQUESTED)
                viewHolder.title.setText("waiting acceptance");
            else if (rideUsers.get(position).status == Constant.REJECTED)
                viewHolder.title.setText("rejected");
            else if (rideUsers.get(position).status == Constant.CANCELLED)
                viewHolder.title.setText("request cancelled");
            else
                viewHolder.title.setText(user.title);
        }

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

	public class ViewHolder {
        public int position;
		public TextView name;
        public TextView title;
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
