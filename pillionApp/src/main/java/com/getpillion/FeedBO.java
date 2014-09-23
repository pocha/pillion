package com.getpillion;

public class FeedBO {

	private String appName;
	private String appIcon;
	private String appPackage;
	private String appDeveloper;
	private String friendNames;
	private String appID;
	private String isInstalled;
	private String allFriendsMode;
	private String iconResourceID = "NA";
	private int totalNewApps = 0;
	private int friendView = 0;

	public int getTotalNewApps() {
		return totalNewApps;
	}

	public void setTotalNewApps(int totalNewApps) {
		this.totalNewApps = totalNewApps;
	}

	public String getTotalFriends() {
		return totalFriends;
	}

	public void setTotalFriends(String totalFriends) {
		this.totalFriends = totalFriends;
	}

	private int isItNew;
	private String totalFriends;

	public int getIsItNew() {
		return isItNew;
	}

	public void setIsItNew(int isItNew) {
		this.isItNew = isItNew;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}

	public String getAppDeveloper() {
		return appDeveloper;
	}

	public void setAppDeveloper(String appDeveloper) {
		this.appDeveloper = appDeveloper;
	}

	public String toString() {
		return appName + "::" + appIcon + "::" + appPackage + "::"
				+ appDeveloper + "::" + isItNew + "::" + totalFriends + "::"
				+ friendNames + "::" + appID + "::" + isInstalled + "::"
				+ allFriendsMode + "::" + iconResourceID + "::" + totalNewApps
				+ "::" + friendView;
	}

	public void populateFromString(String str) {
		try {
			String[] strArr = str.split("::");
			setAppName(strArr[0]);
			setAppIcon(strArr[1]);
			setAppPackage(strArr[2]);
			setAppDeveloper(strArr[3]);
			setIsItNew(Integer.parseInt(strArr[4]));
			setTotalFriends(strArr[5]);
			setFriendNames(strArr[6]);
			setAppID(strArr[7]);
			setIsInstalled(strArr[8]);
			setAllFriendsMode(strArr[9]);
			setIconResourceID(strArr[10]);
			setTotalNewApps(Integer.parseInt(strArr[11]));
			setFriendView(Integer.parseInt(strArr[12]));
		} catch (Exception ex) {
		}
	}

	public String getFriendNames() {
		return friendNames;
	}

	public void setFriendNames(String friendNames) {
		this.friendNames = friendNames;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getIsInstalled() {
		return isInstalled;
	}

	public void setIsInstalled(String isInstalled) {
		this.isInstalled = isInstalled;
	}

	public String getAllFriendsMode() {
		return allFriendsMode;
	}

	public void setAllFriendsMode(String allFriendsMode) {
		this.allFriendsMode = allFriendsMode;
	}

	public String getIconResourceID() {
		return iconResourceID;
	}

	public void setIconResourceID(String iconResourceID) {
		this.iconResourceID = iconResourceID;
	}

	public int getFriendView() {
		return friendView;
	}

	public void setFriendView(int friendView) {
		this.friendView = friendView;
	}

}
