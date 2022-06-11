package com.cominatyou.silverpoint.remoteendpoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.cominatyou.silverpoint.BuildConfig;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.notifications.NotificationUtil;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.RequestQueueSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NonWorkerDiscordStatusQuerier {

    public static DiscordQueryResult run(Context context) {
        final RequestQueue queue = RequestQueueSingleton.getInstance(context).getQueue();
        final SharedPreferences updateSharedPreferences = context.getSharedPreferences("updates", Context.MODE_PRIVATE);

        final StringRequest request = new StringRequest(Request.Method.GET, "https://api.cominatyou.com/silverpoint/updates", rsp -> {
            try {
                final JSONObject response = new JSONObject(rsp);
                if (response.getInt("versionCode") > BuildConfig.VERSION_CODE && response.getBoolean("breaking") && !updateSharedPreferences.getBoolean("alerted", false)) {
                    NotificationUtil.sendUpdateNotification("Required Update Available", "A required update for " + context.getString(R.string.app_name) + " has been released. You will not be able to receive notifications regarding Discord's status until you update.", "Update", "https://cdn.cominatyou.com/silverpoint/releases/latest", context);
                    updateSharedPreferences.edit().putBoolean("alerted", true).putBoolean("breakingUpdateAvailable", true).apply();
                }
            }
            catch(JSONException e) {
                e.printStackTrace();
            }
        }, null);
        request.setShouldCache(false);
        queue.add(request);

        if (updateSharedPreferences.getBoolean("breakingUpdateAvailable", false)) {
            Log.d("SwipeToRefreshThread", "A breaking update is available, stopping");
            return DiscordQueryResult.UPDATE_REQUIRED;
        }

        final JSONObject status = GetStatus.getSync(context);
        if (status == null) {
            return DiscordQueryResult.FAILURE;
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
            if (latestIncident.getString("status").equals("resolved") && ActiveIncidentUtil.inProgress(context)) {
                NotificationUtil.sendWithoutTapAction("Discord: " + incidentName, latestIncidentUpdateBody, "View Status", shortlink, context);
                ActiveIncidentUtil.clear(context);
            }

            // Resolved incident, but it's already been seen before, so don't do anything with it
            else if (latestIncident.getString("status").equals("resolved") && !ActiveIncidentUtil.inProgress(context)) {
                return DiscordQueryResult.SUCCESS;
            }

            // if incident but has updates, display latest update
            else if (incidentUpdates.length() > 1 && !ActiveIncidentUtil.getLatestUpdateId(context).equals(latestIncidentUpdateId)) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, "View Status", shortlink, context);
                ActiveIncidentUtil.setLatestUpdate(context, latestIncidentUpdateId, latestIncidentUpdateBody);
                // in case an update is posted before the worker can get the initial incident
                if (!ActiveIncidentUtil.inProgress(context)) ActiveIncidentUtil.initializeIncident(context, incidentName, incidentID);
            }

            // if incident but no updates (aside from the initial), display incident
            else if (incidentUpdates.length() == 1 && !ActiveIncidentUtil.getId(context).equals(incidentID)) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, "View Status", shortlink, context);
                ActiveIncidentUtil.initializeIncident(context, incidentName, incidentID, latestIncidentUpdateId, latestIncidentUpdateBody, lastUpdated, shortlink);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return DiscordQueryResult.FAILURE;
        }
        return DiscordQueryResult.SUCCESS;
    }
}
