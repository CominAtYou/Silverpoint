package com.cominatyou.silverpoint.notifications.snoozing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cominatyou.silverpoint.databinding.SnoozeNotificationsBottomSheetDialogBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class SnoozeNotificationsBottomSheet extends BottomSheetDialogFragment {
    public static String TAG = "ModalBottomSheet";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SnoozeNotificationsBottomSheetDialogBinding binding = SnoozeNotificationsBottomSheetDialogBinding.inflate(inflater);

        binding.untilTomorrowLayout.setOnClickListener(_v -> {
            SnoozeHandler.set(SnoozeDuration.TOMORROW, requireContext());
            dismiss();
        });

        binding.threeDaysLayout.setOnClickListener(_v -> {
            SnoozeHandler.set(SnoozeDuration.THREE_DAYS, requireContext());
            dismiss();
        });

        binding.weekLayout.setOnClickListener(_v -> {
            SnoozeHandler.set(SnoozeDuration.WEEK, requireContext());
            dismiss();
        });

        binding.monthLayout.setOnClickListener(_v -> {
            SnoozeHandler.set(SnoozeDuration.MONTH, requireContext());
            dismiss();
        });

        binding.untilTurnedBackOnLayout.setOnClickListener(_v -> {
            SnoozeHandler.set(SnoozeDuration.INDEFINITE, requireContext());
            dismiss();
        });

        return binding.getRoot();
    }
}
