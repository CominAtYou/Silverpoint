package com.cominatyou.silverpoint.notifications.snoozing;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import com.cominatyou.silverpoint.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

public class SnoozeNotificationsLayoutClick {
    public static void onClick(FragmentManager supportFragmentManager, Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        final long currentValue = sharedPreferences.getLong("notificationsnooze", 0L);

        if (System.currentTimeMillis() - currentValue > 0) {
            new BottomSheet().show(supportFragmentManager, BottomSheet.TAG);
            return;
        }

        final java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(context);
        String formattedDate = currentValue == Long.MAX_VALUE ? "" : " until " + dateFormat.format(new Date(currentValue));

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setMessage("Notifications are currently snoozed" + formattedDate + ". Do you want to turn them back on?")
                .setTitle("Re-enable notifications?")
                .setPositiveButton("Yes", (dialog, which) -> sharedPreferences.edit().remove("notificationsnooze").apply())
                .setNegativeButton("No", ((dialog, which) -> {}));
        AlertDialog dialog = builder.show();

        TextView dialogMessage = dialog.findViewById(android.R.id.message);
        assert dialogMessage != null;
        dialogMessage.setTypeface(ResourcesCompat.getFont(context, R.font.gs_text_regular));

        TextView dialogTitle = dialog.findViewById(R.id.alertTitle);
        assert dialogTitle != null;
        dialogTitle.setTypeface(ResourcesCompat.getFont(context, R.font.ps_regular));
    }
}
