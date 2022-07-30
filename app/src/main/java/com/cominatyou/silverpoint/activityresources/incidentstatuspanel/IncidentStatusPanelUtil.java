package com.cominatyou.silverpoint.activityresources.incidentstatuspanel;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.res.ResourcesCompat;

import com.cominatyou.silverpoint.IncidentStatusActivity;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityIncidentStatusBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class IncidentStatusPanelUtil {
    public static void update(IncidentStatusActivity activity) {
        activity.binding.incidentTitleValue.setText(ActiveIncidentUtil.getTitle(activity));

        activity.binding.incidentDescriptionLayout.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                .setMessage(ActiveIncidentUtil.getBody(activity))
                .setTitle("Latest update info")
                .setPositiveButton("Done", (dialog, _t) -> dialog.dismiss())
                .setOnCancelListener(DialogInterface::dismiss);
            AlertDialog dialog = builder.show();

            TextView dialogMessage = Objects.requireNonNull(dialog.findViewById(android.R.id.message));
            dialogMessage.setTypeface(ResourcesCompat.getFont(activity, R.font.gs_text_regular));

            TextView dialogTitle = Objects.requireNonNull(dialog.findViewById(R.id.alertTitle));
            dialogTitle.setTypeface(ResourcesCompat.getFont(activity, R.font.ps_regular));
        });

        final String lastUpdatedSharedPref = ActiveIncidentUtil.getLastUpdated(activity);
        final String lastUpdatedISODate = lastUpdatedSharedPref.equals("") ? "2016-12-08T13:20:10.000-08:00" : lastUpdatedSharedPref;
        long lastUpdatedDate = new DateTime(lastUpdatedISODate).getMillis();

        final SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateFormat(activity);
        final SimpleDateFormat timeFormat = (SimpleDateFormat) DateFormat.getTimeFormat(activity);
        activity.binding.lastUpdatedValue.setText(String.format("%s, %s", dateFormat.format(lastUpdatedDate), timeFormat.format(lastUpdatedDate)));

        activity.binding.viewOnStatuspageLayout.setOnClickListener(_v -> new CustomTabsIntent.Builder().build().launchUrl(activity, Uri.parse(ActiveIncidentUtil.getShortlink(activity))));
    }
}
