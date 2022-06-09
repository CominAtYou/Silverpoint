package com.cominatyou.silverpoint.onboarding;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.MainActivity;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityNotificationsPromptBinding;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationsPromptActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    @SuppressWarnings("all")
    private ActivityNotificationsPromptBinding binding;
    private boolean shouldRunOnResume = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 0x1) return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("completedsetup", true).apply();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("SETUP_COMPLETED"));
            finish();
        }
        else {
            if (shouldShowRequestPermissionRationale("android.permission.POST_NOTIFICATIONS")) {
                final Snackbar snackbar = Snackbar.make(binding.getRoot(), "Notifications were denied. You'll need to enable them to use the app.", 7000);
                snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
                snackbar.setAction("Enable", v -> {
                    requestPermissions(new String[]{ "android.permission.POST_NOTIFICATIONS" }, 0x1);
                    snackbar.dismiss();
                });
                snackbar.show();
            }
            else {
                final Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());

                final Snackbar snackbar = Snackbar.make(binding.getRoot(), "Notifications were denied. Enable them in Android settings to use the app.", 7000);
                snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
                snackbar.setAction("Enable", v -> {
                    startActivity(intent);
                    shouldRunOnResume = true;
                });
                snackbar.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);

        binding = ActivityNotificationsPromptBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.okButton.setOnClickListener(v -> {
            if (NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()) {
                getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("completedsetup", true).apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("SETUP_COMPLETED"));
                finish();
            }
            else requestPermissions(new String[]{ "android.permission.POST_NOTIFICATIONS" }, 0x1);
        });

        binding.buttonBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldRunOnResume) {

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()) {
                        getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("completedsetup", true).apply();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("SETUP_COMPLETED"));
                        finish();
                    }
                }
            }, 750);
        }
    }
}
