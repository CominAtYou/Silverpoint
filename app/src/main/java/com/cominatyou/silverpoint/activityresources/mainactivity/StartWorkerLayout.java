package com.cominatyou.silverpoint.activityresources.mainactivity;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.cominatyou.silverpoint.BootReceiver;
import com.cominatyou.silverpoint.MainActivity;
import com.cominatyou.silverpoint.QueryWorker;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class StartWorkerLayout {
    public static void onClick(MainActivity activity) {
        if (!activity.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("incidentChecksEnabled", true)) {
            final Snackbar snackbar = Snackbar.make(activity.binding.getRoot(), "Automatic incident checks are disabled. Turn them on in the app settings to start the worker.", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
            snackbar.show();

            return;
        }


        QueryWorker.enqueue(activity, false);
        final Snackbar snackbar = Snackbar.make(activity.binding.getRoot(), "Worker started!", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
        snackbar.show();
    }
}
