package com.cominatyou.silverpoint;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cominatyou.silverpoint.databinding.SnoozeNotificationsLongClickBottomSheetBinding;
import com.cominatyou.silverpoint.notifications.snoozing.SnoozeNotificationsLayoutClick;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SnoozeNotificationsLongPressBottomSheet extends BottomSheetDialogFragment {
    public static String TAG = "LongPressModalBottomSheet";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Seriously how long can a class name get?
        SnoozeNotificationsLongClickBottomSheetBinding binding = SnoozeNotificationsLongClickBottomSheetBinding.inflate(inflater);

        binding.snoozeNotificationsLayout.setOnClickListener(_v -> {
            dismiss();
            SnoozeNotificationsLayoutClick.onClick(getParentFragmentManager(), requireContext());
        });

        binding.debugLayout.setOnClickListener(v -> {
            dismiss();
            startActivity(new Intent(getContext(), DebugPanelActivity.class));
        });

        return binding.getRoot();
    }
}
