package com.cominatyou.silverpoint.activityresources.mainactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cominatyou.silverpoint.IncidentStatusActivity;
import com.cominatyou.silverpoint.MainActivity;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;

public class ViewActiveIncidentLayout {
    public static void setText(MainActivity activity) {
        if (ActiveIncidentUtil.inProgress(activity)) {
            activity.binding.viewActiveIncidentTitle.setTextColor(activity.getColor(R.color.info_title));
            activity.binding.viewActiveIncidentDescription.setText(ActiveIncidentUtil.getTitle(activity));
        }
        else {
            activity.binding.viewActiveIncidentTitle.setTextColor(activity.getColor(R.color.text_disabled));
            activity.binding.viewActiveIncidentDescription.setEnabled(false);
            activity.binding.viewActiveIncidentDescription.setText(R.string.there_currently_isn_t_an_active_incident);
        }
    }
}
