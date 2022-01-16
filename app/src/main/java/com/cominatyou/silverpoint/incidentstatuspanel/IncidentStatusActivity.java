package com.cominatyou.silverpoint.incidentstatuspanel;

import static com.cominatyou.silverpoint.incidentstatuspanel.IncidentStatusPanelUtil.update;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cominatyou.silverpoint.databinding.ActivityIncidentStatusBinding;
import com.cominatyou.silverpoint.util.ActiveIncidentUtil;
import com.cominatyou.silverpoint.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class IncidentStatusActivity extends AppCompatActivity {
    private ActivityIncidentStatusBinding binding;
    private Context activityContext;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ActiveIncidentUtil.inProgress(getApplicationContext())) {
                finish();
            }
            else update(context, binding, activityContext);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIncidentStatusBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);
        activityContext = this;
        Objects.requireNonNull(getSupportActionBar()).setTitle("Active incident");

        update(getApplicationContext(), binding, this);
        IntentFilter filter = new IntentFilter("INCIDENT_UPDATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }
}
