package com.cominatyou.silverpoint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.activityresources.incidentstatuspanel.IncidentStatusPanelUtil;
import com.cominatyou.silverpoint.databinding.ActivityIncidentStatusBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.Theme;
import com.google.android.material.color.DynamicColors;

public class IncidentStatusActivity extends AppCompatActivity {
    public ActivityIncidentStatusBinding binding;
    private final IncidentStatusActivity thisActivity = this; // for the BroadcastReceiver

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ActiveIncidentUtil.inProgress(getApplicationContext())) {
                finish();
            }
            else IncidentStatusPanelUtil.update(thisActivity);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);

        binding = ActivityIncidentStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.topBar.setNavigationOnClickListener(_s -> finish());
        Theme.set(this);

        IncidentStatusPanelUtil.update(this);
        IntentFilter filter = new IntentFilter("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

    }
}
