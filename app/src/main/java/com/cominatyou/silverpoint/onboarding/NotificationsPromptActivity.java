package com.cominatyou.silverpoint.onboarding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.MainActivity;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityNotificationsPromptBinding;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationsPromptActivity extends AppCompatActivity {
    private ActivityNotificationsPromptBinding binding;
    /**
     * Whether or not the permission check in {@code onResume()} should be run.
     */
    private boolean shouldRunNotificationsPermissionCheckOnResume = false;

    @SuppressLint("InlinedApi")
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
        if (granted) {
            Log.v("NotificationPermissionRequest", "Notification permission was granted");

            getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("completedsetup", true).apply();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("SETUP_COMPLETED")); // Notify the welcome screen that setup is done
            finish();
        }
        else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            Log.v("NotificationPermissionRequest", "Notification permission was denied");

            final Snackbar snackbar = Snackbar.make(binding.getRoot(), "Notifications were denied. You'll need to enable them to use the app.", 7000);
            snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
            snackbar.setAction("Enable", v -> {
                this.requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                snackbar.dismiss();
            });
            snackbar.show();
        }
        else {
            Log.v("NotificationPermissionRequest", "Notifications were manually turned off or Android is refusing to present the permission dialog");

            final Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

            final Snackbar snackbar = Snackbar.make(binding.getRoot(), "Notifications were denied. Enable them in Android settings to use the app.", 7000);
            snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
            snackbar.setAction("Enable", v -> {
                Log.v("NotificationPermissionRequest", "Presenting Android notification settings page for app");
                startActivity(intent);
                shouldRunNotificationsPermissionCheckOnResume = true;
            });
            snackbar.show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        binding = ActivityNotificationsPromptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            Log.v("NotificationPermissionRequest", "App already has notification permission");
            getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("completedsetup", true).apply();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("SETUP_COMPLETED"));
            finish();
        }

        binding.okButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.v("NotificationPermissionRequest", "Presenting notification permission request dialog");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
            else {
                Log.v("NotificationPermissionRequest", "Device is < SDK 33 and notifications have been manually turned off, presenting Android notification settings for app");
                final Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                startActivity(intent);
                shouldRunNotificationsPermissionCheckOnResume = true;
            }
        });

        binding.buttonBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRunNotificationsPermissionCheckOnResume) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()) {
                        Log.v("NotificationPermissionRequest", "Notification permission was granted in Android settings");
                        getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("completedsetup", true).apply();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("SETUP_COMPLETED"));
                        finish();
                    }
                    else {
                        shouldRunNotificationsPermissionCheckOnResume = true;
                    }
                }
            }, 750);
        }
    }
}
