package com.cominatyou.silverpoint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SharedPrefsDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final SharedPreferences sharedPreferences = requireContext().getSharedPreferences("incidentData", Context.MODE_PRIVATE);
        return new AlertDialog.Builder(requireContext())
                .setMessage("Latest Incident ID: " + (sharedPreferences.getString("latestIncidentID", "No Value").equals("") ? "Empty String" : sharedPreferences.getString("latestIncidentID", "No Value"))
                        + "\nLatest Incident Update ID: " + (sharedPreferences.getString("latestIncidentUpdateID", "No Value").equals("") ? "Empty String" : sharedPreferences.getString("latestIncidentUpdateID", "No Value"))
                        + String.format("\nAPIResponse.status: %s", APIResponse.getStatus() == null ? "Null" : "JSONObject"))
                .setPositiveButton("Done", (dialog, which) -> {} )
                .create();
    }
    public static String TAG = "SharedPrefQueryDialog";
}
