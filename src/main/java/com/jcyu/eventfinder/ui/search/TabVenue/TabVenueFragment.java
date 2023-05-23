package com.jcyu.eventfinder.ui.search.TabVenue;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jcyu.eventfinder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TabVenueFragment extends Fragment implements OnMapReadyCallback {

    private LinearLayout horizonVenueItem1;

    private LinearLayout horizonVenueItem2;

    private LinearLayout horizonVenueItem3;

    private LinearLayout horizonVenueItem4;

    private LinearLayout verticalVenueItem1;

    private LinearLayout verticalVenueItem2;

    private LinearLayout verticalVenueItem3;

    private TextView rightText1;

    private TextView rightText2;

    private TextView rightText3;

    private TextView rightText4;

    private TextView downText1;

    private TextView downText2;

    private TextView downText3;

    private MapView mapView;

    private GoogleMap googleMap;

    private LatLng venueLatLng;

    public static TabVenueFragment newInstance(JSONObject venueDetail) {
        Log.d("eventDetailHttp", "venueDetail in TabVenueFragment's newInstance: " + venueDetail.toString());
        TabVenueFragment fragment = new TabVenueFragment();
        Bundle args = new Bundle();
        args.putString("venueDetail", venueDetail.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_venue, container, false);
//        getView()
        horizonVenueItem1 = view.findViewById(R.id.horizonVenue_item_1);
        TextView leftText1 = horizonVenueItem1.findViewById(R.id.left_text);
        leftText1.setText("Name");

        rightText1 = horizonVenueItem1.findViewById(R.id.right_text);
        rightText1.setSelected(true);

        horizonVenueItem2 = view.findViewById(R.id.horizonVenue_item_2);
        TextView leftText2 = horizonVenueItem2.findViewById(R.id.left_text);
        leftText2.setText("Address");

        rightText2 = horizonVenueItem2.findViewById(R.id.right_text);
        rightText2.setSelected(true);

        horizonVenueItem3 = view.findViewById(R.id.horizonVenue_item_3);
        TextView leftText3 = horizonVenueItem3.findViewById(R.id.left_text);
        leftText3.setText("City/State");

        rightText3 = horizonVenueItem3.findViewById(R.id.right_text);
        rightText3.setSelected(true);

        horizonVenueItem4 = view.findViewById(R.id.horizonVenue_item_4);
        TextView leftText4 = horizonVenueItem4.findViewById(R.id.left_text);
        leftText4.setText("Contact Info");

        rightText4 = horizonVenueItem4.findViewById(R.id.right_text);
        rightText4.setSelected(true);

        //  add map view here
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        // vertical card 3 fields
        verticalVenueItem1 = view.findViewById(R.id.verticalVenue_item_1);
        TextView upText1 = verticalVenueItem1.findViewById(R.id.up_text);
        upText1.setText("Open Hours");

        downText1 = verticalVenueItem1.findViewById(R.id.down_text);

        verticalVenueItem2 = view.findViewById(R.id.verticalVenue_item_2);
        TextView upText2 = verticalVenueItem2.findViewById(R.id.up_text);
        upText2.setText("General Rule");

        downText2 = verticalVenueItem2.findViewById(R.id.down_text);

        verticalVenueItem3 = view.findViewById(R.id.verticalVenue_item_3);
        TextView upText3 = verticalVenueItem3.findViewById(R.id.up_text);
        upText3.setText("Child Rule");

        downText3 = verticalVenueItem3.findViewById(R.id.down_text);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            String venueDetailString = args.getString("venueDetail");
            Log.d("eventDetailHttp", "venueDetailString in TabVenueFragment's onViewCreated: " + venueDetailString);
            if (venueDetailString != null) {
                try {
                    JSONObject venueDetail = new JSONObject(venueDetailString);
                    Log.d("eventDetailHttp", "venueDetail in TabVenueFragment's onViewCreated: " + venueDetail);
                    updateVenueDetail(venueDetail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //the map related logic below is copied from online tutorial
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Check if the venue location is available
        if (venueLatLng != null) {
            // Add a marker at the venue location and move the camera
            googleMap.addMarker(new MarkerOptions().position(venueLatLng).title("Venue"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng, 16));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void updateVenueDetail(JSONObject venueDetail) {
        Log.d("eventDetailHttp", "venueDetail in TabVenueFragment's updateVenueDetail: " + venueDetail.toString());
        try {
            JSONObject embedded = venueDetail.getJSONObject("_embedded");
            JSONArray venues = embedded.getJSONArray("venues");
            JSONObject venue = venues.getJSONObject(0);
            // venue is the root of data
            String venueName = venue.getString("name");
            rightText1.setText(venueName);
            // rightText2
            if (venue.has("address")){
                JSONObject address = venue.getJSONObject("address");
                if(address.has("line1")){
                    String addressString = address.getString("line1");
                    rightText2.setText(addressString);
                }else{
                    horizonVenueItem2.setVisibility(View.GONE);
                }
            }else{
                horizonVenueItem2.setVisibility(View.GONE);
            }
            // city, state; rightText3
            if (venue.has("city")){
                JSONObject city = venue.getJSONObject("city");
                if(city.has("name")){
                    String combo = "";
                    String cityName = city.getString("name");
                    String stateName = "";
                    if(venue.has("state")){
                        JSONObject state = venue.getJSONObject("state");
                        if(state.has("name")){
                            stateName = state.getString("name");
                        }
                    }
                    if(stateName.length() == 0){
                        combo = cityName;
                    }else{
                        combo = cityName + "," + stateName;
                    }
                    rightText3.setText(combo);

                }else{
                    horizonVenueItem3.setVisibility(View.GONE);
                }
            }else{
                horizonVenueItem3.setVisibility(View.GONE);
            }
            // rightText4
            if (venue.has("boxOfficeInfo")){
                JSONObject boxOfficeInfo = venue.getJSONObject("boxOfficeInfo");
                if(boxOfficeInfo.has("phoneNumberDetail")){
                    String phoneNumberDetail = boxOfficeInfo.getString("phoneNumberDetail");
                    rightText4.setText(phoneNumberDetail);
                }else{
                    horizonVenueItem4.setVisibility(View.GONE);
                }
            }else{
                horizonVenueItem4.setVisibility(View.GONE);
            }

            //add map view here
            LatLng venueLatLng;
            if (venue.has("location")) {
                JSONObject location = venue.getJSONObject("location");
                double latitude = location.getDouble("latitude");
                double longitude = location.getDouble("longitude");
                venueLatLng = new LatLng(latitude, longitude);

                // If the map is already ready, update the marker and move the camera
                if (googleMap != null) {

                    googleMap.addMarker(new MarkerOptions().position(venueLatLng).title("Venue"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng, 16));
                }
            }

            // yellow card page
            //open hour
            // downText1
            if (venue.has("boxOfficeInfo")){
                JSONObject boxOfficeInfo1 = venue.getJSONObject("boxOfficeInfo");
                if(boxOfficeInfo1.has("openHoursDetail")){
                    String openHoursDetail = boxOfficeInfo1.getString("openHoursDetail");
                    downText1.setText(openHoursDetail);
                    Log.d("TabVenueFragment", "setting downText1 text");
                }else{
                    Log.d("TabVenueFragment", "Hiding verticalVenueItem1");
                    verticalVenueItem1.setVisibility(View.GONE);
                }
            }else{
                Log.d("TabVenueFragment", "Hiding verticalVenueItem1");
                verticalVenueItem1.setVisibility(View.GONE);
            }

            // downText2
            if (venue.has("generalInfo")){
                JSONObject generalInfo = venue.getJSONObject("generalInfo");
                if(generalInfo.has("generalRule")){
                    String generalRule = generalInfo.getString("generalRule");
                    downText2.setText(generalRule);
                    Log.d("TabVenueFragment", "setting downText2 text");
                }else{
                    Log.d("TabVenueFragment", "Hiding verticalVenueItem2");
                    verticalVenueItem2.setVisibility(View.GONE);
                }
            }else{
                Log.d("TabVenueFragment", "Hiding verticalVenueItem2");
                verticalVenueItem2.setVisibility(View.GONE);
            }

            // downText3
            if (venue.has("generalInfo")){
                JSONObject generalInfo1 = venue.getJSONObject("generalInfo");
                if(generalInfo1.has("childRule")){
                    String childRule = generalInfo1.getString("childRule");
                    downText3.setText(childRule);
                    Log.d("TabVenueFragment", "setting downText3 text");
                }else{
                    Log.d("TabVenueFragment", "Hiding verticalVenueItem3");
                    verticalVenueItem3.setVisibility(View.GONE);
                }
            }else{
                Log.d("TabVenueFragment", "Hiding verticalVenueItem3");
                verticalVenueItem3.setVisibility(View.GONE);
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
