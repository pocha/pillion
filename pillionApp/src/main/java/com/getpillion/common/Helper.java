package com.getpillion.common;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

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

}
