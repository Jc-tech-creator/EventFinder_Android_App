package com.jcyu.eventfinder.ui.favorites;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jcyu.eventfinder.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder>{
    private ArrayList<JSONObject> favorites;
    private LayoutInflater layoutInflater;
    private Context context;

    private SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    public FavoritesAdapter(Context context) {
        this.context = context;
        this.favorites = new ArrayList<>();
        loadFavoritesFromSharedPreferences();
        this.sharedPreferences = this.context.getSharedPreferences("savedEvents", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }
    private void loadFavoritesFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("savedEvents", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                JSONObject event = new JSONObject(entry.getValue().toString());
                Log.d("sharePreference", "event in loadFavoritesFromSharedPreferences: " + event);
                favorites.add(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Same onCreateViewHolder implementation as in EventsResultAdapter
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = layoutInflater.inflate(R.layout.item_result, parent, false);
        return new FavoritesAdapter.ViewHolder(view, parent.getContext(), sharedPreferences, editor);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Same onBindViewHolder implementation as in EventsResultAdapter
        JSONObject item = favorites.get(position);
        Log.d("sharePreference", "item in onBindViewHolder: " + item);
        try {
            String eventName = item.getString("name");
            holder.nameTextView.setText(eventName);

            JSONObject dates = item.getJSONObject("dates").getJSONObject("start");
            String date = dates.getString("localDate");

            SimpleDateFormat originalDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat targetDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Date parsedDate = originalDate.parse(date);
            String finalDate = targetDate.format(parsedDate);
            holder.dateTextView.setText(finalDate);
            if (dates.has("localTime")) {
                String time = dates.getString("localTime");
                // format the time
                SimpleDateFormat oldTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                SimpleDateFormat newTime = new SimpleDateFormat("h:mm a", Locale.getDefault());

                Date oldTimeFormatted = oldTime.parse(time);
                String newTimeFormatted = newTime.format(oldTimeFormatted);
                holder.timeTextView.setText(newTimeFormatted);
            } else {
                holder.timeTextView.setVisibility(View.GONE);
            }

            if (item.has("_embedded") &&
                    item.getJSONObject("_embedded").has("venues") &&
                    item.getJSONObject("_embedded").getJSONArray("venues").length() > 0) {
                JSONObject venue = item.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
                String venueName = venue.getString("name");
                holder.venueTextView.setText(venueName);
            } else {
                holder.venueTextView.setVisibility(View.GONE);
            }

            if (item.has("images") && item.getJSONArray("images").length() > 0) {
                String imageUrl = item.getJSONArray("images").getJSONObject(0).getString("url");
                //apply picasso to load and bind image
                Picasso.get().load(imageUrl).into(holder.eventImageView);
            } else {
                holder.eventImageView.setVisibility(View.GONE);
            }

            if (item.has("classifications") && item.getJSONArray("classifications").length() > 0) {
                JSONObject classification = item.getJSONArray("classifications").getJSONObject(0);
                if (classification.has("segment") && classification.getJSONObject("segment").has("name")) {
                    String categoryName = classification.getJSONObject("segment").getString("name");
                    if (!"Undefined".equals(categoryName)) {
                        holder.categoryTextView.setText(categoryName);
                    } else {
                        holder.categoryTextView.setVisibility(View.GONE);
                    }
                }else{
                    holder.categoryTextView.setVisibility(View.GONE);
                }
            } else {
                holder.categoryTextView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Same ViewHolder implementation as in EventsResultAdapter
        TextView nameTextView, venueTextView, categoryTextView, dateTextView, timeTextView;
        ImageView eventImageView;

        ImageView vacantHeart;

        ImageView filledHeart;

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        ViewHolder(@NonNull View itemView, Context context, SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
            super(itemView);
            // Find the TextViews or other views in your list_item_event layout and assign them
            // For example:\
            // eventNameTextView = itemView.findViewById(R.id.event_name_text_view);
            eventImageView = itemView.findViewById(R.id.event_image);
            categoryTextView = itemView.findViewById(R.id.category_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            venueTextView = itemView.findViewById(R.id.venue_text_view);
            vacantHeart = itemView.findViewById(R.id.vacant_heart);
            filledHeart = itemView.findViewById(R.id.filled_heart);

            // scrollable text setting:
            nameTextView.post(new Runnable() {
                @Override
                public void run() {
                    nameTextView.setSelected(true);
                }
            });

            venueTextView.post(new Runnable() {
                @Override
                public void run() {
                    venueTextView.setSelected(true);
                }
            });

            categoryTextView.post(new Runnable() {
                @Override
                public void run() {
                    categoryTextView.setSelected(true);
                }
            });

            this.sharedPreferences = sharedPreferences;
            this.editor = editor;

            filledHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    filledHeart.setVisibility(View.GONE);
                    vacantHeart.setVisibility(View.VISIBLE);
                    removeEventFromSharedPreferences(v.getContext(), position);
                }
            });
        }
    }
    private static void removeEventFromSharedPreferences(Context context,int position) {
        editor.remove(String.valueOf(position));
        editor.commit();
        Toast.makeText(context, "Item removed from favorites.", Toast.LENGTH_LONG).show();
    }


}
