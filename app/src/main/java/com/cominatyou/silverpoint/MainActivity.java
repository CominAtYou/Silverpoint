package com.cominatyou.silverpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import com.cominatyou.silverpoint.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("queryDiscordStatus", ExistingPeriodicWorkPolicy.KEEP, BootReceiver.checkStatus);

        binding.startWorker.setOnClickListener(v -> {
            WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork("queryDiscordStatus", ExistingPeriodicWorkPolicy.KEEP, BootReceiver.checkStatus);
            Toast.makeText(getApplicationContext(), "Worker started!", Toast.LENGTH_LONG).show();
        });

        binding.testWorker.setOnClickListener(v -> WorkTester.test(getApplicationContext()));

        binding.querySharedPrefs.setOnClickListener(v -> {
//            new SharedPrefsDialogFragment().show(getSupportFragmentManager(), SharedPrefsDialogFragment.TAG)
            Intent intent = new Intent(getApplicationContext(), DebugPanel.class);
            startActivity(intent);
        });
    }
}