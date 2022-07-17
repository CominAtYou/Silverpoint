package com.cominatyou.silverpoint.activityresources.settingsactivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;

import androidx.work.WorkManager;

import com.cominatyou.silverpoint.QueryWorker;
import com.cominatyou.silverpoint.SettingsActivity;
import com.cominatyou.silverpoint.databinding.ActivitySettingsBinding;

public class AutomaticIncidentChecks {
    public static void setPreference(CompoundButton button, Boolean isChecked) {
        button.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit().putBoolean("incidentChecksEnabled", isChecked).apply();

        if (isChecked) QueryWorker.enqueue(button.getContext(), false);
        else WorkManager.getInstance(button.getContext()).cancelUniqueWork("queryDiscordStatus");
    }
}
