package com.cominatyou.silverpoint.updates;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

public class UpdateDownloadKickoff {

    public static void downloadUpdate(Context context, String version) {
        Log.v("UpdateDownloadKickoff", "Downloading " + version);

        final WorkRequest workRequest = new OneTimeWorkRequest.Builder(UpdateDownloadWorker.class)
                .setInputData(
                        new Data.Builder()
                                .putString("version", version)
                                .build()
                )
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }
}
