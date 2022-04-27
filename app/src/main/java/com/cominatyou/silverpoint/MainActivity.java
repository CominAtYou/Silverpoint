package com.cominatyou.silverpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.cominatyou.silverpoint.incidentstatuspanel.IncidentStatusActivity;
import com.cominatyou.silverpoint.notifications.NotificationChannels;
import com.cominatyou.silverpoint.updates.UpdateChecker;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.DiscordQueryResult;
import com.cominatyou.silverpoint.util.NonWorkerDiscordStatusQuerier;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

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

        DynamicColors.applyIfAvailable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*
        *   Create notification channels as soon as possible so user is able to
        *   customize them to their liking as soon as possible.
        */
        NotificationChannels.createActiveIncidentChannel(getApplicationContext());
        NotificationChannels.createAvailableUpdateChannel(getApplicationContext());

        SharedPreferences updateSharedPreferences = getApplicationContext().getSharedPreferences("updates", Context.MODE_PRIVATE);
        if (updateSharedPreferences.getInt("lastSeenAppVersion", BuildConfig.VERSION_CODE) < BuildConfig.VERSION_CODE) {
            updateSharedPreferences.edit()
                    .putBoolean("alerted", false)
                    .putBoolean("breakingUpdateAvailable", false)
                    .apply();
        }
        updateSharedPreferences.edit().putInt("lastSeenAppVersion", BuildConfig.VERSION_CODE).apply();

        UpdateChecker.checkForUpdates(binding, getApplicationContext());
        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("queryDiscordStatus", ExistingPeriodicWorkPolicy.KEEP, BootReceiver.checkStatus);

        binding.startWorkerLayout.setOnClickListener(v -> {
            WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("queryDiscordStatus", ExistingPeriodicWorkPolicy.KEEP, BootReceiver.checkStatus);
            final Snackbar snackbar = Snackbar.make(binding.getRoot(), "Worker started!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
            snackbar.show();
        });

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
        binding.viewActiveIncidentLayout.setOnClickListener(v -> {
            final Intent intent = new Intent(getApplicationContext(), IncidentStatusActivity.class);
            startActivity(intent);
        });

        binding.debugLayout.setOnClickListener(v -> {
            final Intent intent = new Intent(getApplicationContext(), DebugPanel.class);
            startActivity(intent);
        });
        final IntentFilter filter = new IntentFilter("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        binding.swipeRefreshLayout.setColorSchemeColors(getColor(R.color.swipe_refresh_color));
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Run in a new thread because running network code on the UI thread is an abhorrent idea
            new Thread(() -> {
                DiscordQueryResult result = NonWorkerDiscordStatusQuerier.run(getApplicationContext());

                if (result != DiscordQueryResult.SUCCESS) runOnUiThread(() -> {
                    final String message = result == DiscordQueryResult.FAILURE ? "Something went wrong. Give it a try later." : "You'll need to update the app before you can do this.";
                    final Snackbar snackbar = Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
                    snackbar.show();
                });
                runOnUiThread(() -> binding.swipeRefreshLayout.setRefreshing(false));
            }).start();
        });
    }
}