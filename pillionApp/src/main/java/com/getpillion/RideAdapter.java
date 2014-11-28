package com.getpillion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.getpillion.models.Ride;
import com.getpillion.models.RideUserMapping;

import java.util.ArrayList;
import java.util.Date;

public class RideAdapter extends ArrayAdapter<Ride> {
	private final Context context;
	private ArrayList<Ride> values;
    private Date today;
    private String activityType;

	public RideAdapter(Context context, ArrayList<Ride> values, String activityType) {
		super(context, R.layout.route, values);
		this.context = context;
		this.values = values;
        today = new Date();
        this.activityType = activityType;
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
			viewHolder.fromHeader = (TextView) convertView.findViewById(R.id.fromHeader);
            viewHolder.fromFooter = (TextView) convertView.findViewById(R.id.fromFooter);
            viewHolder.toHeader = (TextView) convertView.findViewById(R.id.toHeader);
            viewHolder.toFooter = (TextView) convertView.findViewById(R.id.toFooter);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.statusGrey = (TextView) convertView.findViewById(R.id.statusGrey);
            viewHolder.statusRed = (TextView) convertView.findViewById(R.id.statusRed);
            viewHolder.statusGreen = (TextView) convertView.findViewById(R.id.statusGreen);
            viewHolder.statusYellow = (TextView) convertView.findViewById(R.id.statusYellow);

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
		viewHolder.fromHeader.setText(Helper.getHeadFromLocation(ride.origin));
        viewHolder.fromFooter.setText(Helper.getFooterFromLocation(ride.origin));
        viewHolder.toHeader.setText(Helper.getHeadFromLocation(ride.dest));
        viewHolder.toFooter.setText(Helper.getFooterFromLocation(ride.dest));
		viewHolder.time.setText(ride.getAmPmTime());
        viewHolder.statusGrey.setVisibility(View.GONE);
        viewHolder.statusYellow.setVisibility(View.GONE);
        viewHolder.statusGreen.setVisibility(View.GONE);
        viewHolder.statusRed.setVisibility(View.GONE);

        RideUserMapping rideUserMapping = null;

        try {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    Constant.PREFS_NAME, 0);
            rideUserMapping = RideUserMapping.find(RideUserMapping.class, "user_id = ? AND ride_id = ?",
                    String.valueOf(sharedPref.getLong("userId",0L)),
                    String.valueOf(ride.getId())
            ).get(0);
        }
        catch (Exception e){
            rideUserMapping = RideUserMapping.find(RideUserMapping.class,"is_owner = 1 AND ride_id = ?",
                    String.valueOf(ride.getId())
            ).get(0);
        }


            switch (rideUserMapping.status) {
                case Constant.STARTED:
                case Constant.CHECKED_IN:
                case Constant.ACCEPTED:
                    if (Helper.compareDate(ride.dateLong, today) >= 0) {
                        if (rideUserMapping.status == Constant.STARTED)
                            viewHolder.statusGreen.setText("started");
                        else if (rideUserMapping.status == Constant.ACCEPTED)
                            viewHolder.statusGreen.setText("accepted");
                        else
                            viewHolder.statusGreen.setText("checked_in");

                        viewHolder.statusGreen.setVisibility(View.VISIBLE);
                    } else { //older journey. just show the date of journey
                        viewHolder.statusGrey.setText(Helper.niceDate(ride.dateLong));
                        viewHolder.statusGrey.setVisibility(View.VISIBLE);
                    }
                    break;
                case Constant.CANCELLED:
                case Constant.REJECTED:
                    if (rideUserMapping.status == Constant.CANCELLED)
                        viewHolder.statusGreen.setText("cancelled");
                    else
                        viewHolder.statusGreen.setText("rejected");
                    viewHolder.statusRed.setVisibility(View.VISIBLE);
                    break;
                case Constant.CREATED:
                    if (ride.isOffered) {
                        viewHolder.statusRed.setText("not-scheduled");
                        viewHolder.statusRed.setVisibility(View.VISIBLE);
                    }
                    else {
                        if (ride.dateLong != null) {
                            if (Helper.compareDate(ride.dateLong, today) >= 0) {
                                viewHolder.statusYellow.setText(Helper.niceDate(ride.dateLong));
                                viewHolder.statusYellow.setVisibility(View.VISIBLE);
                            } else {
                                viewHolder.statusGrey.setText(Helper.niceDate(ride.dateLong));
                                viewHolder.statusGrey.setVisibility(View.VISIBLE);
                            }
                        }
                        else { //office ride mostly
                            viewHolder.statusGrey.setText("daily");
                            viewHolder.statusGrey.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case Constant.REQUESTED:
                    viewHolder.statusYellow.setText("requested");
                    viewHolder.statusYellow.setVisibility(View.VISIBLE);
                    break;
                case Constant.SCHEDULED:
                    if (ride.dateLong != null) {
                        if (Helper.compareDate(ride.dateLong, today) >= 0) {
                            viewHolder.statusYellow.setText(Helper.niceDate(ride.dateLong));
                            viewHolder.statusYellow.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.statusGrey.setText(Helper.niceDate(ride.dateLong));
                            viewHolder.statusGrey.setVisibility(View.VISIBLE);
                        }
                    }
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

	public class RideViewHolder {
        public int position;
        public long rideId;
		public TextView fromHeader;
        public TextView fromFooter;
        public TextView toHeader;
        public TextView toFooter;
        public TextView time;
        public TextView statusGrey;
        public TextView statusRed;
        public TextView statusYellow;
        public TextView statusGreen;
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
