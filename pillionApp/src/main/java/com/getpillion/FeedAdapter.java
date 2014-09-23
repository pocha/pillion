package com.getpillion;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getpillion.common.Constant;
import com.getpillion.common.Helper;
import com.getpillion.common.Session;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;

import uk.co.senab.bitmapcache.CacheableImageView;

public class FeedAdapter extends ArrayAdapter<String> {
	private final Context context;
	private String[] values;
	
	private String appName;
	private String appPackage;
	private String shareFlag = "0";
	private CheckBox itemCheckBox = null;
	private String FB_USER_ID;
	SharedPreferences settings = null;

	public FeedAdapter(Context context, String[] values, String facebookUserID) {
		super(context, R.layout.feedrow, values);
		this.context = context;
		this.values = values;
		FB_USER_ID = facebookUserID;
		settings = context
				.getSharedPreferences(Constant.PREFS_NAME, 0);
	}

	public void populateData(FeedBO[] values) {
		this.values = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			this.values[i] = values[i].toString();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if ( position == (values.length - 1) ) {
			//Toast.makeText(context, "Reached bottom", Toast.LENGTH_SHORT).show();
		}
		FeedViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.feedrow, parent, false);
			viewHolder = new FeedViewHolder();
			viewHolder.appName = (TextView) convertView
					.findViewById(R.id.appName);
			viewHolder.appIcon = (CacheableImageView) convertView
					.findViewById(R.id.appIcon);
			viewHolder.appIconLocal = (ImageView) convertView
					.findViewById(R.id.appIconLocal);
			viewHolder.appDeveloper = (TextView) convertView
					.findViewById(R.id.appDeveloper);

			viewHolder.installIcon = (ImageView) convertView
					.findViewById(R.id.installIcon);
			viewHolder.googlePlayIcon = (ImageView) convertView
					.findViewById(R.id.googlePlayIcon);
			viewHolder.installText = (TextView) convertView
					.findViewById(R.id.installText);
			viewHolder.totalFriends = (TextView) convertView
					.findViewById(R.id.total_friends);

			viewHolder.actionDiv = (LinearLayout) convertView
					.findViewById(R.id.action_div);
			viewHolder.totalFriendsDiv = (LinearLayout) convertView
					.findViewById(R.id.total_friends_div);
			viewHolder.friendNewApp = (LinearLayout) convertView
					.findViewById(R.id.friend_new_app);
			viewHolder.shareCheckboxDiv = (LinearLayout) convertView
					.findViewById(R.id.share_checkbox_div);
			
			viewHolder.rightArrowDiv = (LinearLayout) convertView
					.findViewById(R.id.right_arrow_div);

			viewHolder.shareCheckbox = (CheckBox) convertView
					.findViewById(R.id.share_checkbox);
			
			viewHolder.totalNew = (TextView) convertView
					.findViewById(R.id.total_new);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (FeedViewHolder) convertView.getTag();
		}

		String selectedVal = values[position];
		final FeedBO feed = new FeedBO();
		feed.populateFromString(selectedVal);
		
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

		viewHolder.position = position;
		viewHolder.appName.setText(feed.getAppName());
		viewHolder.appDeveloper.setText(feed.getAppDeveloper());
		viewHolder.totalFriends.setText(feed.getTotalFriends());

		
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
		// .execute(feed.getAppIcon());

		// new ThumbnailTask(position, viewHolder).execute(feed.getAppIcon());
		viewHolder.appIcon.setImageDrawable(null);
		if (feed.getAppIcon().equals("NA")) {
			viewHolder.appIcon.setVisibility(View.GONE);
			viewHolder.appIconLocal.setVisibility(View.VISIBLE);
			viewHolder.appIconLocal.setImageDrawable(Session.hm.get(feed
					.getIconResourceID()));
		}

		// imageView.setImageResource(R.drawable.no);
		return convertView;
	}

	public class FeedViewHolder {
		public TextView appName;
		public CacheableImageView appIcon;
		public ImageView appIconLocal;
		public TextView appDeveloper;
		public int position;
		public ImageView newApp;
		public ImageView installIcon;
		public ImageView googlePlayIcon;
		public TextView installText;
		public TextView totalFriends;
		public LinearLayout actionDiv;
		public LinearLayout totalFriendsDiv;
		public LinearLayout shareCheckboxDiv;
		public LinearLayout friendNewApp;
		public CheckBox shareCheckbox;
		public TextView totalNew;
		public LinearLayout rightArrowDiv;
	}

	private static class ThumbnailTask extends AsyncTask {
		private int mPosition;
		private FeedViewHolder mHolder;

		public ThumbnailTask(int position, FeedViewHolder holder) {
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
		/*
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

		if ( shareFlag.equals("1")) {
			alertDialog.setTitle("Confirm Share ...");
			alertDialog.setMessage("Do you really want to share " + appName + "?");
		} else {
			alertDialog.setTitle("Confirm UnShare ...");
			alertDialog.setMessage("Do you really want to unshare " + appName + "?");
		}


		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						updateUserAppShareSetting();
						dialog.dismiss();
					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if ( shareFlag.equals("1")) {
							itemCheckBox.setChecked(false);
						} else {
							itemCheckBox.setChecked(true);
						}
					}
				});

		alertDialog.show();
		*/
	}

	/*
	 * private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	 * ImageView bmImage;
	 * 
	 * public DownloadImageTask(ImageView bmImage) { this.bmImage = bmImage; }
	 * 
	 * protected Bitmap doInBackground(String... urls) { String urldisplay =
	 * urls[0]; Bitmap mIcon11 = null; try { InputStream in = new
	 * java.net.URL(urldisplay).openStream(); mIcon11 =
	 * BitmapFactory.decodeStream(in); } catch (Exception e) {
	 * e.printStackTrace(); } return mIcon11; }
	 * 
	 * protected void onPostExecute(Bitmap result) {
	 * bmImage.setImageBitmap(result); } }
	 */
	
	
	
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

}
