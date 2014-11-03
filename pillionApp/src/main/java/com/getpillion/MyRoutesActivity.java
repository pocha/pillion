package com.getpillion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getpillion.common.Helper;
import com.getpillion.models.Route;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MyRoutesActivity extends ExtendMeSherlockWithMenuActivity {

    private ArrayList<Route> oRoutes = new ArrayList<Route>();
    private ArrayList<Route> rRoutes = new ArrayList<Route>();
    private RouteAdapter oRouteAdapter;
    private RouteAdapter rRouteAdapter;

    @InjectView(R.id.offeredRoutes)
    ListView offeredRoutes;
    @InjectView(R.id.emptyOfferedRoutes)
    TextView emptyOfferedRoutes;
    @InjectView(R.id.requestedRoutes)
    ListView requestedRoutes;
    @InjectView(R.id.emptyRequestedRoutes)
    TextView emptyRequestedRoutes;
    @InjectView(R.id.primaryButtonLayout)
    LinearLayout primaryButtonLayout;

    @OnItemClick(R.id.offeredRoutes) void onOfferedRouteItemClick(int position){
        showRouteDetail(offeredRoutes,position);
    }
    @OnItemClick(R.id.requestedRoutes) void onRequestedRouteItemClick(int position){
        showRouteDetail(requestedRoutes,position);
    }

    private void showRouteDetail(ListView parent, int position){
        Intent intent = new Intent(MyRoutesActivity.this, MyRouteInfoActivity.class);
        intent.putExtra("routeId",((Route)parent.getAdapter().getItem(position)).getId());
        startActivityForResult(intent, 1);
    }

    @OnClick(R.id.addRoute) void fireNewRouteIntent(View v){
        Intent intent = new Intent(MyRoutesActivity.this, MyRouteInfoActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        oRoutes.clear();
        oRoutes.addAll(Route.find(Route.class, "owner = ? AND is_offered = 1", String.valueOf(sharedPref.getLong("userId", 0L))));
        oRouteAdapter.notifyDataSetChanged();
        Helper.setListViewHeightBasedOnChildren(offeredRoutes);


        rRoutes.clear();
        rRoutes.addAll(Route.find(Route.class, "owner = ? AND is_offered = 0", String.valueOf(sharedPref.getLong("userId",0L)) ));
        rRouteAdapter.notifyDataSetChanged();
        Helper.setListViewHeightBasedOnChildren(requestedRoutes);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_routes);
        ButterKnife.inject(this);


        oRoutes.addAll(Route.find(Route.class, "owner = ? AND is_offered = 1", String.valueOf(sharedPref.getLong("userId",0L)) ));
        rRoutes.addAll(Route.find(Route.class, "owner = ? AND is_offered = 0", String.valueOf(sharedPref.getLong("userId",0L)) ));

        offeredRoutes.setEmptyView(emptyOfferedRoutes);
        requestedRoutes.setEmptyView(emptyRequestedRoutes);

        oRouteAdapter = new RouteAdapter(getApplicationContext(), oRoutes);
        offeredRoutes.setAdapter(oRouteAdapter);
        Helper.setListViewHeightBasedOnChildren(offeredRoutes);

        rRouteAdapter = new RouteAdapter(getApplicationContext(), rRoutes);
        requestedRoutes.setAdapter(rRouteAdapter);
        Helper.setListViewHeightBasedOnChildren(requestedRoutes);
    }

    private class RouteAdapter extends ArrayAdapter<Route> {
        private final Context context;
        private ArrayList<Route> values;

        public RouteAdapter(Context context, ArrayList<Route> values) {
            super(context, R.layout.route, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            RouteViewHolder viewHolder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.route, parent, false);
                viewHolder = new RouteViewHolder();
                viewHolder.from = (TextView) convertView.findViewById(R.id.from);
                viewHolder.to = (TextView) convertView.findViewById(R.id.to);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RouteViewHolder) convertView.getTag();
            }

            final Route route = values.get(position);
            viewHolder.position = position;
            viewHolder.from.setText(route.origin);
            viewHolder.to.setText(route.dest);

            return convertView;
        }

        private class RouteViewHolder {
            public int position;
            public TextView from;
            public TextView to;
        }
    }




}