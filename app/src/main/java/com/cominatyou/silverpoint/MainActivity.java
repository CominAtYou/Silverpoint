package com.cominatyou.silverpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.activityresources.mainactivity.InitialSetup;
import com.cominatyou.silverpoint.activityresources.mainactivity.PostUpdateLaunch;
import com.cominatyou.silverpoint.activityresources.mainactivity.SettingsLongPressBottomSheet;
import com.cominatyou.silverpoint.activityresources.mainactivity.StartWorkerLayout;
import com.cominatyou.silverpoint.activityresources.mainactivity.SwipeRefreshLayout;
import com.cominatyou.silverpoint.activityresources.mainactivity.ViewActiveIncidentLayout;
import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.cominatyou.silverpoint.notifications.NotificationChannels;
import com.cominatyou.silverpoint.updates.UpdateChecker;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.Theme;
import com.google.android.material.color.DynamicColors;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            binding.viewActiveIncidentLayout.setEnabled(ActiveIncidentUtil.inProgress(context));
            binding.viewActiveIncidentDescription.setEnabled(ActiveIncidentUtil.inProgress(context));
            if (ActiveIncidentUtil.inProgress(getApplicationContext())) {
                binding.viewActiveIncidentTitle.setTextColor(getColor(R.color.info_title));
                binding.viewActiveIncidentDescription.setText(ActiveIncidentUtil.getTitle(context));
            }
            else {
                binding.viewActiveIncidentTitle.setTextColor(getColor(R.color.text_disabled));
                binding.viewActiveIncidentDescription.setEnabled(false);
                binding.viewActiveIncidentDescription.setText(R.string.no_active_incident_explanation);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);

        // Start the initial setup if it wasn't already completed.
        InitialSetup.startIfNotCompleted(this);
        Theme.set(this);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Make the window not fit system windows.
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Create notification channels as soon as possible so user is able to
        // customize them to their liking as soon as possible.
        NotificationChannels.createActiveIncidentChannels(this);
        NotificationChannels.createAvailableUpdateChannel(this);
        NotificationChannels.createUpdateDownloadChannel(this);

        // Do tasks necessary after an app update.
        PostUpdateLaunch.handleIfOccurred(getApplicationContext());
        getSharedPreferences("updates", Context.MODE_PRIVATE).edit().putInt("lastSeenAppVersion", BuildConfig.VERSION_CODE).apply();

        UpdateChecker.checkForUpdates(this);
        // Start the worker
        QueryWorker.enqueue(this, false);

        binding.startWorkerLayout.setOnClickListener(_v -> StartWorkerLayout.onClick(this));

        binding.viewActiveIncidentLayout.setEnabled(ActiveIncidentUtil.inProgress(getApplicationContext()));
        binding.viewActiveIncidentDescription.setEnabled(ActiveIncidentUtil.inProgress(getApplicationContext()));

        ViewActiveIncidentLayout.setText(this);

        binding.viewActiveIncidentLayout.setOnClickListener(v -> startActivity(new Intent(this, IncidentStatusActivity.class)));

        binding.settingsLayout.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        binding.settingsLayout.setOnLongClickListener(v -> {
            new SettingsLongPressBottomSheet().show(getSupportFragmentManager(), SettingsLongPressBottomSheet.TAG);
            return true;
        });

        final IntentFilter filter = new IntentFilter("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        binding.swipeRefreshLayout.setColorSchemeColors(getColor(R.color.swipe_refresh_color));
        binding.swipeRefreshLayout.setOnRefreshListener(() -> SwipeRefreshLayout.onRefresh(this));
    }
}