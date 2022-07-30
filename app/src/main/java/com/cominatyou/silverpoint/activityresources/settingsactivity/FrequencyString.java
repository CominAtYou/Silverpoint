package com.cominatyou.silverpoint.activityresources.settingsactivity;

import android.content.Context;

import com.cominatyou.silverpoint.R;

public class FrequencyString {
    public static String get(Context context) {
        final int minutes = context.getSharedPreferences("settings", Context.MODE_PRIVATE).getInt("updateFrequencyMinutes", 15);

        if (minutes == 15) return context.getString(R.string.settings_frequency_select_layout_fifteen_minutes_description);
        else if (minutes == 30) return context.getString(R.string.settings_frequency_select_layout_thirty_minutes_description);
        else return context.getString(R.string.settings_frequency_select_layout_one_hour_description);
    }
}
