package com.cominatyou.silverpoint.activityresources.settingsactivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.WindowCompat;

import com.cominatyou.silverpoint.BuildConfig;
import com.cominatyou.silverpoint.databinding.ActivityAboutBinding;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.color.DynamicColors;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DynamicColors.applyToActivityIfAvailable(this);
        final ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(binding.getRoot());

        binding.topBar.setNavigationOnClickListener(_s -> finish());

        binding.versionDescription.setText(String.format(Locale.getDefault(), "%s (%d)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        binding.commitDescription.setText(BuildConfig.COMMIT_HASH);

        CustomTabsIntent intent = new CustomTabsIntent.Builder().setShowTitle(true).build();
        binding.sourceLayout.setOnClickListener(_s -> intent.launchUrl(this, Uri.parse("https://github.com/CominAtYou/Silverpoint")));

        binding.licenseLayout.setOnClickListener(_s -> startActivity(new Intent(this, LicensesActivity.class)));
    }
}
