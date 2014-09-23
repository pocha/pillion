package com.getpillion.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.graphics.drawable.Drawable;

public class Session {

	private String userName;
	private String facebookUserID;
	public static HashMap<String, Drawable> hm = new HashMap<String, Drawable>();
	
	public static List<String> packageList = new ArrayList<String>();
	public static String[] values = null;
	
	public static String showNewAppsInMyApps = "0";

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFacebookUserID() {
		return facebookUserID;
	}

	public void setFacebookUserID(String facebookUserID) {
		this.facebookUserID = facebookUserID;
	}
	
	
	
}
