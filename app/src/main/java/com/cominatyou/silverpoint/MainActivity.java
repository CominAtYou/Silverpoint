package com.cominatyou.silverpoint;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.cominatyou.silverpoint.notifications.NotificationChannels;
import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.cominatyou.silverpoint.updates.UpdateChecker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

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
            Toast.makeText(getApplicationContext(), "Worker started!", Toast.LENGTH_LONG).show();
        });

        binding.testWorkerLayout.setOnClickListener(v -> WorkTester.test(getApplicationContext()));

        binding.debugLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DebugPanel.class);
            startActivity(intent);
        });
    }
}