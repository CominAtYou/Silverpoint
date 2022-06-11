package com.cominatyou.silverpoint.activityresources.debugpanel;

import android.content.Context;

import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.databinding.ActivityDebugPanelBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.google.android.material.snackbar.Snackbar;

public class ClearSharedPreferences {
    public static void onClick(Context context, ActivityDebugPanelBinding binding) {
        ActiveIncidentUtil.clear(context);
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Incident Shared Preferences have been cleared.", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundResource(R.drawable.tags_rounded_corners);
        snackbar.show();
    }
}
