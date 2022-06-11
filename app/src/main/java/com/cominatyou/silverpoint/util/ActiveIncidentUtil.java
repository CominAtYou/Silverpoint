package com.cominatyou.silverpoint.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ActiveIncidentUtil {
    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("incidentData", Context.MODE_PRIVATE);
    }

    // Most values do not have setters because they should only be set in initializeIncident.

    public static String getTitle(Context context) {
       return getSharedPreferences(context).getString("title", "");
    }

    public static String getId(Context context) {
        return getSharedPreferences(context).getString("id", "");
    }

    public static String getLatestUpdateId(Context context) {
        return getSharedPreferences(context).getString("latestUpdateId", "");
    }

    // A change to an incident update will always involve a change to ID and the body.
    public static void setLatestUpdate(Context context, String id, String body) {
        getSharedPreferences(context).edit()
                .putString("latestUpdateId", id)
                .putString("body", body)
        .apply();

        Intent intent = new Intent("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static String getBody(Context context) {
        return getSharedPreferences(context).getString("body", "");
    }

    public static boolean inProgress(Context context) {
        return getSharedPreferences(context).getBoolean("inProgress", false);
    }

    public static String getLastUpdated(Context context) {
        return getSharedPreferences(context).getString("lastUpdated", "");
    }

    public static String getShortlink(Context context) {
        return getSharedPreferences(context).getString("shortlink", "");
    }

    public static void initializeIncident(Context context, String title, String id, String latestUpdateId, String body, String lastUpdated, String shortlink) {
        Log.d("ActiveIncident", String.format("A new incident was initialized with title %s and ID %s, and with an update ID of %s", title, id, latestUpdateId));
        getSharedPreferences(context).edit()
                .putString("title", title)
                .putString("id", id)
                .putString("latestUpdateId", latestUpdateId)
                .putString("body", body)
                .putBoolean("inProgress", true)
                .putString("lastUpdated", lastUpdated)
                .putString("shortlink", shortlink)
        .apply();

        Intent intent = new Intent("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void initializeIncident(Context context, String title, String id) {
        Log.d("ActiveIncident", String.format("A new incident was initialized with title %s and ID %s", title, id));
        getSharedPreferences(context).edit()
                .putString("title", title)
                .putString("id", id)
        .apply();
    }

    public static void clear(Context context) {
        Log.d("ActiveIncident", "Incident shared preferences were cleared, possibly by user invocation");
        getSharedPreferences(context).edit()
                .remove("title")
                .remove("id")
                .remove("latestUpdateId")
                .remove("body")
                .remove("inProgress")
                .remove("lastUpdated")
                .remove("shortlink")
        .apply();

        Intent intent = new Intent("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
