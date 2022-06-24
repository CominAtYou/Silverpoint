package com.cominatyou.silverpoint.activityresources.debugpanel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.cominatyou.silverpoint.DebugPanelActivity;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityDebugPanelBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DebugSwitch {
    public static void onChange(CompoundButton v, DebugPanelActivity activity) {
        final SharedPreferences configSharedPreferences = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
        if (v.isChecked()) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                    .setMessage("You're about to switch to the testing endpoint. This endpoint is completely separate from the Discord status endpoint, and as such, you will not receive notifications when Discord goes down. Instead, you will receive testing notifications, which will be of no use to you, and will be frequent at times."
                            + "\n\nUnless you have been asked to, it is highly recommended to stay on the production endpoint for full functionality."
                            + "\n\nContinue anyway?")
                    .setTitle("Hold Up!")
                    .setPositiveButton(activity.getString(R.string.yes), (dialog, which) -> {
                        Log.v("DebugMode", "Debug mode was enabled");
                        configSharedPreferences.edit().putString("selectedEndpoint", "testing").apply();
                        activity.binding.clearSharedPreferencesLayout.setVisibility(View.VISIBLE);
                    })
                    .setNegativeButton(activity.getString(R.string.no), (dialog, which) -> activity.binding.debugSwitch.toggle())
                    .setOnCancelListener(dialog -> activity.binding.debugSwitch.toggle());

            AlertDialog dialog = builder.show();

            TextView dialogMessage = dialog.findViewById(android.R.id.message);
            assert dialogMessage != null;
            dialogMessage.setTypeface(ResourcesCompat.getFont(activity, R.font.gs_text_regular));

            TextView dialogTitle = dialog.findViewById(R.id.alertTitle);
            assert dialogTitle != null;
            dialogTitle.setTypeface(ResourcesCompat.getFont(activity, R.font.ps_regular));
        }
        else {
            Log.v("DebugMode", "Debug mode was disabled; back on a production endpoint");
            configSharedPreferences.edit().putString("selectedEndpoint", "production").apply();
            activity.binding.clearSharedPreferencesLayout.setVisibility(View.GONE);
        }
    }

    public static void toggleBasedOnPreference(Context context, ActivityDebugPanelBinding binding) {
        final SharedPreferences configSharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String selectedEndpoint = configSharedPreferences.getString("selectedEndpoint", null);

        if (selectedEndpoint == null) {
            Log.v("DebugMode", "Debug mode has not been set (Shared Preferences entry is null); setting it to off by default");
            configSharedPreferences.edit().putString("selectedEndpoint", "production").apply();
        }
        else if (selectedEndpoint.equals("testing")) {
            Log.v("DebugMode", "Debug mode is currently on");
            binding.debugSwitch.toggle();
            binding.clearSharedPreferencesLayout.setVisibility(View.VISIBLE);
        }
        else Log.v("DebugMode", "Debug mode is currently off");
    }
}
