package com.cominatyou.silverpoint.updates;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.browser.customtabs.CustomTabsIntent;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.cominatyou.silverpoint.BuildConfig;
import com.cominatyou.silverpoint.MainActivity;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.util.RequestQueueSingleton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateChecker { // TODO: Add logging
    public static void checkForUpdates(MainActivity activity) {
        SharedPreferences updatePreferences = activity.getSharedPreferences("updates", Context.MODE_PRIVATE);
        final RequestQueue queue = RequestQueueSingleton.getInstance(activity).getQueue();
        final CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();

        final StringRequest request = new StringRequest(Request.Method.GET, "https://api.cominatyou.com/silverpoint/updates", rsp -> {
            Log.v("UpdateChecker", "Got a response!");
            try {
                JSONObject response = new JSONObject(rsp);
                final String newVersion = response.getString("version");
                if (response.getInt("versionCode") > BuildConfig.VERSION_CODE && !response.getBoolean("breaking")) {
                    Log.v("UpdateChecker", String.format("A new version is available (%s)", response.getString("version")));
                    Snackbar snackbar = Snackbar.make(activity.binding.getRoot(), String.format("Update available (%s)", newVersion), Snackbar.LENGTH_LONG).setAction("Update", v -> customTabsIntent.launchUrl(activity, Uri.parse("https://cdn.cominatyou.com/silverpoint/releases/latest")));
                    snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
                    snackbar.show();
                }
                else if (response.getInt("versionCode") > BuildConfig.VERSION_CODE && response.getBoolean("breaking")) {
                    Log.v("UpdateChecker", String.format("Breaking update available (%s). Disabling most app functionality until update is installed.", response.getString("version")));
                    Snackbar snackbar = Snackbar.make(activity.binding.getRoot(), "Update available. You will not be able to receive notifications until you update.", Snackbar.LENGTH_INDEFINITE).setAction("Update", v -> customTabsIntent.launchUrl(activity, Uri.parse("https://cdn.cominatyou.com/silverpoint/releases/latest")));
                    snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
                    snackbar.show();

                    final TextView[] buttonTitles = { activity.binding.startWorkerTitle, activity.binding.viewActiveIncidentTitle, activity.binding.snoozeNotificationsTitle, activity.binding.debugTitle };
                    for (TextView titleText : buttonTitles) {
                        titleText.setTextColor(activity.getColor(R.color.text_disabled));
                    }
                    final View[] buttonElements = { activity.binding.startWorkerLayout, activity.binding.startWorkerDescription, activity.binding.viewActiveIncidentLayout, activity.binding.snoozeNotificationsLayout, activity.binding.snoozeNotificationsDescription, activity.binding.debugLayout, activity.binding.debugDescription };
                    for (View buttonElement : buttonElements) {
                        buttonElement.setEnabled(false);
                    }
                    updatePreferences.edit().putBoolean("breakingUpdateAvailable", true).apply();
                }
            }
            catch (JSONException e) {
                Log.e("UpdateChecker", "JSON parser failed");
                e.printStackTrace();
            }
        }, null);
        request.setShouldCache(false);
        queue.add(request);
        Log.v("UpdateChecker", "Queued the update endpoint request");
    }
}
