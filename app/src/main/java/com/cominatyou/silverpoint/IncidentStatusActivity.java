package com.cominatyou.silverpoint;

import com.cominatyou.silverpoint.activityresources.incidentstatuspanel.IncidentStatusPanelUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.TouchDelegate;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.databinding.ActivityIncidentStatusBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.Theme;
import com.google.android.material.color.DynamicColors;

public class IncidentStatusActivity extends AppCompatActivity {
    private ActivityIncidentStatusBinding binding;
    private Activity activityContext;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ActiveIncidentUtil.inProgress(getApplicationContext())) {
                finish();
            }
            else IncidentStatusPanelUtil.update(binding, activityContext);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);

        binding = ActivityIncidentStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Theme.set(this);

        activityContext = this;

        // Make the back button easier to hit
        final View parent = (View) binding.backButton.getParent();
        parent.post(() -> {
            final Rect rect = new Rect();
            binding.backButton.getHitRect(rect);
            rect.top -= 100;    // increase top hit area
            rect.left -= 100;   // increase left hit area
            rect.bottom += 50; // increase bottom hit area
            rect.right += 50;  // increase right hit area
            parent.setTouchDelegate(new TouchDelegate(rect, binding.backButton));
        });

        binding.backButton.setOnClickListener(_s -> finish());

        IncidentStatusPanelUtil.update(binding, this);
        IntentFilter filter = new IntentFilter("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

    }
}
