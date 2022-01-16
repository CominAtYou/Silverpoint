package com.cominatyou.silverpoint.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {
    private final RequestQueue queue;
    private static RequestQueueSingleton instance = null;

    private RequestQueueSingleton(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public static RequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    public RequestQueue getQueue() {
        return queue;
    }
}
