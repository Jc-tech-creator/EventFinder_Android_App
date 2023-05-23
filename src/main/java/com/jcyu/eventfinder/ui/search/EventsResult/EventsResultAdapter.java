package com.jcyu.eventfinder.ui.search.EventsResult;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class EventsResultAdapter extends RecyclerView.Adapter<EventsResultAdapter.ViewHolder> implements View.OnClickListener{
    private static ArrayList<JSONObject> eventResult;
    private LayoutInflater layoutInflater;

    public interface OnEventClickListener {
        void onEventClick(String eventId, String venueName);
    }

    private OnEventClickListener onEventClickListener;

    private SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private Context context;

    public EventsResultAdapter(Context context, ArrayList<JSONObject> eventResult, OnEventClickListener onEventClickListener) {
        Collections.sort(eventResult, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject j1, JSONObject j2) {
                try {
                    String ja = j1.getJSONObject("dates").getJSONObject("start").getString("localDate");
                    String jb = j2.getJSONObject("dates").getJSONObject("start").getString("localDate");

                    SimpleDateFormat oldDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    Date newDate1 = oldDate.parse(ja);
                    Date newDate2 = oldDate.parse(jb);

                    return newDate1.compareTo(newDate2);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        this.context = context;
        this.eventResult = eventResult;
        this.onEventClickListener = onEventClickListener;
        this.sharedPreferences = this.context.getSharedPreferences("savedEvents", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    @Override
    public void onClick(View v) {
        //get the position of clicked item
        int position = (int) v.getTag();
        JSONObject item = eventResult.get(position);
        try {
            String eventId = item.getString("id");
            String venueName = "";
            if (item.has("_embedded") &&
                    item.getJSONObject("_embedded").has("venues") &&
                    item.getJSONObject("_embedded").getJSONArray("venues").length() > 0) {
                JSONObject venue = item.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
                venueName = venue.getString("name");
            }
            Log.d("eventDetailSearch", "eventId in adapter: " + eventId);
            Log.d("eventDetailSearch", "venueName in adapter: " + venueName);
            onEventClickListener.onEventClick(eventId, venueName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = layoutInflater.inflate(R.layout.item_result, parent, false);
        View toastCustomLayout = layoutInflater.inflate(R.layout.customized_toast, null);
        return new ViewHolder(view, parent.getContext(), sharedPreferences, editor);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject item = eventResult.get(position);
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
                Log.d("image", "imageUrl: " + imageUrl);
//                Glide.with(context).load(imageUrl).into(holder.eventImageView);
                Picasso.get().load(imageUrl).into(holder.eventImageView);
//                Glide.with(context)
//                        .load(imageUrl)
//                        .placeholder(R.drawable.info_icon) // Replace with your placeholder image
//                        .error(R.drawable.logo) // Replace with your error image
//                        .into(holder.eventImageView);
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
            holder.itemView.setTag(position); // Set the position as the tag to retrieve it later
            holder.itemView.setOnClickListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return eventResult.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Define the TextViews or other views you have in your list_item_event layout
        // For example:
        // TextView eventNameTextView;
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

            vacantHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    vacantHeart.setVisibility(View.GONE);
                    filledHeart.setVisibility(View.VISIBLE);
                    storeEventToSharedPreferences(v.getContext(),position);
                }
            });

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

    private static void storeEventToSharedPreferences(Context context,int position) {
        JSONObject item = eventResult.get(position);
        String jsonString = item.toString();
        Log.d("sharePreference", "jsonString in storeEventToSharedPreferences: " + jsonString);
        editor.putString(String.valueOf(position), jsonString);
        editor.commit();
        Toast.makeText(context, "Item saved to favorites.", Toast.LENGTH_LONG).show();
    }

    private static void removeEventFromSharedPreferences(Context context,int position) {
        editor.remove(String.valueOf(position));
        editor.commit();
        Toast.makeText(context, "Item removed from favorites.", Toast.LENGTH_LONG).show();
    }

}
