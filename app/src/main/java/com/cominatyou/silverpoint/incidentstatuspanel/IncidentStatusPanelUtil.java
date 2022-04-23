package com.cominatyou.silverpoint.incidentstatuspanel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
    protected static void update(Context context, ActivityIncidentStatusBinding binding, Context activityContext) {
        binding.incidentTitleValue.setText(ActiveIncidentUtil.getTitle(context));
        binding.incidentDescriptionLayout.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activityContext)
                .setMessage(ActiveIncidentUtil.getBody(context))
                .setTitle("Latest update info")
                .setPositiveButton("Done", (dialog, _t) -> dialog.dismiss())
                .setOnCancelListener(DialogInterface::dismiss);
            AlertDialog dialog = builder.show();

            TextView dialogMessage = dialog.findViewById(android.R.id.message);
            assert dialogMessage != null;
            dialogMessage.setTypeface(ResourcesCompat.getFont(context, R.font.gs_text_regular));

            TextView dialogTitle = dialog.findViewById(R.id.alertTitle);
            assert dialogTitle != null;
            dialogTitle.setTypeface(ResourcesCompat.getFont(context, R.font.ps_regular));
        });

        final String lastUpdatedSharedPref = ActiveIncidentUtil.getLastUpdated(context);
        final String lastUpdatedISODate = lastUpdatedSharedPref.equals("") ? "2016-12-08T13:20:10.000-08:00" : lastUpdatedSharedPref;
        Date lastUpdatedDate = new Date();
        try {
            lastUpdatedDate = DateUtil.parseRFC3339Date(lastUpdatedISODate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateFormat(context);
        final SimpleDateFormat timeFormat = (SimpleDateFormat) DateFormat.getTimeFormat(context);
        binding.lastUpdatedValue.setText(String.format("%s, %s", dateFormat.format(lastUpdatedDate), timeFormat.format(lastUpdatedDate)));

        binding.viewOnStatuspageLayout.setOnClickListener(_v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(ActiveIncidentUtil.getShortlink(context)));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }
}
