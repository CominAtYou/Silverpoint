package com.cominatyou.silverpoint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cominatyou.silverpoint.databinding.ActivityDebugPanelBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class DebugPanel extends AppCompatActivity {

    private final Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityDebugPanelBinding binding = ActivityDebugPanelBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Debug");

        SharedPreferences configSharedPreferences = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        String selectedEndpoint = configSharedPreferences.getString("selectedEndpoint", null);
        if (selectedEndpoint == null) {
            configSharedPreferences.edit().putString("selectedEndpoint", "production").apply();
        } else if (selectedEndpoint.equals("testing")) {
            binding.debugSwitch.toggle();
        }

        binding.debugSwitch.setOnCheckedChangeListener((v, _t) -> {
            if (v.isChecked()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You're about to switch to the testing endpoint. This endpoint is completely separate from the Discord status endpoint, and as such, you will not receive notifications when Discord goes down. Instead, you will receive testing notifications, which will be of no use to you, and will be frequent at times."
                        + "\n\nUnless you have been asked to, it is highly recommended to stay on the production endpoint for full functionality."
                        + "\n\nContinue anyway?")
                        .setTitle("Hold Up!");
                builder.setPositiveButton("Yes", (dialog, which) -> configSharedPreferences.edit().putString("selectedEndpoint", "testing").apply());
                builder.setNegativeButton("No", (dialog, which) -> binding.debugSwitch.toggle());
                builder.setOnCancelListener(dialog -> binding.debugSwitch.toggle());
                builder.show();
            } else {
                configSharedPreferences.edit().putString("selectedEndpoint", "production").apply();
            }
        });

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerHandler.post(() -> {
                    long lastRun = configSharedPreferences.getLong("lastInvoked", 0L);
                    String lastInvokedFormatted = lastRun == 0L ? "N/A" : DateUtils.getRelativeTimeSpanString(lastRun).toString();
                    binding.lastRanField.setText(lastInvokedFormatted.equals("0 minutes ago") ? "Just now" : lastInvokedFormatted);
                    boolean workerSuccess = configSharedPreferences.getBoolean("workerSuccess", false);
                    binding.workerResultField.setText(workerSuccess ? "Success" : "Failure");
                    final String latestIncidentID = ActiveIncidentUtil.getId(getApplicationContext());
                    binding.latestIncidentId.setText(latestIncidentID.equals("") ? "null" : latestIncidentID);
                    final String latestIncidentUpdateID = ActiveIncidentUtil.getLatestUpdateId(getApplicationContext());
                    binding.latestIncidentUpdateId.setText(latestIncidentUpdateID.equals("") ? "null" : latestIncidentUpdateID);
                    binding.versionField.setText(String.format(Locale.getDefault(), "%s %s, %d", BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE.toUpperCase(Locale.getDefault()), BuildConfig.VERSION_CODE));
                    final ZonedDateTime buildTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(BuildConfig.BUILD_TIME)), TimeZone.getTimeZone("America/Chicago").toZoneId());
                    binding.buildTimestamp.setText(buildTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy h:KK a z")));
                });
            }
        }, 0L, 1000L);
    }
}