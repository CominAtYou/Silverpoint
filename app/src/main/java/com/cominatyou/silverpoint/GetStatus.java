package com.cominatyou.silverpoint;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.cominatyou.silverpoint.util.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class GetStatus {
    protected static JSONObject getSync(Context context) {
        RequestQueue queue = RequestQueueSingleton.getInstance(context).getQueue();
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        RequestFuture<String> mRequestFuture = RequestFuture.newFuture();
        final String endpoint = sharedPreferences.getString("selectedEndpoint", "production");
        final String url = endpoint.equals("production") ? "https://discordstatus.com/api/v2/incidents.json" : "https://cdn.cominatyou.com/yes.json";
        StringRequest request = new StringRequest(Request.Method.GET, url, mRequestFuture, mRequestFuture);
        request.setShouldCache(false);
        queue.add(request);
        try {
            return new JSONObject(mRequestFuture.get(10, TimeUnit.SECONDS));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}