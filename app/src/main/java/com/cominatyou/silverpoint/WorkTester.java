package com.cominatyou.silverpoint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class WorkTester {

    protected static void test(Context context) {
        GetStatus.get(context);
        JSONObject status = APIResponse.getStatus();
        final SharedPreferences sharedPreferences = context.getSharedPreferences("incidentData", Context.MODE_PRIVATE);

        if (status == null) {
            Toast.makeText(context, "Seems like we didn't get anything.", Toast.LENGTH_LONG).show();
            return;
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
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, shortlink, context);
            }
            // Resolved incident, but it's already been seen before, so don't do anything with it
            else if (latestIncident.getString("status").equals("resolved") && latestSeenIncidentID.equals("")) {
                Toast.makeText(context, "Got a response! Nothing new here.", Toast.LENGTH_LONG).show();
            }
            // if incident but has updates, display latest update
            // i am playing with fire with the latter condition
            else if (incidentUpdates.length() > 1 && !latestSeenIncidentUpdateID.equals(latestIncidentUpdate.getString("id"))) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdate.getString("body"), shortlink, context);
            }
            // if incident but no updates (aside from the initial), display incident
            else if (incidentUpdates.length() == 1 && !sharedPreferences.getString("latestIncidentID", "").equals(latestIncident.getString("id"))) {
                NotificationUtil.send("Discord: " + incidentName, latestIncidentUpdateBody, shortlink, context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Something went wrong.", Toast.LENGTH_LONG).show();
        }
    }
}
