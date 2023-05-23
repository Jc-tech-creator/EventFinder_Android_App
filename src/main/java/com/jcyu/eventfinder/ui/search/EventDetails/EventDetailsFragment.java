package com.jcyu.eventfinder.ui.search.EventDetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jcyu.eventfinder.R;
import com.jcyu.eventfinder.network.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class EventDetailsFragment extends Fragment{
    private String eventId;

    private String venueName;
    private static final String ARG_EVENT_ID = "event_id";

    private static final String ARG_VENUE_NAME = "venue_Name";

    private JSONObject venueDetail;

//    private JSONObject artistDetail;

    private EventDetailsPagerAdapter pagerAdapter;

    public interface OnBackArrowClickListener {
        void onBackArrowClick();
    }

    private OnBackArrowClickListener onBackArrowClickListener;

    private TextView titleView;

    private ImageView facebook_icon;

    private ImageView twitter_icon;
    
    private JSONObject eventDetail;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnBackArrowClickListener) {
            onBackArrowClickListener = (OnBackArrowClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnBackArrowClickListener");
        }
    }

    public static Fragment newInstance(String eventId, String venueName) {
        Log.d("eventDetailHttp", "eventId in newInstance: " + eventId);
        Log.d("eventDetailHttp", "venueName in newInstance: " + venueName);
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        args.putString(ARG_VENUE_NAME, venueName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            eventId = args.getString(ARG_EVENT_ID);
            venueName = args.getString(ARG_VENUE_NAME);
        }
        Log.d("eventDetailHttp", "eventId in onViewCreated: " + eventId);
        Log.d("eventDetailHttp", "venueName in onViewCreated: " + venueName);
        // Make the API call here
        ApiService.generateEventDetail(getContext(), eventId, new ApiService.OnEventDetailReceived() {
            @Override
            public void onEventDetailReceived(JSONObject eventDetail) {
                populateEventDetails(eventDetail);
                Log.d("eventDetailHttp", "eventDetail in onEventDetailReceived: " + eventDetail.toString());
                // Pass the JSONObject to the TabDetailFragment through the pagerAdapter
                pagerAdapter.updateTabDetailFragment(eventDetail);
                // parse eventDetail to get attraction array
                try {
                    if (eventDetail.has("_embedded") && !eventDetail.isNull("_embedded")) {
                        JSONObject embedded = eventDetail.getJSONObject("_embedded");
                        if (embedded.has("attractions") && !embedded.isNull("attractions")) {
                            JSONArray attractionsArray = embedded.getJSONArray("attractions");

                            // Check if the JSONArray is not empty
                            if (attractionsArray.length() > 0) {
                                generateArtistSum(attractionsArray);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle the JSONException appropriately, e.g., log an error message or show a user-facing error message
                }
            }
        });
        //Make venue api call here:
        ApiService.generateVenueDetail(getContext(), venueName, new ApiService.OnVenueDetailReceived(){
            @Override
            public void onVenueDetailReceived(JSONObject venueDetail) {
                Log.d("eventDetailHttp", "venueDetail in onEventDetailReceived: " + venueDetail.toString());
                // Pass the JSONObject to the TabDetailFragment through the pagerAdapter
//                pagerAdapter.updateTabVenueFragment(venueDetail);
                EventDetailsFragment.this.venueDetail = venueDetail;
                pagerAdapter.updateTabVenueFragment(venueDetail);
            }
        });
    }
    private void populateEventDetails(JSONObject eventDetail) {
        try {
            this.eventDetail = eventDetail;
            String title = eventDetail.getString("name");
            titleView.setText(title);
            titleView.setSelected(true);
            //facebook, twitter logic here bind data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void generateArtistSum(JSONArray attractionsArray) {

        // Your logic for processing the attractionsArray goes here
        for (int i = 0; i < attractionsArray.length(); i++) {
            try {
                JSONObject attraction = attractionsArray.getJSONObject(i);
                System.out.println("this is array[i]:");
                System.out.println(attraction);

                if (!attraction.has("classifications") || attraction.isNull("classifications")) {
                    Log.d("ArtistDetail", "break first");
                    continue;
                }

                JSONArray classifications = attraction.getJSONArray("classifications");
                if (classifications.length() == 0) {
                    Log.d("ArtistDetail", "break first");
                    continue;
                }

                JSONObject classification = classifications.getJSONObject(0);
                if (!classification.has("segment") || classification.isNull("segment")) {
                    Log.d("ArtistDetail", "break second");
                    continue;
                }

                JSONObject segment = classification.getJSONObject("segment");
                if (!segment.has("name") || !segment.getString("name").equals("Music")) {
                    Log.d("ArtistDetail", "break third");
                    continue;
                }
                String attractName = attraction.getString("name");
                Log.d("ArtistDetail", "attractName " + attractName);
                //generate artistDetail first
                ApiService.generateArtistDetail(getContext(), attractName, new ApiService.OnArtistDetailReceived(){
                    @Override
                    public void onArtistDetailReceived(JSONObject artistDetail) {
                        Log.d("artistDetailHttp", "artistDetail in onArtistDetailReceived in EventDetailsFragment: " + artistDetail.toString());
                        // Pass the JSONObject to the TabArtistFragment through the pagerAdapter
//                        EventDetailsFragment.this.artistDetail = artistDetail;
                        pagerAdapter.updateTabArtistFragment(artistDetail);
                    }

                    @Override
                        public void onError(String error) {
                            // Handle the error for generateArtistDetail
                            Log.e("artistDetailHttp", "Error in generateArtistDetail: " + error);
                        }
                });

            } catch (JSONException e) {
                e.printStackTrace();
//                latch.countDown();
            }
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);


        ViewPager2 newPageView = view.findViewById(R.id.newPage);
        TabLayout newTabView = view.findViewById(R.id.newTab);
        ImageView newArrow = view.findViewById(R.id.new_arrow_back);

        titleView = view.findViewById(R.id.title);
        facebook_icon = view.findViewById(R.id.facebook_icon);
        twitter_icon = view.findViewById(R.id.twitter_icon);

        facebook_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String eventUrl = eventDetail.getString("url");
                    String facebookUrl = "https://www.facebook.com/sharer/sharer.php?u=" + eventUrl + "&src=sdkpreparse";

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
                    startActivity(browserIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle exceptions appropriately, e.g., log an error message or show a user-facing error message
                }
            }
        });

        twitter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String eventName = eventDetail.getString("name");
                    String eventUrl = eventDetail.getString("url");
                    String tweetText = "Check " + eventName + " on Ticketmaster";
                    String encodedTweetText = URLEncoder.encode(tweetText, "UTF-8");
                    String twitterUrl = "https://twitter.com/intent/tweet?text=" + encodedTweetText + "&url=" + eventUrl;

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
                    startActivity(browserIntent);
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    // Handle exceptions appropriately, e.g., log an error message or show a user-facing error message
                }
            }
        });
        pagerAdapter = new EventDetailsPagerAdapter(this, eventId, venueName, venueDetail, new ArrayList<JSONObject>());
        newPageView.setAdapter(pagerAdapter);

        newArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBackArrowClickListener != null) {
                    onBackArrowClickListener.onBackArrowClick();
                }
            }
        });

        new TabLayoutMediator(newTabView, newPageView,
                (tab, position) -> {
                    View customView = LayoutInflater.from(getContext()).inflate(R.layout.tab_custom_view, null);
                    ImageView tabIcon = customView.findViewById(R.id.tab_icon);
                    TextView tabText = customView.findViewById(R.id.tab_text);

                    switch (position) {
                        case 0:
                            tabText.setText("DETAILS");
                            tabIcon.setImageResource(R.drawable.info_icon);
                            tabIcon.setColorFilter(Color.WHITE);
                            break;
                        case 1:
                            tabText.setText("ARTIST");
                            tabIcon.setImageResource(R.drawable.artist_icon);
                            tabIcon.setColorFilter(Color.WHITE);
                            break;
                        case 2:
                            tabText.setText("VENUE");
                            tabIcon.setImageResource(R.drawable.venue_icon);
                            tabIcon.setColorFilter(Color.WHITE);
                            break;
                    }
                    tab.setCustomView(customView);
                }
        ).attach();

        return view;
    }
}
