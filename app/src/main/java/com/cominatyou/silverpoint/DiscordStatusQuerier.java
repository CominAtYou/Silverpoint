package com.cominatyou.silverpoint;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.cominatyou.silverpoint.notifications.NotificationUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiscordStatusQuerier extends Worker {

    public DiscordStatusQuerier(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }
    @NonNull
    @Override
    public Result doWork() {
        final RequestQueue queue = RequestQueueSingleton.getInstance(getApplicationContext()).getQueue();
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("incidentData", Context.MODE_PRIVATE);
        final SharedPreferences configSharedPreferences = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        final SharedPreferences updateSharedPreferences = getApplicationContext().getSharedPreferences("updates", Context.MODE_PRIVATE);

        final StringRequest request = new StringRequest(Request.Method.GET, "https://api.cominatyou.com/silverpoint/updates", rsp -> {
            try {
                final JSONObject response = new JSONObject(rsp);
                if (response.getInt("versionCode") > BuildConfig.VERSION_CODE && response.getBoolean("breaking") && !updateSharedPreferences.getBoolean("alerted", false)) {
                    NotificationUtil.sendUpdateNotification("Required Update Available", "A required update for Silverpoint has been released. You will not be able to receive notifications regarding Discord's status until you update.", "Update", "https://cdn.cominatyou.com/silverpoint/releases/latest", getApplicationContext());
                    updateSharedPreferences.edit().putBoolean("alerted", true).putBoolean("breakingUpdateAvailable", true).apply();
                }
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }, null);
        queue.add(request);

        configSharedPreferences.edit().putLong("lastInvoked", System.currentTimeMillis()).apply();

        if (updateSharedPreferences.getBoolean("breakingUpdateAvailable", false)) return Result.success();

        final JSONObject status = GetStatus.getSync(getApplicationContext());
        if (status == null) {
            configSharedPreferences.edit().putBoolean("workerSuccess", false).apply();
            return Result.failure();
        }
        try {
            final JSONArray incidents = status.getJSONArray("incidents");
            final JSONObject latestIncident = incidents.getJSONObject(0);
            final JSONArray incidentUpdates = incidents.getJSONObject(0).getJSONArray("incident_updates");
            final JSONObject latestIncidentUpdate = incidentUpdates.getJSONObject(0);

            final String shortlink = latestIncident.getString("shortlink");
            final String incidentName = latestIncident.getString("name");
            final String incidentID = latestIncident.getString("id");
            final String latestIncidentUpdateBody = latestIncidentUpdate.getString("body");
            final String latestSeenIncidentID = sharedPreferences.getString("latestIncidentID", "");
            final String latestSeenIncidentUpdateID = sharedPreferences.getString("latestIncidentUpdateID", "");

            if (latestIncident.getString("status").equals("resolved") && !latestSeenIncidentID.equals("")) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, "View Status", shortlink, getApplicationContext());
                sharedPreferences.edit().putString("latestIncidentID", "").putString("latestIncidentUpdateID", "").apply();
            }
            // Resolved incident, but it's already been seen before, so don't do anything with it
            else if (latestIncident.getString("status").equals("resolved") && latestSeenIncidentID.equals("")) {
                configSharedPreferences.edit().putBoolean("workerSuccess", true).apply();
                return Result.success();
            }
            // if incident but has updates, display latest update
            // i am playing with fire with the latter condition
            else if (incidentUpdates.length() > 1 && !latestSeenIncidentUpdateID.equals(latestIncidentUpdate.getString("id"))) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdate.getString("body"), "View Status", shortlink, getApplicationContext());
                sharedPreferences.edit().putString("latestIncidentUpdateID", latestIncidentUpdate.getString("id")).apply();
                // in case an update is posted before the worker can get the initial incident
                if (latestSeenIncidentID.equals("")) sharedPreferences.edit().putString("latestIncidentID", incidentID).apply();
            }
            // if incident but no updates (aside from the initial), display incident
            else if (incidentUpdates.length() == 1 && !latestSeenIncidentID.equals(incidentID)) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, "View Status", shortlink, getApplicationContext());
                sharedPreferences.edit().putString("latestIncidentID", incidentID).apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
            configSharedPreferences.edit().putBoolean("workerSuccess", false).apply();
            return Result.failure();
        }
        configSharedPreferences.edit().putBoolean("workerSuccess", true).apply();
        return Result.success();
    }
}
