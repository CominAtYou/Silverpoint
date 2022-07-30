package com.cominatyou.silverpoint.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.cominatyou.silverpoint.R;

public class Theme {
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    public static void set(Activity activity) {
        final int preference = activity.getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("theme", THEME_SYSTEM);

        if (preference == THEME_LIGHT) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else if (preference == THEME_DARK) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static String getUserPreferenceAsString(Context context) {
        final int preference = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("theme", THEME_SYSTEM);

        if (preference == THEME_LIGHT) return context.getString(R.string.settings_theme_layout_light_description);
        else if (preference == THEME_DARK) return context.getString(R.string.settings_theme_layout_dark_description);
        else return context.getString(R.string.settings_theme_layout_follow_system_description);
    }
}
