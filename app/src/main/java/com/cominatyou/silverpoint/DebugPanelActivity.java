package com.cominatyou.silverpoint;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cominatyou.silverpoint.databinding.ActivityDebugPanelBinding;
import com.cominatyou.silverpoint.activityresources.debugpanel.ClearSharedPreferences;
import com.cominatyou.silverpoint.activityresources.debugpanel.DebugStatusItems;
import com.cominatyou.silverpoint.activityresources.debugpanel.DebugSwitch;
import com.google.android.material.color.DynamicColors;

import java.util.Timer;
import java.util.TimerTask;

public class DebugPanelActivity extends AppCompatActivity {

    private final Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);

        final ActivityDebugPanelBinding binding = ActivityDebugPanelBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        DebugSwitch.toggleBasedOnPreference(getApplicationContext(), binding);

        binding.debugSwitch.setOnCheckedChangeListener((v, _t) -> DebugSwitch.onChange(v, this, binding));

        binding.backButton.setOnClickListener(_s -> finish());

        binding.clearSharedPreferencesLayout.setOnClickListener(_s -> ClearSharedPreferences.onClick(getApplicationContext(), binding));

        // TODO: Use broadcasts for this.
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timerHandler.post(() -> DebugStatusItems.update(getApplicationContext(), binding));
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