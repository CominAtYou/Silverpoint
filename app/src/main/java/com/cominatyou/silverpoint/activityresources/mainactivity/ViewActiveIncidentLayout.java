package com.cominatyou.silverpoint.activityresources.mainactivity;

import android.content.Context;
import android.content.Intent;

import com.cominatyou.silverpoint.IncidentStatusActivity;
import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityMainBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;

public class ViewActiveIncidentLayout {
    public static void setText(Context context, ActivityMainBinding binding) {
        if (ActiveIncidentUtil.inProgress(context)) {
            binding.viewActiveIncidentTitle.setTextColor(context.getColor(R.color.info_title));
            binding.viewActiveIncidentDescription.setText(ActiveIncidentUtil.getTitle(context));
        }
        else {
            binding.viewActiveIncidentTitle.setTextColor(context.getColor(R.color.text_disabled));
            binding.viewActiveIncidentDescription.setEnabled(false);
            binding.viewActiveIncidentDescription.setText(R.string.there_currently_isn_t_an_active_incident);
        }
    }
}
