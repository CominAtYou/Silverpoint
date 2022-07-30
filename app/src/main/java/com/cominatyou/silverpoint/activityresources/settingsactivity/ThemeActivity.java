package com.cominatyou.silverpoint.activityresources.settingsactivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivitySetThemeBinding;
import com.cominatyou.silverpoint.util.Theme;
import com.google.android.material.color.DynamicColors;

public class ThemeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        final ActivitySetThemeBinding binding = ActivitySetThemeBinding.inflate(getLayoutInflater());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(binding.getRoot());

        binding.topBar.setNavigationOnClickListener(_s -> finish());

        final int currentPreference = getSharedPreferences("settings", MODE_PRIVATE).getInt("theme", Theme.THEME_SYSTEM);

        if (currentPreference == Theme.THEME_LIGHT) binding.radioGroup.check(R.id.light_option);
        if (currentPreference == Theme.THEME_DARK) binding.radioGroup.check(R.id.dark_option);
        if (currentPreference == Theme.THEME_SYSTEM) binding.radioGroup.check(R.id.follow_system_option);


        binding.radioGroup.setOnCheckedChangeListener((radioGroup, selected) -> {
            if (selected == R.id.light_option) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getSharedPreferences("settings", MODE_PRIVATE).edit().putInt("theme", Theme.THEME_LIGHT).apply();
            }
            else if (selected == R.id.dark_option) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                getSharedPreferences("settings", MODE_PRIVATE).edit().putInt("theme", Theme.THEME_DARK).apply();
            }
            else if (selected == R.id.follow_system_option) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                getSharedPreferences("settings", MODE_PRIVATE).edit().putInt("theme", Theme.THEME_SYSTEM).apply();
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("ACTION_SETTINGS_CHANGED"));
        });
    }
}
