package com.cominatyou.silverpoint.activityresources.mainactivity;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.cominatyou.silverpoint.BootReceiver;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class StartWorkerLayout {
    public static void onClick(Context context, ActivityMainBinding binding) {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("queryDiscordStatus", ExistingPeriodicWorkPolicy.KEEP, BootReceiver.CHECK_STATUS);
        final Snackbar snackbar = Snackbar.make(binding.getRoot(), "Worker started!", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
        snackbar.show();
    }
}
