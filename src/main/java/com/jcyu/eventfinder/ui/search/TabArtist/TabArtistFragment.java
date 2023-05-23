package com.jcyu.eventfinder.ui.search.TabArtist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jcyu.eventfinder.R;
import com.jcyu.eventfinder.network.ApiService;
import com.jcyu.eventfinder.ui.search.ArtistAlbumItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TabArtistFragment extends Fragment{
    public static TabArtistFragment newInstance(ArrayList<JSONObject> artistDetailList) {
        Log.d("artistDetailHttp", "artistDetail in TabArtistFragment's newInstance: " + artistDetailList.toString());
        Log.d("artistDetailHttp", "artistDetail in TabArtistFragment's newInstance 's size: " + artistDetailList.size());
//        Log.d("artistDetailHttp", "artistDetail in TabArtistFragment's newInstance 's index 0: " + artistDetailList.get(0));
//        Log.d("artistDetailHttp", "artistDetail in TabArtistFragment's newInstance 's index 1: " + artistDetailList.get(1));
        TabArtistFragment fragment = new TabArtistFragment();
        Bundle args = new Bundle();
        args.putSerializable("artistDetailList", artistDetailList);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_artist, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            ArrayList<JSONObject> artistDetailList = (ArrayList<JSONObject>) args.getSerializable("artistDetailList");
            Log.d("artistDetailHttp", "artistDetailString in TabArtistFragment's onViewCreated: " + artistDetailList);
            if (artistDetailList != null) {
                Log.d("artistDetailHttp", "artistDetail in TabArtistFragment's onViewCreated： " + artistDetailList);
                updateArtistDetail(artistDetailList);
            }
        }
    }
//
    public void updateArtistDetail(ArrayList<JSONObject> artistDetailList) {
        ArrayList<ArtistAlbumItem> artistAlbumItems = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger(artistDetailList.size());
        for (JSONObject artistDetailRaw : artistDetailList) {
            try {
                JSONObject artists = artistDetailRaw.getJSONObject("artists");
                JSONArray items = artists.getJSONArray("items");
                JSONObject artistDetail = (JSONObject) items.get(0);
                //artistDetail is the first item we need to add
                String artistItemId = artistDetail.getString("id");
                ApiService.generateAlbum(getContext(), artistItemId, new ApiService.OnAlbumReceived() {
                    @Override
                    public void onAlbumReceived(JSONObject album) {
                        artistAlbumItems.add(new ArtistAlbumItem(artistDetail, album));
                        if (counter.decrementAndGet() == 0) {// alraedy all in
                            setupRecyclerView(artistAlbumItems);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("generateArtistSum", "Error in generateAlbum: " + error);
                        if (counter.decrementAndGet() == 0) {
                            setupRecyclerView(artistAlbumItems);
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupRecyclerView(ArrayList<ArtistAlbumItem> artistAlbumItems) {
        RecyclerView recyclerView = getView().findViewById(R.id.artist_album_recycler_view);
        TextView emptyStateTextView = getView().findViewById(R.id.empty_state_text_view);
        if (artistAlbumItems.isEmpty()) {
            Log.d("setupRecyclerView", "artistAlbumItems in TabArtistFragment's setupRecyclerView: artistAlbumItems.isEmpty()");
            emptyStateTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        Log.d("setupRecyclerView", "artistAlbumItems in TabArtistFragment's setupRecyclerView: " + artistAlbumItems.toString());
        if(artistAlbumItems.size() > 0) {
            emptyStateTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.d("setupRecyclerView", "artistAlbumItems in TabArtistFragment's setupRecyclerView: " + artistAlbumItems.get(0).artistDetail.toString());
            Log.d("setupRecyclerView", "artistAlbumItems in TabArtistFragment's setupRecyclerView: " + artistAlbumItems.get(0).album.toString());
        }
        ArtistAlbumAdapter adapter = new ArtistAlbumAdapter(artistAlbumItems);
        recyclerView.setAdapter(adapter);
    }

}
