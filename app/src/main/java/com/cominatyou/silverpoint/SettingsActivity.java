package com.cominatyou.silverpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.activityresources.settingsactivity.*;
import com.cominatyou.silverpoint.databinding.ActivitySettingsBinding;
import com.cominatyou.silverpoint.notifications.snoozing.SnoozeNotificationsBottomSheet;
import com.cominatyou.silverpoint.notifications.snoozing.SnoozeNotificationsLayoutClick;
import com.cominatyou.silverpoint.util.Theme;
import com.google.android.material.color.DynamicColors;

public class SettingsActivity extends AppCompatActivity {
    public ActivitySettingsBinding binding;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            binding.frequencySelectDescription.setText(FrequencyString.get(context));
            binding.themeDescription.setText(Theme.getUserPreferenceAsString(context));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(binding.getRoot());

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("ACTION_SETTINGS_CHANGED"));

        final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        binding.topBar.setNavigationOnClickListener(_s -> finish());

        binding.incidentChecksSiwtch.setChecked(preferences.getBoolean("incidentChecksEnabled", true));

        binding.incidentChecksLayout.setOnClickListener(_s -> binding.incidentChecksSiwtch.toggle());
        binding.incidentChecksSiwtch.setOnCheckedChangeListener(AutomaticIncidentChecks::setPreference);

        binding.frequencySelectLayout.setOnClickListener(_s -> startActivity(new Intent(this, UpdateFrequencyActivity.class)));
        binding.frequencySelectDescription.setText(FrequencyString.get(this));

        binding.snoozeNotificationsLayout.setOnClickListener(_s -> SnoozeNotificationsLayoutClick.onClick(getSupportFragmentManager(), this));

        binding.themeLayout.setOnClickListener(_s -> startActivity(new Intent(this, ThemeActivity.class)));
        binding.themeDescription.setText(Theme.getUserPreferenceAsString(this));

        binding.aboutLayout.setOnClickListener(_s -> startActivity(new Intent(this, AboutActivity.class)));
    }
}