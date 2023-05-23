package com.jcyu.eventfinder.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jcyu.eventfinder.R;


public class FavoritesFragment extends Fragment {

    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;

    private ProgressBar progressBar;

    public FavoritesFragment() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        progressBar = view.findViewById(R.id.spinner_bar);
        progressBar.setVisibility(View.GONE);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recycler_view);
        favoritesAdapter = new FavoritesAdapter(getContext());
        favoritesRecyclerView.setAdapter(favoritesAdapter);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}