package com.cominatyou.silverpoint.activityresources.incidentstatuspanel;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.res.ResourcesCompat;

import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityIncidentStatusBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.DateUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IncidentStatusPanelUtil {
    public static void update(ActivityIncidentStatusBinding binding, Context activityContext) {
        binding.incidentTitleValue.setText(ActiveIncidentUtil.getTitle(activityContext));
        binding.incidentDescriptionLayout.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activityContext)
                .setMessage(ActiveIncidentUtil.getBody(activityContext))
                .setTitle("Latest update info")
                .setPositiveButton("Done", (dialog, _t) -> dialog.dismiss())
                .setOnCancelListener(DialogInterface::dismiss);
            AlertDialog dialog = builder.show();

            TextView dialogMessage = dialog.findViewById(android.R.id.message);
            assert dialogMessage != null;
            dialogMessage.setTypeface(ResourcesCompat.getFont(activityContext, R.font.gs_text_regular));

            TextView dialogTitle = dialog.findViewById(R.id.alertTitle);
            assert dialogTitle != null;
            dialogTitle.setTypeface(ResourcesCompat.getFont(activityContext, R.font.ps_regular));
        });

        final String lastUpdatedSharedPref = ActiveIncidentUtil.getLastUpdated(activityContext);
        final String lastUpdatedISODate = lastUpdatedSharedPref.equals("") ? "2016-12-08T13:20:10.000-08:00" : lastUpdatedSharedPref;
        Date lastUpdatedDate = new Date();
        try {
            lastUpdatedDate = DateUtil.parseRFC3339Date(lastUpdatedISODate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateFormat(activityContext);
        final SimpleDateFormat timeFormat = (SimpleDateFormat) DateFormat.getTimeFormat(activityContext);
        binding.lastUpdatedValue.setText(String.format("%s, %s", dateFormat.format(lastUpdatedDate), timeFormat.format(lastUpdatedDate)));

        binding.viewOnStatuspageLayout.setOnClickListener(_v -> new CustomTabsIntent.Builder().build().launchUrl(activityContext, Uri.parse(ActiveIncidentUtil.getShortlink(activityContext))));
    }
}
