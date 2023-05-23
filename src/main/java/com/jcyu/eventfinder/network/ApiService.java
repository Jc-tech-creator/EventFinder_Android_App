package com.jcyu.eventfinder.network;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jcyu.eventfinder.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ApiService {
    private static final String GEOCODING_API_KEY = "AIzaSyDWzcop693ZXXkFYSO_Ns7VDxeIOf3F9-I";

    private static final String Root_url = "https://sincere-cacao-382306.wl.r.appspot.com/";

    private static final String EVENT_DETAIL_URL = "https://sincere-cacao-382306.wl.r.appspot.com/detail?Event_id=";

//    private static RequestQueue requestQueue;

//    public ApiService(Context context) {
//        requestQueue = Volley.newRequestQueue(context);
//    }
    public interface CallbackAutoComplete {
        void onResponse(ArrayList<String> suggestions);
        void onError(String error);
    }

    public interface CallbackEventResult {
        void onResponse(ArrayList<JSONObject> events);
        void onError(String error);
    }

    public interface OnEventDetailReceived {
        void onEventDetailReceived(JSONObject eventDetail);
    }

    public interface OnVenueDetailReceived {
        void onVenueDetailReceived(JSONObject venueDetail);
    }

    public interface OnArtistDetailReceived{
        void onArtistDetailReceived(JSONObject artistDetail);
        void onError(String error);
    }

    public interface OnAlbumReceived{
        void onAlbumReceived(JSONObject albumDetail);
        void onError(String error);
    }

    public static void generateAutoComplete(String inputWord, Context context, CallbackAutoComplete callback) {
        // the source of this code is from stackOverFlow tutorial
        String url = "https://sincere-cacao-382306.wl.r.appspot.com/autoComplete?InputWord=" + inputWord;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest autoCompleteJsonRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray autoResponse) {
                        ArrayList<String> hints = new ArrayList<>();
                        //build the list to hold all result items
                        for (int i = 0; i < autoResponse.length(); i++) {
                            try {
                                JSONObject ResItem = autoResponse.getJSONObject(i);
                                //for each json response item
                                hints.add(ResItem.getString("name"));
                                //get the name field and add to the list
                                Log.d("Debug", "we enter onResponse");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        callback.onResponse(hints);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        callback.onError(error.getMessage());
                    }
                });

        requestQueue.add(autoCompleteJsonRequest);
    }

    public static void generateEventResult(String keywordText, String locationText, boolean autoDetect, String categoryText, String distanceText, Context context, CallbackEventResult callbackeventresult) {
        final double[] lat = new double[1];
        final double[] lon = new double[1];
        if(autoDetect == true){
            // get Lat and Lon from mainActivity
            MainActivity mainActivity = (MainActivity) context;
            lat[0] =  mainActivity.Lat;
            lon[0] = mainActivity.Lon;
            Log.d("LocationAutoDetect", "Latitude: " + lat[0] + ", Longitude: " + lon[0]);
            performActionWithUpdatedLocation(lat[0], lon[0], keywordText, categoryText, distanceText, context, callbackeventresult);
        }else{
            String geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + locationText + "&key=" + GEOCODING_API_KEY;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            Log.d("LocationGeoCoding", "geocodingUrl: " + geocodingUrl);
            JsonObjectRequest GoogleLocationJsonObject = new JsonObjectRequest
                    (Request.Method.GET, geocodingUrl, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject res) {
                            try {
                                JSONArray results = res.getJSONArray("results");
                                JSONObject headResult = results.getJSONObject(0);
                                JSONObject geoLocation = headResult.getJSONObject("geometry").getJSONObject("location");

                                lat[0] = geoLocation.getDouble("lat");
                                lon[0] = geoLocation.getDouble("lng");

                                // You can now use the lat and lng values
                                Log.d("GeocodingResponse", "Latitude: " + lat[0] + ", Longitude: " + lon[0]);
                                performActionWithUpdatedLocation(lat[0], lon[0], keywordText, categoryText, distanceText, context, callbackeventresult);
                            } catch (JSONException e) {
                                Log.e("GeocodingResponse", "Error parsing JSON response", e);
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("GeocodingResponse", "Error in request: " + error.getMessage());
                        }
                    });

            requestQueue.add(GoogleLocationJsonObject);
        }
    }

    private static void performActionWithUpdatedLocation(double lat, double lon, String keywordText, String categoryText, String distanceText, Context context, CallbackEventResult callbackeventresult) {
        Log.d("FinalLocationResponse", "keywordText" + keywordText + ", categoryText" + categoryText + ", distanceText" + distanceText + ", Latitude: " + lat + ", Longitude: " + lon);
        String eventSearchUrl = Root_url + "search?Keyword=" + keywordText + "&Distance=" + distanceText + "&Lat=" + lat + "&Lon=" + lon + "&Category=" + categoryText;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Log.d("eventSearch", "eventSearchUrl: " + eventSearchUrl);
        JsonObjectRequest GoogleLocationJsonObject = new JsonObjectRequest
                (Request.Method.GET, eventSearchUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String formattedResponse = response.toString(4);
                            Log.d("HttpResponse", "Response: " + formattedResponse);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        //initialize eventResult here
                        ArrayList<JSONObject> eventResult = new ArrayList<>();
                        Log.d("eventSearch", "we enter onResponse" );
                        try {
                            JSONObject _embedded = response.getJSONObject("_embedded");
                            JSONArray events = _embedded.getJSONArray("events");
                            for (int i = 0; i < events.length(); i++) {
                                try {
                                    JSONObject item = events.getJSONObject(i);
                                    //for each json response item
                                    eventResult.add(item);
                                    Log.d("eventSearch", "for-loop-item" );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("eventSearch", "Error parsing JSON response", e);
                        }
                        //I think I can get here anyway, if error occur, evenResult can be empty list;
                        Log.d("HttpResponse", "going to do callbackeventresult.onResponse(eventResult)");
                        callbackeventresult.onResponse(eventResult);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("eventSearch", "Error in request: " + error.getMessage());
                        callbackeventresult.onError(error.getMessage());
                    }
                });

            requestQueue.add(GoogleLocationJsonObject);
        }

    public static void generateEventDetail(Context context, String eventId, OnEventDetailReceived callback) {
        String url = "https://sincere-cacao-382306.wl.r.appspot.com/detail?Event_id=" + eventId;
        Log.d("eventDetailHttp", "eventId: " + eventId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("eventDetailHttp", "eventDetail: " + response.toString());
                        callback.onEventDetailReceived(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                Log.e("eventSearch", "Error in request: " + error.getMessage());
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    //generate venue details:
    public static void generateVenueDetail(Context context, String venueName, OnVenueDetailReceived callback) {
        String url = "https://sincere-cacao-382306.wl.r.appspot.com/venue?Keyword=" + venueName;
        Log.d("eventDetailHttp", "venueName: " + venueName);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("eventDetailHttp", "venueDetail: " + response.toString());
                        callback.onVenueDetailReceived(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                Log.e("eventSearch", "Error in request of venueDetail: " + error.getMessage());
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void generateArtistDetail(Context context, String attractName, OnArtistDetailReceived callback) {
        String url = "https://sincere-cacao-382306.wl.r.appspot.com/artist?Attraction=" + attractName;
        Log.d("ArtistDetail", "attractName in generateArtistDetail: " + attractName);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ArtistDetail", "artistDetail : " + response.toString());
                        callback.onArtistDetailReceived(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                Log.e("ArtistDetail", "Error in request of ArtistDetail: " + error.getMessage());
                callback.onError(error.getMessage());
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public static void generateAlbum(Context context, String artistItemId, OnAlbumReceived callback) {
        String url = "https://sincere-cacao-382306.wl.r.appspot.com/artist/album?spotifyArtistID=" + artistItemId;
        Log.d("AlbumDetail", "artistItemId in generateAlbum: " + artistItemId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("AlbumDetail", "AlbumDetail : " + response.toString());
                        callback.onAlbumReceived(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                Log.e("AlbumDetail", "Error in request of AlbumDetail: " + error.getMessage());
                callback.onError(error.getMessage());
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    }
