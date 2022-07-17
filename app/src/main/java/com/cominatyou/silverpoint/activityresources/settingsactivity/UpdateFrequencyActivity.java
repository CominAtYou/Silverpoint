package com.cominatyou.silverpoint.activityresources.settingsactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.QueryWorker;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivitySetUpdateFrequencyBinding;
import com.google.android.material.color.DynamicColors;

public class UpdateFrequencyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        ActivitySetUpdateFrequencyBinding binding = ActivitySetUpdateFrequencyBinding.inflate(getLayoutInflater());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(binding.getRoot());

        binding.topBar.setNavigationOnClickListener(_s -> finish());

        final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

        int updateFrequency = preferences.getInt("updateFrequencyMinutes", 15);

        if (updateFrequency == 15) binding.radioGroup.check(R.id.fifteen_minutes_option);
        if (updateFrequency == 30) binding.radioGroup.check(R.id.thirty_minutes_option);
        if (updateFrequency == 60) binding.radioGroup.check(R.id.one_hour_option);

        binding.radioGroup.setOnCheckedChangeListener((radioGroup, checked) -> {
            if (checked == R.id.fifteen_minutes_option) preferences.edit().putInt("updateFrequencyMinutes", 15).apply();
            if (checked == R.id.thirty_minutes_option) preferences.edit().putInt("updateFrequencyMinutes", 30).apply();
            if (checked == R.id.one_hour_option) preferences.edit().putInt("updateFrequencyMinutes", 60).apply();

            QueryWorker.enqueue(this, true); // Replace the worker with the new setting

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("ACTION_SETTINGS_CHANGED"));
        });
    }
}
