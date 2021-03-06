package com.cominatyou.silverpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    private static final Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
    public static final PeriodicWorkRequest checkStatus = new PeriodicWorkRequest.Builder(DiscordStatusQuerier.class, 15, TimeUnit.MINUTES).setConstraints(constraints).build();
    @Override
    public void onReceive(Context context, Intent intent) { // Start WorkManager.
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork("queryDiscordStatus", ExistingPeriodicWorkPolicy.KEEP, checkStatus);
        }
    }
}
