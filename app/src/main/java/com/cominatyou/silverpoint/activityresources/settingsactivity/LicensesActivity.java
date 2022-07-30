package com.cominatyou.silverpoint.activityresources.settingsactivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.WindowCompat;

import com.cominatyou.silverpoint.databinding.ActivityLicensesBinding;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.color.DynamicColors;

public class LicensesActivity extends AppCompatActivity {
    private ActivityLicensesBinding binding;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        binding = ActivityLicensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        binding.topBar.setNavigationOnClickListener(_s -> finish());
        binding.frontendLayout.setOnClickListener(_s -> new CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse("https://www.apache.org/licenses/LICENSE-2.0.txt")));
        binding.behindTheScenesLayout.setOnClickListener(_s -> startActivity(new Intent(this, OssLicensesMenuActivity.class)));
    }
}
