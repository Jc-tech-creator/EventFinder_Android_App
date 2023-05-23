package com.jcyu.eventfinder.ui.search.EventsResult;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jcyu.eventfinder.MainActivity;
import com.jcyu.eventfinder.R;

import org.json.JSONObject;

import java.util.ArrayList;

public class EventsResultsFragment extends Fragment implements EventsResultAdapter.OnEventClickListener {
    private static RecyclerView eventsResultRecyclerView;
    private static ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.events_result, container, false);

        eventsResultRecyclerView = root.findViewById(R.id.events_result_recycler_view);
        progressBar = root.findViewById(R.id.spinner_bar);

        LinearLayout backGroupLayout = root.findViewById(R.id.backToSearchGroup);
        backGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return root;
    }
    public static void passSearchResults(ArrayList<JSONObject> eventResult, Activity activity, androidx.fragment.app.FragmentManager fragmentManager) {
        // the eventResult can be empty!
        LinearLayout notFoundlayout = activity.findViewById(R.id.notFoundlayout);
        if (eventResult.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            eventsResultRecyclerView.setVisibility(View.GONE);
            notFoundlayout.setVisibility(View.VISIBLE);
        }else {
            // Hide the ProgressBar and display the RecyclerView, also hide the noEventView
            progressBar.setVisibility(View.GONE);
            eventsResultRecyclerView.setVisibility(View.VISIBLE);
            notFoundlayout.setVisibility(View.GONE);
            // Set up the RecyclerView with the search results
            // You need to create your own RecyclerView.Adapter for this purpose
            EventsResultAdapter eventsResultAdapter = new EventsResultAdapter(activity, eventResult, (EventsResultsFragment) fragmentManager.findFragmentByTag("EventsResultsFragment"));
            eventsResultRecyclerView.setAdapter(eventsResultAdapter);
            eventsResultRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        }
    }
    @Override
    public void onEventClick(String eventId, String venueName) {
        Log.d("eventDetailSearch", "eventId in EventsResultsFragment: " + eventId);
        // Handle the event click here
        // You can use the eventId to make an API call, for example
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).displayEventDetailsFragment(eventId, venueName);
        }
    }


}
