package com.getpillion.common;


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
	public static String postData(String url, List<NameValuePair> nameValuePairs) {
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
	        
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	    	e.printStackTrace();
	        // TODO Auto-generated catch block
	    }catch(Exception e){
	    	e.printStackTrace();
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

    public static String niceDate(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("E MMM d");
        return ft.format(date);
    }

    public static String niceTime(Time time){
        return new SimpleDateFormat("hh:mm a").format(time);
    }

    public static int compareDate(Date date1, Date date2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date1).compareTo(sdf.format(date2));
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

        if (stringText.matches("^\\d$")) {
            if (stringText.equals("1") || stringText.equals("0"))
                textToBeSet = "0:00AM";
            else {
                textToBeSet = ":00AM";
            }
        }
        else if (stringText.matches("^\\d\\d$")) {
            textToBeSet = ":00AM";
        }else if (stringText.matches("^\\d+:$"))
            textToBeSet = "00AM";
        else if (stringText.matches("^\\d+:\\d$"))
            textToBeSet = "0AM";
        else if (stringText.matches("^\\d+:\\d\\d$")) {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
            textToBeSet = "AM";
        } else if (stringText.matches("^\\d+:\\d\\d(A|P)$")) {
            inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
            textToBeSet = "M";
        } else
            textToBeSet = "";

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

}
