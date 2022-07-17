package com.cominatyou.silverpoint;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.cominatyou.silverpoint.remoteendpoint.DiscordStatusQuerier;

import java.util.concurrent.TimeUnit;

public class QueryWorker {
    private static final Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

    public static void enqueue(Context context, Boolean replaceExistingWorker) {
        if (!context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("incidentChecksEnabled", true)) return;

        final int interval = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("updateFrequencyMinutes", 15);
        final PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(DiscordStatusQuerier.class, interval, TimeUnit.MINUTES).setConstraints(constraints).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork("queryDiscordStatus", replaceExistingWorker ? ExistingPeriodicWorkPolicy.REPLACE : ExistingPeriodicWorkPolicy.KEEP, request);
    }
}
