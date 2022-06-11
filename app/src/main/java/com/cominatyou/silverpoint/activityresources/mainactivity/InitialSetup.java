package com.cominatyou.silverpoint.activityresources.mainactivity;

import android.content.Context;
import android.content.Intent;

import com.cominatyou.silverpoint.MainActivity;
import com.cominatyou.silverpoint.onboarding.WelcomeScreen;

public class InitialSetup {
    public static void startIfNotCompleted(MainActivity activity) {
        if (!activity.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("completedsetup", false)) {
            activity.startActivity(new Intent(activity, WelcomeScreen.class));
            activity.finish();
        }
    }
}
