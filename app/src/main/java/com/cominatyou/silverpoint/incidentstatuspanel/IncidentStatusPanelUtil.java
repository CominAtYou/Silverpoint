package com.cominatyou.silverpoint.incidentstatuspanel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;

import com.cominatyou.silverpoint.databinding.ActivityIncidentStatusBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IncidentStatusPanelUtil {
    protected static void update(Context context, ActivityIncidentStatusBinding binding, Context activityContext) {
        binding.incidentTitleValue.setText(ActiveIncidentUtil.getTitle(context));
        binding.incidentDescriptionLayout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
            builder.setMessage(ActiveIncidentUtil.getBody(context))
                    .setTitle("Latest update info");
            builder.setPositiveButton("Done", (dialog, _t) -> dialog.dismiss());
            builder.setOnCancelListener(DialogInterface::dismiss);
            builder.show();
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
