package com.getpillion.common;

public class Constant {

	public static String SERVER = "http://192.168.1.8:3000";//"http://pillion.herokuapp.com";
    public static String NEW_RECORD_URL = SERVER + "/new";
    public static String UPDATE_RECORD_URL = SERVER + "/update";
    public static String DELETE_RECORD_URL = SERVER + "/delete";
    public static String GET_RECORD_URL = SERVER + "/get";


    public static String SERVER_GET = "http://testapiget.qples.com";
	public static String REGISTRATION_API = "/api/version2/register_user";
	public static String GCM_REGISTION_API = "/api/version2/register_gcm";
	public static String FRIENDS_COUNT = "/api/version2/get_my_friends_count.json";
	public static String USER_APP_VIEW = "/api/version2/user_app_view.json";
	public static String USER_APP_SHARE = "/api/version2/user_share.json";
	public static String USER_APPS = "/api/version2/user_apps.json";
	public static String FRIENDS_APPS = "/api/version2/fetch_friends_app.json";
	public static String MY_APPS = "/api/version2/my_apps.json";
	
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String SENDER_ID = "528993769112";
	public static final String EXTRA_MESSAGE = "message";
	
	public static final String PACKAGE_FILE_NAME = "app_packages_file";
	
	public static final String BUGSENSE_API_KEY = "7e67e321";

    public static final int CREATED = 0;
    public static final int  REQUESTED = 1;
    public static final int  ACCEPTED = 2;
    public static final int  REJECTED = 3;
    public static final int  CANCELLED = 4;
    public static final int  CHECKED_IN = 5;
    public static final int  SCHEDULED = 6;
    public static final int  STARTED = 7;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.getpillion.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "getpillion.com";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
	
}

