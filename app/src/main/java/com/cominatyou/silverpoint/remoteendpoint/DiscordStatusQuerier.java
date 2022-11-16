package com.cominatyou.silverpoint.remoteendpoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.cominatyou.silverpoint.BuildConfig;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.notifications.NotificationChannels;
import com.cominatyou.silverpoint.notifications.NotificationUtil;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.RequestQueueSingleton;

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
        final SharedPreferences configSharedPreferences = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        final SharedPreferences updateSharedPreferences = getApplicationContext().getSharedPreferences("updates", Context.MODE_PRIVATE);

        Log.v("DiscordStatusWorker", "Worker has been invoked, starting work");

        final StringRequest request = new StringRequest(Request.Method.GET, "https://api.cominatyou.com/silverpoint/updates", rsp -> {
            try {
                final JSONObject response = new JSONObject(rsp);
                if (response.getInt("versionCode") > BuildConfig.VERSION_CODE && response.getBoolean("breaking") && !updateSharedPreferences.getBoolean("alerted", false)) {
                    NotificationUtil.sendUpdateNotification("Required update available", "A required update for " + getApplicationContext().getString(R.string.app_name) + " has been released. You will not be able to receive notifications regarding Discord's status until you update.", "Update", "https://cdn.cominatyou.com/silverpoint/releases/latest", getApplicationContext());
                    updateSharedPreferences.edit().putBoolean("alerted", true).putBoolean("breakingUpdateAvailable", true).apply();
                }
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }, null);
        request.setShouldCache(false);
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
            final String lastUpdated = latestIncident.has("updated_at") ? latestIncident.getString("updated_at") : latestIncident.getString("created_at");
            final String latestIncidentUpdateBody = latestIncidentUpdate.getString("body");
            final String latestIncidentUpdateId = latestIncidentUpdate.getString("id");

            // Incident is resolved
            if (latestIncident.getString("status").equals("resolved") && ActiveIncidentUtil.inProgress(getApplicationContext())) {
                NotificationUtil.sendWithoutTapAction("Discord: " + incidentName, latestIncidentUpdateBody, "View Status", shortlink, NotificationChannels.ActiveIncidents.CHANNEL_INCIDENT_UPDATES, getApplicationContext());
                ActiveIncidentUtil.clear(getApplicationContext());
            }

            // Resolved incident, but it's already been seen before, so don't do anything with it
            else if (latestIncident.getString("status").equals("resolved") && !ActiveIncidentUtil.inProgress(getApplicationContext())) {
                configSharedPreferences.edit().putBoolean("workerSuccess", true).apply();
                return Result.success();
            }

            // if incident but has updates, display latest update
            else if (incidentUpdates.length() > 1 && !ActiveIncidentUtil.getLatestUpdateId(getApplicationContext()).equals(latestIncidentUpdateId)) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, "View Status", shortlink, NotificationChannels.ActiveIncidents.CHANNEL_INCIDENT_UPDATES, getApplicationContext());
                ActiveIncidentUtil.setLatestUpdate(getApplicationContext(), latestIncidentUpdateId, latestIncidentUpdateBody, lastUpdated);
                // in case an update is posted before the worker can get the initial incident
                if (!ActiveIncidentUtil.inProgress(getApplicationContext())) ActiveIncidentUtil.initializeIncident(getApplicationContext(), incidentName, incidentID, shortlink);
            }

            // if incident but no updates (aside from the initial), display incident
            else if (incidentUpdates.length() == 1 && !ActiveIncidentUtil.getId(getApplicationContext()).equals(incidentID)) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, "View Status", shortlink, NotificationChannels.ActiveIncidents.CHANNEL_NEW_INCIDENT, getApplicationContext());
                ActiveIncidentUtil.initializeIncident(getApplicationContext(), incidentName, incidentID, latestIncidentUpdateId, latestIncidentUpdateBody, lastUpdated, shortlink);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            configSharedPreferences.edit().putBoolean("workerSuccess", false).apply();
            Log.e("DiscordStatusWorker", "Worker failed in the JSON parse/notification step");
            return Result.failure();
        }
        configSharedPreferences.edit().putBoolean("workerSuccess", true).apply();
        Log.v("DiscordStatusWorker", "Worker was successful");
        return Result.success();
    }
}
