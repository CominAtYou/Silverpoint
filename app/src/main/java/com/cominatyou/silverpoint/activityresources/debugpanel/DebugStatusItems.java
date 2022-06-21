package com.cominatyou.silverpoint.activityresources.debugpanel;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import com.cominatyou.silverpoint.BuildConfig;
import com.cominatyou.silverpoint.databinding.ActivityDebugPanelBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

public class DebugStatusItems {
    public static void update(Context context, ActivityDebugPanelBinding binding) {
        final SharedPreferences configSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        long lastRun = configSharedPreferences.getLong("lastInvoked", 0L);
        String lastInvokedFormatted = lastRun == 0L ? "N/A" : DateUtils.getRelativeTimeSpanString(lastRun).toString();
        binding.lastRanField.setText(lastInvokedFormatted.equals("0 minutes ago") ? "Just now" : lastInvokedFormatted);

        boolean workerSuccess = configSharedPreferences.getBoolean("workerSuccess", false);
        binding.workerResultField.setText(workerSuccess ? "Success" : "Failure");

        final String latestIncidentID = ActiveIncidentUtil.getId(context);
        binding.latestIncidentId.setText(latestIncidentID.equals("") ? "null" : latestIncidentID);

        final String latestIncidentUpdateID = ActiveIncidentUtil.getLatestUpdateId(context);
        binding.latestIncidentUpdateId.setText(latestIncidentUpdateID.equals("") ? "null" : latestIncidentUpdateID);
        binding.versionField.setText(String.format(Locale.getDefault(), "%s %s, %d", BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE, BuildConfig.VERSION_CODE));

        final ZonedDateTime buildTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(BuildConfig.BUILD_TIME)), TimeZone.getTimeZone("America/Chicago").toZoneId());
        binding.buildTimestamp.setText(buildTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy h:KK a z")));
    }
}