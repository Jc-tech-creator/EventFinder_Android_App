package com.jcyu.eventfinder.ui.search.TabArtist;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jcyu.eventfinder.R;
import com.jcyu.eventfinder.ui.search.ArtistAlbumItem;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ArtistAlbumAdapter extends RecyclerView.Adapter<ArtistAlbumAdapter.ViewHolder> {
    private ArrayList<ArtistAlbumItem> artistAlbumItems;

    public ArtistAlbumAdapter(ArrayList<ArtistAlbumItem> artistAlbumItems) {
        this.artistAlbumItems = artistAlbumItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtistAlbumItem currentItem = artistAlbumItems.get(position);
        // Populate your view with data from artistAlbumItems.get(position);
        JSONObject artistDetail = currentItem.artistDetail;
        JSONObject album = currentItem.album;
        try {
            //artist Image
            if (artistDetail.has("images") && artistDetail.getJSONArray("images").length() > 0) {
                String imageUrl = artistDetail.getJSONArray("images").getJSONObject(0).getString("url");
                //apply picasso to load and bind image
                Log.d("onBindViewHolder", "imageUrl in onBindViewHolder: " + imageUrl);
                Picasso.get().load(imageUrl).into(holder.artistImage);
            } else {
                holder.artistImage.setVisibility(View.GONE);
            }
            //artist Name
            if(artistDetail.has("name")){
                String artistName = artistDetail.getString("name");
                holder.artistName.setText(artistName);
            }else{
                holder.artistName.setVisibility(View.GONE);
            }
            if(artistDetail.has("followers")) {
                JSONObject follower = artistDetail.getJSONObject("followers");
                if (follower.has("total")) {
                    int totalFollowers = follower.getInt("total");
                    String formattedFollowers;

                    if (totalFollowers >= 1000000) {
                        formattedFollowers = String.format(Locale.getDefault(), "%.1fM", totalFollowers / 1000000.0);
                    } else if (totalFollowers >= 1000) {
                        formattedFollowers = String.format(Locale.getDefault(), "%.1fK", totalFollowers / 1000.0);
                    } else {
                        formattedFollowers = String.valueOf(totalFollowers);
                    }

                    holder.artistFollowers.setText(formattedFollowers);
                } else {
                    holder.artistFollowers.setVisibility(View.GONE);
                }
            }else{
                holder.artistFollowers.setVisibility(View.GONE);
            }
            //spotify link
            if(artistDetail.has("external_urls")){
                JSONObject spotify = artistDetail.getJSONObject("external_urls");
                if(spotify.has("spotify")){
                    String spotifyUrl = spotify.getString("spotify");
                    holder.artistWebsite.setText("Check out on Spotify");
                    holder.artistWebsite.setTextColor(Color.GREEN);
                    holder.artistWebsite.setVisibility(View.VISIBLE);

                    holder.artistWebsite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl));
//                            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
//                            holder.itemView.getContext().startActivity(browserIntent);

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUrl));
                            holder.itemView.getContext().startActivity(browserIntent);
                        }
                    });
                }else{
                    holder.artistWebsite.setVisibility(View.GONE);
                }
            }else{
                holder.artistWebsite.setVisibility(View.GONE);
            }
            //popularity
            if(artistDetail.has("popularity")){
                int popularity = artistDetail.getInt("popularity");
                holder.popularityNumber.setText(String.valueOf(popularity));
                holder.popularityProgressBar.setProgress(popularity);
            }else{
                holder.popularityNumber.setVisibility(View.GONE);
                holder.popularityProgressBar.setVisibility(View.GONE);
            }

            //three album picture
            //first image
            if (album.has("body") && album.getJSONObject("body").has("items")) {
                JSONArray items = album.getJSONObject("body").getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject firstAlbumItem = items.getJSONObject(0);
                    if (firstAlbumItem.has("images") && firstAlbumItem.getJSONArray("images").length() > 1) {
                        JSONObject secondImage = firstAlbumItem.getJSONArray("images").getJSONObject(1);
                        if (secondImage.has("url")) {
                            String imageUrl = secondImage.getString("url");
                            Picasso.get().load(imageUrl).into(holder.albumImage1);
                        } else {
                            holder.albumImage1.setVisibility(View.GONE);
                        }
                    } else {
                        holder.albumImage1.setVisibility(View.GONE);
                    }
                } else {
                    holder.albumImage1.setVisibility(View.GONE);
                }
            } else {
                holder.albumImage1.setVisibility(View.GONE);
            }

            //second image
            if (album.has("body") && album.getJSONObject("body").has("items")) {
                JSONArray items = album.getJSONObject("body").getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject AlbumItem = items.getJSONObject(1);
                    if (AlbumItem.has("images") && AlbumItem.getJSONArray("images").length() > 1) {
                        JSONObject secondImage = AlbumItem.getJSONArray("images").getJSONObject(1);
                        if (secondImage.has("url")) {
                            String imageUrl = secondImage.getString("url");
                            Picasso.get().load(imageUrl).into(holder.albumImage2);
                        } else {
                            holder.albumImage2.setVisibility(View.GONE);
                        }
                    } else {
                        holder.albumImage2.setVisibility(View.GONE);
                    }
                } else {
                    holder.albumImage2.setVisibility(View.GONE);
                }
            } else {
                holder.albumImage2.setVisibility(View.GONE);
            }

            //third one
            //third image
            if (album.has("body") && album.getJSONObject("body").has("items")) {
                JSONArray items = album.getJSONObject("body").getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject AlbumItem2 = items.getJSONObject(2);
                    if (AlbumItem2.has("images") && AlbumItem2.getJSONArray("images").length() > 1) {
                        JSONObject secondImage = AlbumItem2.getJSONArray("images").getJSONObject(1);
                        if (secondImage.has("url")) {
                            String imageUrl = secondImage.getString("url");
                            Picasso.get().load(imageUrl).into(holder.albumImage3);
                        } else {
                            holder.albumImage3.setVisibility(View.GONE);
                        }
                    } else {
                        holder.albumImage3.setVisibility(View.GONE);
                    }
                } else {
                    holder.albumImage3.setVisibility(View.GONE);
                }
            } else {
                holder.albumImage3.setVisibility(View.GONE);
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return artistAlbumItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView artistImage;
        TextView artistName;
        TextView artistFollowers;
        TextView artistWebsite;
        ProgressBar popularityProgressBar;

        TextView popularAlbum;
        ImageView albumImage1;
        ImageView albumImage2;
        ImageView albumImage3;

        TextView popularityNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your card views here
            artistImage = itemView.findViewById(R.id.artist_image);
            artistName = itemView.findViewById(R.id.artist_name);
            artistFollowers = itemView.findViewById(R.id.artist_followers);
            artistWebsite = itemView.findViewById(R.id.artist_website);
            popularityNumber = itemView.findViewById(R.id.popularity_number);
            popularityProgressBar = itemView.findViewById(R.id.popularity_progress_bar);
//            popularAlbum = itemView.findViewById(R.id.popular_album);
            albumImage1 = itemView.findViewById(R.id.album_image1);
            albumImage2 = itemView.findViewById(R.id.album_image2);
            albumImage3 = itemView.findViewById(R.id.album_image3);
        }


    }
}

