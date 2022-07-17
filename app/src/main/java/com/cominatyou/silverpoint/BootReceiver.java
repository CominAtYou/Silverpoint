package com.cominatyou.silverpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.cominatyou.silverpoint.remoteendpoint.DiscordStatusQuerier;

import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) { // Start WorkManager.
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.v("DiscordStatusWorker", "Starting worker");
            QueryWorker.enqueue(context, false);
        }
    }
}
