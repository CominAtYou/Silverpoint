package com.cominatyou.silverpoint;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class GetStatus {
    protected static void get(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        try {
//            https://cdn.cominatyou.com/yes.json
            StringRequest request = new StringRequest(Request.Method.GET, "https://discordstatus.com/api/v2/incidents.json", response -> {
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