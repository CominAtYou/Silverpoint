package com.cominatyou.silverpoint.onboarding;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.databinding.ActivityWelcomeScreenBinding;
import com.google.android.material.color.DynamicColors;

public class WelcomeScreen extends AppCompatActivity {
    @SuppressWarnings("all")
    private ActivityWelcomeScreenBinding binding;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);

        binding = ActivityWelcomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getStartedButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NotificationsPromptActivity.class)));

        // Destroy the activity when setup completes so the user can't use the back button to return to it
        final IntentFilter filter = new IntentFilter("SETUP_COMPLETED");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
}
