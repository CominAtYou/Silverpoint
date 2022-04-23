package com.cominatyou.silverpoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.cominatyou.silverpoint.databinding.ActivityDebugPanelBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

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
        DynamicColors.applyIfAvailable(this);
        final ActivityDebugPanelBinding binding = ActivityDebugPanelBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setTitle("Debug");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.hide();

        SharedPreferences configSharedPreferences = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        String selectedEndpoint = configSharedPreferences.getString("selectedEndpoint", null);
        if (selectedEndpoint == null) {
            configSharedPreferences.edit().putString("selectedEndpoint", "production").apply();
        } else if (selectedEndpoint.equals("testing")) {
            binding.debugSwitch.toggle();
            binding.clearSharedPreferencesLayout.setVisibility(View.VISIBLE);
        }

        // Make the back button easier to hit
        final View parent = (View) binding.backButton.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            binding.backButton.getHitRect(rect);
            rect.top -= 100;    // increase top hit area
            rect.left -= 100;   // increase left hit area
            rect.bottom += 50; // increase bottom hit area
            rect.right += 50;  // increase right hit area
            parent.setTouchDelegate(new TouchDelegate(rect, binding.backButton));
        });

        binding.debugSwitch.setOnCheckedChangeListener((v, _t) -> {
            if (v.isChecked()) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                    .setMessage("You're about to switch to the testing endpoint. This endpoint is completely separate from the Discord status endpoint, and as such, you will not receive notifications when Discord goes down. Instead, you will receive testing notifications, which will be of no use to you, and will be frequent at times."
                        + "\n\nUnless you have been asked to, it is highly recommended to stay on the production endpoint for full functionality."
                        + "\n\nContinue anyway?")
                        .setTitle("Hold Up!")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        configSharedPreferences.edit().putString("selectedEndpoint", "testing").apply();
                        binding.clearSharedPreferencesLayout.setVisibility(View.VISIBLE);
                    })
                    .setNegativeButton("No", (dialog, which) -> binding.debugSwitch.toggle())
                    .setOnCancelListener(dialog -> binding.debugSwitch.toggle());

                AlertDialog dialog = builder.show();

                TextView dialogMessage = dialog.findViewById(android.R.id.message);
                assert dialogMessage != null;
                dialogMessage.setTypeface(ResourcesCompat.getFont(this, R.font.gs_text_regular));

                TextView dialogTitle = dialog.findViewById(R.id.alertTitle);
                assert dialogTitle != null;
                dialogTitle.setTypeface(ResourcesCompat.getFont(this, R.font.ps_regular));

            } else {
                configSharedPreferences.edit().putString("selectedEndpoint", "production").apply();
                binding.clearSharedPreferencesLayout.setVisibility(View.GONE);
            }
        });

        binding.backButton.setOnClickListener(_s -> finish());

        binding.clearSharedPreferencesLayout.setOnClickListener(_s -> {
            ActiveIncidentUtil.clear(getApplicationContext());
            Snackbar snackbar = Snackbar.make(binding.getRoot(), "Incident Shared Preferences have been cleared.", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
            snackbar.show();
        });

        binding.scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Wait for title to be able to be un/eclipsed by action bar
            if (scrollY < 260 || oldScrollY > scrollY && scrollY < 410) {
                actionBar.hide();
            }
            else {
                actionBar.show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}