package com.cominatyou.silverpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.cominatyou.silverpoint.activityresources.debugpanel.ClearSharedPreferences;
import com.cominatyou.silverpoint.activityresources.debugpanel.DebugStatusItems;
import com.cominatyou.silverpoint.activityresources.debugpanel.DebugSwitch;
import com.cominatyou.silverpoint.databinding.ActivityDebugPanelBinding;
import com.google.android.material.color.DynamicColors;

public class DebugPanelActivity extends AppCompatActivity {
    public ActivityDebugPanelBinding binding;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DebugStatusItems.update(getApplicationContext(), binding);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding = ActivityDebugPanelBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        DebugSwitch.toggleBasedOnPreference(getApplicationContext(), binding);

        binding.debugSwitch.setOnCheckedChangeListener((v, _t) -> DebugSwitch.onChange(v, this));

        binding.topBar.setNavigationOnClickListener(v -> finish());

        binding.clearSharedPreferencesLayout.setOnClickListener(_s -> ClearSharedPreferences.onClick(this));

        final IntentFilter filter = new IntentFilter();
        filter.addAction("INCIDENT_UPDATED");
        filter.addAction(Intent.ACTION_TIME_TICK);

        registerReceiver(broadcastReceiver, filter);

        DebugStatusItems.update(getApplicationContext(), binding);
    }
}