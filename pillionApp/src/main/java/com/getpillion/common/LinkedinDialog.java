package com.getpillion.common;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.getpillion.IPostLoginCallback;
import com.getpillion.R;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class LinkedinDialog extends Dialog{
    private static ProgressDialog progressDialog = null;

    public static LinkedInApiClientFactory factory;
    public static LinkedInOAuthService oAuthService;
    public static LinkedInRequestToken liToken;
    public static LinkedInAccessToken accessToken;
    public static LinkedInApiClient client;
    private static String authorizationUrl;

    private static class Config {

        public static String LINKEDIN_CONSUMER_KEY = "75h5k3s5y5uann";
        public static String LINKEDIN_CONSUMER_SECRET = "4wxs2cqe7lX1m2gj";
        public static String scopeParams = "r_fullprofile+r_emailaddress+r_contactinfo";

        public static String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
        public static String OAUTH_CALLBACK_HOST = "callback";
        //public static String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
        public static String OAUTH_CALLBACK_URL = "x-oauthflow-linkedin://callback";
    }

    //Construct a new LinkedIn dialog

    public LinkedinDialog(Context context) {
        super(context);
    }


    public static void Login(final Context context, final IPostLoginCallback callBack) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);

        final LinkedinDialog d = new LinkedinDialog(context);

        d.setVerifierListener(new OnVerifyListener() {
            @Override
            public void onVerify(final String verifier) {
                try {
                    Log.i("LinkedinSample", "verifier: " + verifier);
                    progressDialog.show();
                    try {
                        new AsyncTask<Void, Void, Person>() {
                            @Override
                            protected Person doInBackground(Void... params) {
                                try {
                                    accessToken = oAuthService
                                            .getOAuthAccessToken(liToken,verifier);
                                    factory = LinkedInApiClientFactory.newInstance(
                                            Config.LINKEDIN_CONSUMER_KEY, Config.LINKEDIN_CONSUMER_SECRET);
                                    client = factory.createLinkedInApiClient(accessToken);
                                    Person p2 = client.getProfileForCurrentUser(EnumSet.of(ProfileField.ID));
                                    return client.getProfileById(p2.getId(), EnumSet.of(
                                            ProfileField.FIRST_NAME,
                                            ProfileField.LAST_NAME,
                                            ProfileField.INDUSTRY,
                                            ProfileField.SKILLS,
                                            ProfileField.HEADLINE,
                                            ProfileField.SUMMARY,
                                            ProfileField.LANGUAGES,
                                            ProfileField.LANGUAGES_LANGUAGE_NAME,
                                            ProfileField.LANGUAGES_PROFICIENCY_NAME,
                                            ProfileField.LANGUAGES_LANGUAGE,
                                            ProfileField.HONORS,
                                            ProfileField.INTERESTS,
                                            ProfileField.POSITIONS,
                                            ProfileField.EDUCATIONS,
                                            ProfileField.INDUSTRY,
                                            ProfileField.API_STANDARD_PROFILE_REQUEST,
                                            ProfileField.PICTURE_URL,
                                            ProfileField.PUBLIC_PROFILE_URL));

                                } catch (Exception e) {

                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Person p) {
                                progressDialog.dismiss();
                                callBack.postLoginCallback(p);
                            }
                        }.execute();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                } catch (Exception e) {
                    Log.i("LinkedinSample", "error to get verifier");
                    e.printStackTrace();
                }
            }
        });

        progressDialog.show();
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        oAuthService = LinkedInOAuthServiceFactory.getInstance()
                                .createLinkedInOAuthService(Config.LINKEDIN_CONSUMER_KEY,
                                        Config.LINKEDIN_CONSUMER_SECRET);

                        liToken = oAuthService.getOAuthRequestToken(Config.OAUTH_CALLBACK_URL);
                        authorizationUrl = liToken.getAuthorizationUrl();
                    } catch (Exception e) {

                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    progressDialog.dismiss();
                    d.show();
                }
            }.execute();
        } catch (Exception ex) {
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);// must call before super.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        WebView mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl(authorizationUrl);
        mWebView.setWebViewClient(new HelloWebViewClient());

     /*  mWebView.setPictureListener(new PictureListener() {
            @Override
            public void onNewPicture(WebView view, Picture picture) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
        });*/

    }

    //webview client for internal url loading

    class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(Config.OAUTH_CALLBACK_URL)) {
                Uri uri = Uri.parse(url);
                String verifier = uri.getQueryParameter("oauth_verifier");
                cancel();
                for (OnVerifyListener d : listeners) {
                    // call listener method
                    d.onVerify(verifier);
                }
            } else if (url.contains("https://www.linkedin.com/uas/oauth/redorangetechnologies.com")) {
                cancel();
            } else {
                Log.i("LinkedinSample", "url: " + url);
                view.loadUrl(url);
            }

            return true;
        }
    }

    /**
     * List of listener.
     */
    private List<OnVerifyListener> listeners = new ArrayList<OnVerifyListener>();

    /**
     * Register a callback to be invoked when authentication have finished.
     *
     * @param data
     *            The callback that will run
     */
    public void setVerifierListener(OnVerifyListener data) {
        listeners.add(data);
    }

    /**
     * Listener for oauth_verifier.
     */
    interface OnVerifyListener {
        /**
         * invoked when authentication have finished.
         *
         * @param verifier
         *            oauth_verifier code.
         */
        public void onVerify(String verifier);
    }
}