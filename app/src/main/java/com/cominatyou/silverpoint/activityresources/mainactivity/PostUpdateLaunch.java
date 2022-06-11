package com.cominatyou.silverpoint.activityresources.mainactivity;

import android.content.Context;
import android.content.SharedPreferences;

import com.cominatyou.silverpoint.BuildConfig;

public class PostUpdateLaunch {
    public static void handleIfOccurred(Context context) {
        SharedPreferences updateSharedPreferences = context.getSharedPreferences("updates", Context.MODE_PRIVATE);
        if (updateSharedPreferences.getInt("lastSeenAppVersion", BuildConfig.VERSION_CODE) < BuildConfig.VERSION_CODE) {
            updateSharedPreferences.edit().remove("alerted").remove("breakingUpdateAvailable").apply();
        }
    }
}
