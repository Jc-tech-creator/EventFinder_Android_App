package com.jcyu.eventfinder.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    // this class is copied from stackOverFlow

    private Context context;
    private static VolleySingleton volleyInstance;
    private RequestQueue requestQueue;

    private VolleySingleton(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (volleyInstance == null) {
            volleyInstance = new VolleySingleton(context);
        }
        return volleyInstance;
    }


    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
