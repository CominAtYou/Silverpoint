package com.cominatyou.silverpoint.updates;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.notifications.NotificationChannels;
import com.cominatyou.silverpoint.util.OKHttpClientInstance;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ThreadLocalRandom;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateDownloadWorker extends Worker {
    private void installUpdate(File updateFile) {
        Log.v("UpdateDownloadWorker", "Installing update: " + updateFile.getAbsolutePath());
        final Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), "com.cominatyou.silverpoint.CacheFileProvider", updateFile), "application/vnd.android.package-archive");
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        getApplicationContext().startActivity(installIntent);
        Log.v("UpdateDownloadWorker", "Called PackageManager");
    }

    public UpdateDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        setForegroundAsync(createForegroundInfo());

        final Request request = new Request.Builder()
                .url("https://cdn.cominatyou.com/silverpoint/releases/latest")
                .method("GET", null)
                .build();

        final Call call = OKHttpClientInstance.getInstance().newCall(request);

        try (final Response response = call.execute()) {
            if (response.isSuccessful()) {
                final File directory = new File(getApplicationContext().getCacheDir() + "/downloads/");
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File updateFile = new File(getApplicationContext().getCacheDir() + "/downloads/", "update.apk");
                IOUtils.copy(response.body().byteStream(), new FileOutputStream(updateFile));
                installUpdate(updateFile);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }

        return Result.success();
    }

    private ForegroundInfo createForegroundInfo() {
        final int notificationId = ThreadLocalRandom.current().nextInt(1, 65535 + 1);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), NotificationChannels.Miscellaneous.UPDATE_DOWNLOADS_CHANNEL)
                .setContentTitle(getApplicationContext().getString(R.string.update_downloads_notification_title))
                .setContentText(getApplicationContext().getString(R.string.update_downloads_notification_description, getInputData().getString("version")))
                .setSmallIcon(R.drawable.ic_file_download_24)
                .setProgress(100, 0, true)
                .setOngoing(true);

        return new ForegroundInfo(notificationId, notification.build());
    }
}
