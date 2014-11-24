package com.getpillion.common;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.getpillion.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Helper {

    public static String postData(String url, String json) throws Exception {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-type", "application/json");
        StringBuilder stringBuilder = null;
        String str = null;
        try {
            // Add your data
            httppost.setEntity(new StringEntity(json,"UTF-8"));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            stringBuilder = inputStreamToString(response.getEntity().getContent());
            str = stringBuilder.toString();

	    /*} catch (ClientProtocolException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block*/
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("Could not send data to server. Check your internet connection & try again.");
        }
        return str;
    }

	public static String postData(String url, List<NameValuePair> nameValuePairs) throws Exception {
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);
	    StringBuilder stringBuilder = null;
	    String str = null;
	    try {
	        // Add your data
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        stringBuilder = inputStreamToString(response.getEntity().getContent());
	        str = stringBuilder.toString();
	        
	    /*} catch (ClientProtocolException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block*/
	    }catch(Exception e){
	    	e.printStackTrace();
            throw new Exception("Could not send data to server. Check your internet connection & try again.");
	    }
	    return str;
	}
	public static String getData(String url) {
	    // Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
	    final HttpGet httpget = new HttpGet(url);
	    HttpResponse response = null;
	    StringBuilder stringBuilder = null;
	    String str = null;
	    try {
		     response = httpclient.execute(httpget);
	         stringBuilder = inputStreamToString(response.getEntity().getContent());
	         str = stringBuilder.toString();
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    } catch(NullPointerException e){
	    	e.printStackTrace();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    //return response.toString();
	   return str;
	}
	// Fast Implementation
	private static StringBuilder inputStreamToString(InputStream is) {
	    String line = "";
	    StringBuilder total = new StringBuilder();
	    // Wrap a BufferedReader around the InputStream
	     BufferedReader rd = new BufferedReader(new InputStreamReader(is));

	    // Read response until the end
	    try {
			while ((line = rd.readLine()) != null) { 
			    total.append(line); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // Return full string
	    return total; 
	}

    public static void createMenu(SlidingMenu menu, SherlockFragmentActivity activity){
        //menu = new SlidingMenu(activity);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.0f);
        menu.attachToActivity(activity, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.menu);
    }

    public static String niceDate(Long date) {
        SimpleDateFormat ft = new SimpleDateFormat("E d MMM");
        return ft.format(new Date(date));
    }



    public static int compareDate(Long date1, Date date2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date(date1)).compareTo(sdf.format(date2));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

        listView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true; // Indicates that this has been handled by you and will not be forwarded further.
                }
                return false;
            }
        });
    }

    public static void autoCompleteTime(CharSequence text, EditText time, TextView timeHint){
        String stringText = text.toString();

        String textToBeSet = "";
        int inputType = InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_TIME;
        time.setError(null); //remove any error set from previously
        boolean error = false;

        if (stringText.matches("^\\d$")) {
            if (stringText.equals("1") || stringText.equals("0"))
                textToBeSet = "0:00AM";
            else {
                textToBeSet = ":00AM";
            }
        }
        else if (stringText.matches("^\\d\\d$") ) {
            int intText = Integer.parseInt(stringText);
            if (intText >= 0 && intText <= 12)
                textToBeSet = ":00AM";
            else
                error = true;
        }else if (stringText.matches("^\\d+:$"))
            textToBeSet = "00AM";
        else if (stringText.matches("^\\d+:\\d$"))
            textToBeSet = "0AM";
        else if (stringText.matches("^\\d+:\\d\\d$")) {
            int intText = Integer.parseInt(stringText.replaceAll("^\\d+:", "")); //get minutes
            if (intText > 0 && intText < 60) {
                inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
                textToBeSet = "AM";
            }
            else
                error = true;
        } else if (stringText.matches("^\\d+:\\d\\d(A|P)$")) {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
            textToBeSet = "M";
        } else if (stringText.equals(""))
            textToBeSet = "";
        else if (stringText.matches("^\\d+:\\d+(A|P)M")) {
            //To-do - take control to next input field
        } else {//error condition
            error = true;
        }

        if (error){
            textToBeSet = "";
            time.setError("Incorrect time format");
        }

        time.setInputType(inputType);
        timeHint.setText(textToBeSet);

    }

    public static void pushCursorToEnd(boolean isFocussed, EditText editText){
        if (isFocussed && editText.getText().length() > 0){
            String text = editText.getText().toString();
            editText.setText("");
            editText.append(text);
        }
    }

    public static Time formatAmPmTimetoSqlTime(String AmPmTime){
        try {
            return Time.valueOf(AmPmTime.replaceAll("(\\d+):(\\d+)(A|P)M", getString("$1", "$2", "$3")));
        }catch (Exception e){ //the time field could be empty. Return null in that case.
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(String s1, String s2, String s3){
        if (s3 == "P")
            return (Integer.parseInt(s1) + 12) + ":" + s2 + ":00";
        else
            return s1 + ":" + s2 + ":00";
    }

    public static String updateFromJsonField(String classField,String jsonField){
        if (jsonField != null)
            return jsonField;
        else
            return classField;
    }
    public static Long updateFromJsonField(Long classField, Long jsonField){
        if (jsonField != null)
            return jsonField;
        else
            return classField;
    }
    public static Boolean updateFromJsonField(Boolean classField, Boolean jsonField){
        if (jsonField != null)
            return jsonField;
        else
            return classField;
    }

    public static void returnControlToCallingActivity(Activity activity){
        activity.setResult(activity.getIntent().getIntExtra("requestCode",0));
        activity.finish();
    }

    public static Account mAccount = null;

    public static void createSyncAccount(Context context) {

        if (mAccount != null)
            return; //if at all static values persist across Activities

        // Create the account type and default account
        mAccount = new Account(
                Constant.ACCOUNT, Constant.ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        context.ACCOUNT_SERVICE);


            /*
             * Add the account and account type, no password or user data
             * If successful, return the Account object, otherwise report an error.
             */
        if (accountManager.addAccountExplicitly(mAccount, null, null)) {
                /*
                 * If you don't set android:syncable="true" in
                 * in your <provider> element in the manifest,
                 * then call context.setIsSyncable(account, AUTHORITY, 1)
                 * here.
                 */
            //return mAccount;
        } else {
                /*
                 * The account exists or some other error occurred. Log this, report it,
                 * or handle it internally.
                 */
            //return null;
        }
    }

    public static boolean fieldsHaveErrors(Activity activity){
        return false; //for testing
        //return !FormValidator.validate(activity, new SimpleErrorPopupCallback(activity));
    }


}
