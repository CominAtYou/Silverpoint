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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class DebugPanel extends AppCompatActivity {

    private Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final boolean[] dontWarnAgain = {false};
        super.onCreate(savedInstanceState);
        final ActivityDebugPanelBinding binding = ActivityDebugPanelBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Debug");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        String selectedEndpoint = sharedPreferences.getString("selectedEndpoint", null);
        if (selectedEndpoint == null) {
            sharedPreferences.edit().putString("selectedEndpoint", "production").apply();
            binding.endpointRadioGroup.check(R.id.radio_prod_endpoint);
        }
        else if (selectedEndpoint.equals("production")) {
            binding.endpointRadioGroup.check(R.id.radio_prod_endpoint);
        }
        else {
            binding.endpointRadioGroup.check(R.id.radio_testing_endpoint);
        }

        binding.endpointRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_testing_endpoint) {
                if (dontWarnAgain[0]) {
                    dontWarnAgain[0] = false;
                    return;
                }
//                binding.endpointRadioGroup.check(R.id.radio_prod_endpoint);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You're about to switch to the testing endpoint. This endpoint is completely separate from the Discord status endpoint, and as such, you will not receive notifications when Discord goes down. Instead, you will receive testing notifications, which will be of no use to you, and will be frequent at times."
                        + "\n\nUnless you have been asked to, it is highly recommended to stay on the production endpoint for full functionality."
                        + "\n\nContinue anyway?")
                        .setTitle("Hold Up!");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    dontWarnAgain[0] = true;
                    sharedPreferences.edit().putString("selectedEndpoint", "testing").apply();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dontWarnAgain[0] = true;
                    binding.endpointRadioGroup.check(R.id.radio_prod_endpoint);
                });
                builder.setOnCancelListener(dialog -> {
                   dontWarnAgain[0] = true;
                   binding.endpointRadioGroup.check(R.id.radio_prod_endpoint);
                });
                builder.show();
            }
            else {
                sharedPreferences.edit().putString("selectedEndpoint", "production").apply();
            }
        });

        long lastRun = sharedPreferences.getLong("lastInvoked", 0L);
        String lastInvokedFormatted = lastRun == 0L ? "N/A" : DateUtils.getRelativeTimeSpanString(lastRun).toString();
        binding.lastRanField.setText(lastInvokedFormatted);
        boolean workerSuccess = sharedPreferences.getBoolean("workerSuccess", false);
        binding.workerResultField.setText(workerSuccess ? "Success" : "Failure");
        binding.latestIncidentId.setText(sharedPreferences.getString("latestIncidentID", "null"));
        binding.latestIncidentUpdateId.setText(sharedPreferences.getString("latestIncidentUpdateID", "null"));
        binding.apistatusStatus.setText(APIResponse.getStatus() == null ? "null": "JSONObject");
        binding.versionField.setText(String.format(Locale.getDefault(), "%s %s, %d", BuildConfig.VERSION_NAME, BuildConfig.BUILD_TYPE.toUpperCase(), BuildConfig.VERSION_CODE));
        final ZonedDateTime buildTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(BuildConfig.BUILD_TIME)), TimeZone.getTimeZone("America/Chicago").toZoneId());
        binding.buildTimestamp.setText(buildTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy h:KK a z")));
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerHandler.post(() -> {
                    long lastRun = sharedPreferences.getLong("lastInvoked", 0L);
                    String lastInvokedFormatted = lastRun == 0L ? "N/A" : DateUtils.getRelativeTimeSpanString(lastRun).toString();
                    binding.lastRanField.setText(lastInvokedFormatted);
                });
            }
        }, 0L, 1000L);
    }
}