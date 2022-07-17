package com.cominatyou.silverpoint.activityresources.mainactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cominatyou.silverpoint.DebugPanelActivity;
import com.cominatyou.silverpoint.SettingsActivity;
import com.cominatyou.silverpoint.databinding.SettingsLongClickBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SettingsLongPressBottomSheet extends BottomSheetDialogFragment {
    public static String TAG = "LongPressModalBottomSheet";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SettingsLongClickBottomSheetBinding binding = SettingsLongClickBottomSheetBinding.inflate(inflater);

        binding.settingsLayout.setOnClickListener(_v -> {
            dismiss();
            startActivity(new Intent(getContext(), SettingsActivity.class));
        });

        binding.debugLayout.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(getContext(), DebugPanelActivity.class));
        });

        return binding.getRoot();
    }
}
