package com.jcyu.eventfinder.ui.search.EventDetails;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jcyu.eventfinder.ui.search.TabArtist.TabArtistFragment;
import com.jcyu.eventfinder.ui.search.TabDetail.TabDetailFragment;
import com.jcyu.eventfinder.ui.search.TabVenue.TabVenueFragment;

import org.json.JSONObject;

import java.util.ArrayList;

public class EventDetailsPagerAdapter extends FragmentStateAdapter {
    private String eventId;

    private String venueName;

    private JSONObject venueDetail;

    private ArrayList<JSONObject> artistDetailList;

    private TabDetailFragment tabDetailFragment;

    private TabVenueFragment tabVenueFragment;

    public EventDetailsPagerAdapter(Fragment fragment, String eventId, String venueName, JSONObject venueDetail, ArrayList<JSONObject> artistDetailList) {
        super(fragment);
        this.eventId = eventId;
        this.venueName = venueName;
        this.venueDetail = venueDetail;
        this.artistDetailList = artistDetailList;
    }

    public void updateTabDetailFragment(JSONObject eventDetail) {
        if (tabDetailFragment != null) {
            Log.d("eventDetailHttp", "enter updateTabDetailFragment in adapter");
            Log.d("eventDetailHttp", "eventDetail in updateTabDetailFragment in eventDetailadapter: " + eventDetail.toString());
            tabDetailFragment.updateEventDetail(eventDetail);
        }
    }

    public void updateTabArtistFragment(JSONObject artistDetail){
        Log.d("artistDetailHttp", "enter updateTabArtistFragment in adapter");
        Log.d("artistDetailHttp", "artistDetail in updateTabArtistFragment in eventDetailadapter: " + artistDetail.toString());
        this.artistDetailList.add(artistDetail);
    }

    public void updateTabVenueFragment(JSONObject venueDetail) {
            Log.d("eventDetailHttp", "enter updateTabVenuelFragment in adapter");
            Log.d("eventDetailHttp", "venueDetail in updateTabVenuelFragment in eventDetailadapter: " + venueDetail.toString());
            this.venueDetail = venueDetail;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                tabDetailFragment = new TabDetailFragment(); // Initialize tabDetailFragment here
                fragment = tabDetailFragment;
                break;
            case 1:
                fragment = TabArtistFragment.newInstance(artistDetailList);
                return fragment;
            case 2:
                fragment = TabVenueFragment.newInstance(venueDetail);
                return fragment;
            default:
                throw new IllegalStateException("Unexpected position: " + position);
        }
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        args.putString("venueName", venueName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
