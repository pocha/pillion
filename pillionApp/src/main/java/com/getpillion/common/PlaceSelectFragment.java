package com.getpillion.common;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.getpillion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

public class PlaceSelectFragment extends DialogFragment {

    private ArrayList<String> places;
    private PlacesAdapter placeAdapter;

    @InjectView(R.id.searchBox)
    EditText searchBox;

    @InjectView(R.id.searchResult)
    ListView searchResult;

    @InjectView(R.id.poweredByGoogle) TextView poweredByGoogle;
    @InjectView(R.id.loading) TextView loading;

    private AsyncTask asyncTask = null;
    @OnTextChanged(R.id.searchBox) void triggerSearch(final CharSequence text){
        if (text.length() < 3)
            return;
        if (asyncTask != null)
            asyncTask.cancel(true); //cancel any previous asyncTask running

        loading.setVisibility(View.VISIBLE);
        asyncTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000); //ensures that this asyncTask does not do http request if position is still typing as it would get CANCELLED
                    if (!this.isCancelled()) {
                        places.clear();
                        places.addAll(autocomplete(text.toString()));
                    }
                } catch (Exception e) {
                    Log.e("ashish","exception",e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
               if (!this.isCancelled()) {
                   Log.d("PlaceSelectFragment","Got data from google - " + places.toString());
                   loading.setVisibility(View.GONE);
                   placeAdapter.notifyDataSetChanged();
               }
            }
        }.execute();
    }

    private EditText setThisInParentView;
    @OnItemClick(R.id.searchResult) void OnItemClick(int position){
        setThisInParentView.setText(places.get(position));
        this.dismiss();
    }


    public PlaceSelectFragment(){}

    public static PlaceSelectFragment newInstance(String title){
        PlaceSelectFragment p = new PlaceSelectFragment();
        p.setStyle(DialogFragment.STYLE_NORMAL,R.style.AppTheme);
        return p;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance){
        setThisInParentView = (EditText)getActivity().findViewById(getArguments().getInt("populateMeInParent"));
        View view = inflater.inflate(R.layout.fragment_place_select,container);

        ButterKnife.inject(this,view);
        places = new ArrayList<String>();
        searchResult.setEmptyView(poweredByGoogle);
        placeAdapter = new PlacesAdapter(getActivity(),places);
        searchResult.setAdapter(placeAdapter);
        getDialog().setTitle("Search location");

        return view;
    }

    public void onResume()
    {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        //window.setGravity(Gravity.CENTER);
    }

    private static final String LOG_TAG = "PlaceSelectFragment";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyBc2jwTqRGdQaVfAaOEueX8hYO7FK_za4k";

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            e.printStackTrace();
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        //Log.d(LOG_TAG,"json result from server - " + jsonResults.toString());
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    private class PlacesAdapter extends ArrayAdapter<String> {

        private final Context ctx;
        ArrayList<String> places;

        public PlacesAdapter(Context context, ArrayList<String> places) {
            super(context, R.layout.route, places);
            this.ctx = context;
            this.places = places;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) ctx
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView) convertView.findViewById(R.id.row_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final String place = places.get(position);
            viewHolder.position = position;
            viewHolder.name.setText(place);
            return convertView;
        }
    }

    private class ViewHolder {
        public int position;
        public TextView name;
        //public TextView title;
    }


}