package com.jcyu.eventfinder.ui.search.TabDetail;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.jcyu.eventfinder.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TabDetailFragment extends Fragment{
//    private TextView eventNameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_detail, container, false);

//        eventNameTextView = view.findViewById(R.id.event_name); // Replace R.id.event_name with the actual ID of the TextView in your layout file

        return view;
    }

    public void updateEventDetail(JSONObject eventDetail) {
        Log.d("eventDetailHttp", "eventDetail in TabDetailFragment: " + eventDetail.toString());
        try {
            String eventName = eventDetail.getString("name");
//            eventNameTextView.setText(eventName);
            LinearLayout horizontalItem1 = getView().findViewById(R.id.horizontal_item_1);
            TextView leftText1 = horizontalItem1.findViewById(R.id.left_text);
            leftText1.setText("Artist/Team");

            // Set the horizontal_item_1 rightText
            TextView rightText1 = horizontalItem1.findViewById(R.id.right_text);
            JSONArray attractions = eventDetail.getJSONObject("_embedded").getJSONArray("attractions");
            StringBuilder attractionNames = new StringBuilder();
            for (int i = 0; i < attractions.length(); i++) {
                JSONObject attraction = attractions.getJSONObject(i);
                if (attraction.has("name")) {
                    if (i != 0) {
                        attractionNames.append(" | ");
                    }
                    attractionNames.append(attraction.getString("name"));
                }
            }
            rightText1.setText(attractionNames.toString());
            rightText1.setSelected(true);

            LinearLayout horizontalItem2 = getView().findViewById(R.id.horizontal_item_2);
            TextView leftText2 = horizontalItem2.findViewById(R.id.left_text);
            leftText2.setText("Venue");

            // Set the horizontal_item_2 rightText
            TextView rightText2 = horizontalItem2.findViewById(R.id.right_text);
            JSONObject venuesEmbedded = eventDetail.getJSONObject("_embedded");
            if (venuesEmbedded.has("venues")) {
                JSONArray venues = venuesEmbedded.getJSONArray("venues");
                if (venues.length() > 0) {
                    JSONObject firstVenue = venues.getJSONObject(0);
                    if (firstVenue.has("name")) {
                        rightText2.setText(firstVenue.getString("name"));
                        rightText2.setSelected(true);
                    } else {
                        horizontalItem2.setVisibility(View.GONE);
                    }
                } else {
                    horizontalItem2.setVisibility(View.GONE);
                }
            } else {
                horizontalItem2.setVisibility(View.GONE);
            }
            // the third line:
            LinearLayout horizontalItem3 = getView().findViewById(R.id.horizontal_item_3);
            TextView leftText3 = horizontalItem3.findViewById(R.id.left_text);
            leftText3.setText("Date");

            // Set the horizontal_item_3 rightText
            TextView rightText3 = horizontalItem3.findViewById(R.id.right_text);
            String localDateString = eventDetail.getJSONObject("dates").getJSONObject("start").getString("localDate");

            // Format the date
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date localDate = inputFormat.parse(localDateString);
            String formattedDate = outputFormat.format(localDate);
            rightText3.setText(formattedDate);
            rightText3.setSelected(true);
            // the fourth line
            LinearLayout horizontalItem4 = getView().findViewById(R.id.horizontal_item_4);
            TextView leftText4 = horizontalItem4.findViewById(R.id.left_text);
            leftText4.setText("Time");

            // Set the horizontal_item_4 rightText
            TextView rightText4 = horizontalItem4.findViewById(R.id.right_text);
            JSONObject start = eventDetail.getJSONObject("dates").getJSONObject("start");

            if (start.has("localTime")) {
                String time = start.getString("localTime");

                // Format the time
                SimpleDateFormat oldTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                SimpleDateFormat newTime = new SimpleDateFormat("h:mm a", Locale.getDefault());
                Date oldTimeFormatted = oldTime.parse(time);
                String newTimeFormatted = newTime.format(oldTimeFormatted);

                rightText4.setText(newTimeFormatted);
                rightText4.setSelected(true);
            } else {
                horizontalItem4.setVisibility(View.GONE);
            }
            //fifth line
            LinearLayout horizontalItem5 = getView().findViewById(R.id.horizontal_item_5);
            TextView leftText5 = horizontalItem5.findViewById(R.id.left_text);
            leftText5.setText("Genres");

            // Set the horizontal_item_5 rightText
            TextView rightText5 = horizontalItem5.findViewById(R.id.right_text);
            JSONArray classifications = eventDetail.getJSONArray("classifications");

            if (classifications.length() > 0) {
                JSONObject classification = classifications.getJSONObject(0);
                StringBuilder genreText = new StringBuilder();

                String[] keys = {"segment", "genre", "subGenre", "type", "subType"};
                String[] names = new String[keys.length];

                for (int i = 0; i < keys.length; i++) {
                    if (classification.has(keys[i]) && classification.getJSONObject(keys[i]).has("name")) {
                        names[i] = classification.getJSONObject(keys[i]).getString("name");
                        if (!names[i].equals("Undefined")) {
                            genreText.append(" | ").append(names[i]);
                        }
                    }
                }

                if (genreText.length() > 0) {
                    genreText.delete(0, 3); // Remove the leading " | "
                }
                rightText5.setText(genreText.toString());
                rightText5.setSelected(true);
            } else {
                rightText5.setText("");
            }
            //6th line
            LinearLayout horizontalItem6 = getView().findViewById(R.id.horizontal_item_6);
            TextView leftText6 = horizontalItem6.findViewById(R.id.left_text);
            leftText6.setText("Price Range");

            // Set the horizontal_item_6 rightText
            TextView rightText6 = horizontalItem6.findViewById(R.id.right_text);
            if (eventDetail.has("priceRanges")) {
                JSONArray priceRanges = eventDetail.getJSONArray("priceRanges");
                if (priceRanges.length() > 0) {
                    JSONObject priceRange = priceRanges.getJSONObject(0);
                    if (priceRange.has("min") && priceRange.has("max")) {
                        double min = priceRange.getDouble("min");
                        double max = priceRange.getDouble("max");
                        rightText6.setText(String.format(Locale.getDefault(), "%.2f - %.2f", min, max));
                        rightText6.setSelected(true);
                    } else {
                        horizontalItem6.setVisibility(View.GONE);
                    }
                } else {
                    horizontalItem6.setVisibility(View.GONE);
                }
            } else {
                horizontalItem6.setVisibility(View.GONE);
            }
            //still have two more textView to bind
            // ticket status
            LinearLayout horizontalItem7 = getView().findViewById(R.id.horizontal_item_7);
            TextView leftText7 = horizontalItem7.findViewById(R.id.left_text);
            TextView rightText7 = horizontalItem7.findViewById(R.id.right_text);

            leftText7.setText("Ticket Status");

            JSONObject status = eventDetail.getJSONObject("dates").getJSONObject("status");
            String statusCode = status.getString("code");

            String statusLabel = "";
            int backgroundColorResource = R.color.transparent; // Set a default transparent color

            switch (statusCode) {
                case "onsale":
                    statusLabel = "On Sale";
                    backgroundColorResource = R.color.green;
                    break;
                case "offsale":
                    statusLabel = "Off Sale";
                    backgroundColorResource = R.color.red;
                    break;
                case "cancelled":
                    statusLabel = "Canceled";
                    backgroundColorResource = R.color.black;
                    break;
                case "postponed":
                    statusLabel = "Postponed";
                    backgroundColorResource = R.color.orange;
                    break;
                case "rescheduled":
                    statusLabel = "Rescheduled";
                    backgroundColorResource = R.color.orange;
                    break;
                default:
                    horizontalItem7.setVisibility(View.GONE);
                    break;
            }

            if (!statusLabel.isEmpty()) {
                rightText7.setText(statusLabel);
                rightText7.setBackgroundColor(ContextCompat.getColor(getContext(), backgroundColorResource));
            } else {
                horizontalItem7.setVisibility(View.GONE);
            }
            // url
            LinearLayout horizontalItem8 = getView().findViewById(R.id.horizontal_item_8);
            TextView leftText8 = horizontalItem8.findViewById(R.id.left_text);
            TextView rightText8 = horizontalItem8.findViewById(R.id.right_text);
            rightText8.setSelected(true);
            leftText8.setText("Buy Tickets At");
            if (eventDetail.has("url")){
                String url = eventDetail.getString("url");
                int linkColor = getResources().getColor(R.color.colorActionBarText);
                SpannableString spannableString = new SpannableString(url);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    }
                };
                spannableString.setSpan(new ForegroundColorSpan(linkColor), 0, url.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(clickableSpan, 0, url.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                rightText8.setText(spannableString);
                rightText8.setMovementMethod(LinkMovementMethod.getInstance());
                rightText8.setHighlightColor(Color.TRANSPARENT);
//              rightText8.setText(url);
//              rightText8.setSelected(true);
            }else{
                horizontalItem8.setVisibility(View.GONE);
            }
                //image binding
            ImageView seatMapView = getView().findViewById(R.id.seat_map);
            if (eventDetail.has("seatmap") && eventDetail.getJSONObject("seatmap").has("staticUrl")) {
                String staticUrl = eventDetail.getJSONObject("seatmap").getString("staticUrl");
                Picasso.get().load(staticUrl).into(seatMapView);
            } else {
                seatMapView.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
