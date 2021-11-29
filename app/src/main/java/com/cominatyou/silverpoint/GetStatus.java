package com.cominatyou.silverpoint;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class GetStatus {
    protected static void get(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        final String endpoint = sharedPreferences.getString("selectedEndpoint", "production");
        final String url = endpoint.equals("production") ? "https://discordstatus.com/api/v2/incidents.json" : "https://cdn.cominatyou.com/yes.json";
        try {
            StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                try {
                    APIResponse.setStatus(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                error.printStackTrace();
                APIResponse.setStatus(null);
            });
            queue.add(request);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}