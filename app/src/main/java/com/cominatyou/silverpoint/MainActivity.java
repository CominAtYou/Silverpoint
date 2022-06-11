package com.cominatyou.silverpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.cominatyou.silverpoint.activityresources.mainactivity.InitialSetup;
import com.cominatyou.silverpoint.activityresources.mainactivity.PostUpdateLaunch;
import com.cominatyou.silverpoint.activityresources.mainactivity.StartWorkerLayout;
import com.cominatyou.silverpoint.activityresources.mainactivity.SwipeRefreshLayout;
import com.cominatyou.silverpoint.activityresources.mainactivity.ViewActiveIncidentLayout;
import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.cominatyou.silverpoint.notifications.NotificationChannels;
import com.cominatyou.silverpoint.notifications.snoozing.SnoozeNotificationsLayoutClick;
import com.cominatyou.silverpoint.updates.UpdateChecker;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.google.android.material.color.DynamicColors;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            binding.viewActiveIncidentLayout.setEnabled(ActiveIncidentUtil.inProgress(getApplicationContext()));
            binding.viewActiveIncidentDescription.setEnabled(ActiveIncidentUtil.inProgress(getApplicationContext()));
            if (ActiveIncidentUtil.inProgress(getApplicationContext())) {
                binding.viewActiveIncidentTitle.setTextColor(getColor(R.color.info_title));
                binding.viewActiveIncidentDescription.setText(ActiveIncidentUtil.getTitle(getApplicationContext()));
            }
            else {
                binding.viewActiveIncidentTitle.setTextColor(getColor(R.color.text_disabled));
                binding.viewActiveIncidentDescription.setEnabled(false);
                binding.viewActiveIncidentDescription.setText(R.string.there_currently_isn_t_an_active_incident);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DynamicColors.applyToActivityIfAvailable(this);

        // Start the initial setup if it wasn't already completed.
        InitialSetup.startIfNotCompleted(this);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Create notification channels as soon as possible so user is able to
        // customize them to their liking as soon as possible.
        NotificationChannels.createActiveIncidentChannel(getApplicationContext());
        NotificationChannels.createAvailableUpdateChannel(getApplicationContext());

        // Do tasks necessary after an app update.
        PostUpdateLaunch.handleIfOccurred(getApplicationContext());
        getSharedPreferences("updates", Context.MODE_PRIVATE).edit().putInt("lastSeenAppVersion", BuildConfig.VERSION_CODE).apply();

        UpdateChecker.checkForUpdates(binding, getApplicationContext());
        // Start the worker
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("queryDiscordStatus", ExistingPeriodicWorkPolicy.KEEP, BootReceiver.CHECK_STATUS);

        binding.startWorkerLayout.setOnClickListener(v -> StartWorkerLayout.onClick(getApplicationContext(), binding));

        binding.viewActiveIncidentLayout.setEnabled(ActiveIncidentUtil.inProgress(getApplicationContext()));
        binding.viewActiveIncidentDescription.setEnabled(ActiveIncidentUtil.inProgress(getApplicationContext()));

        ViewActiveIncidentLayout.setText(getApplicationContext(), binding);

        binding.viewActiveIncidentLayout.setOnClickListener(v -> startActivity(new Intent(this, IncidentStatusActivity.class)));

        binding.snoozeNotificationsLayout.setOnClickListener(v -> SnoozeNotificationsLayoutClick.onClick(getSupportFragmentManager(), this));

        binding.debugLayout.setOnClickListener(v -> startActivity(new Intent(this, DebugPanelActivity.class)));

        final IntentFilter filter = new IntentFilter("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        binding.swipeRefreshLayout.setColorSchemeColors(getColor(R.color.swipe_refresh_color));
        binding.swipeRefreshLayout.setOnRefreshListener(() -> SwipeRefreshLayout.onRefresh(getApplicationContext(), this, binding));
    }
}