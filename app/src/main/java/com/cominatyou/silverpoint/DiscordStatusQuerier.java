package com.cominatyou.silverpoint;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

public class DiscordStatusQuerier extends Worker {

    public DiscordStatusQuerier(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }
    @NonNull
    @Override
    public Result doWork() {
        GetStatus.get(getApplicationContext());
        JSONObject status = APIResponse.getStatus();
        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("incidentData", Context.MODE_PRIVATE);

        if (status == null) {
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
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, shortlink, getApplicationContext());
                sharedPreferences.edit().putString("latestIncidentID", "").apply();
            }
            // Resolved incident, but it's already been seen before, so don't do anything with it
            else if (latestIncident.getString("status").equals("resolved") && latestSeenIncidentID.equals("")) {
                return Result.success();
            }
            // if incident but has updates, display latest update
            // i am playing with fire with the latter condition
            else if (incidentUpdates.length() > 1 && !latestSeenIncidentUpdateID.equals(latestIncidentUpdate.getString("id"))) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdate.getString("body"), shortlink, getApplicationContext());
                sharedPreferences.edit().putString("latestIncidentUpdateID", latestIncidentUpdate.getString("id")).apply();
            }
            // if incident but no updates (aside from the initial), display incident
            else if (incidentUpdates.length() == 1 && !sharedPreferences.getString("latestIncidentID", "").equals(latestIncident.getString("id"))) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, shortlink, getApplicationContext());
                sharedPreferences.edit().putString("latestIncidentID", incidentID).apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }
}
