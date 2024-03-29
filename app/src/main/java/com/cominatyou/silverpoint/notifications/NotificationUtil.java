package com.cominatyou.silverpoint.notifications;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cominatyou.silverpoint.R;
import com.cominatyou.silverpoint.IncidentStatusActivity;

public class NotificationUtil {
    public static void send(String title, String description, String buttonText, String shortlink, String channelId, Context context) {
        if (System.currentTimeMillis() - context.getSharedPreferences("config", Context.MODE_PRIVATE).getLong("notificationsnooze", 0L) < 0)
            return;
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return;

        Intent linkIntent = new Intent(Intent.ACTION_VIEW);
        linkIntent.setData(Uri.parse(shortlink));
        PendingIntent linkPendingIntent = PendingIntent.getActivity(context, 0, linkIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent activityIntent = new Intent(context, IncidentStatusActivity.class);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(69)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setSmallIcon(R.drawable.ic_dns)
                .setContentIntent(activityPendingIntent)
                .addAction(R.drawable.ic_open_link, buttonText, linkPendingIntent)
                .setColor(context.getColor(R.color.silverpoint_light))
                .setAutoCancel(true);

        NotificationChannels.createActiveIncidentChannels(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(1, builder.build());
        }
    }
    public static void sendWithoutTapAction(String title, String description, String buttonText, String shortlink, String channelId, Context context) {
        if (System.currentTimeMillis() - context.getSharedPreferences("config", Context.MODE_PRIVATE).getLong("notificationsnooze", 0L) < 0) return;
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return;

        Intent linkIntent = new Intent(Intent.ACTION_VIEW);
        linkIntent.setData(Uri.parse(shortlink));
        PendingIntent linkPendingIntent = PendingIntent.getActivity(context, 0, linkIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(69)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setSmallIcon(R.drawable.ic_dns)
                .addAction(R.drawable.ic_open_link, buttonText, linkPendingIntent)
                .setColor(context.getColor(R.color.silverpoint_light));

        NotificationChannels.createActiveIncidentChannels(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(1, builder.build());
        }
    }
    public static void sendUpdateNotification(String title, String description, String buttonText, String shortlink, Context context) {
        if (System.currentTimeMillis() - context.getSharedPreferences("config", Context.MODE_PRIVATE).getLong("notificationsnooze", 0L) < 0) return;
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return;

        Intent linkIntent = new Intent(Intent.ACTION_VIEW);
        linkIntent.setData(Uri.parse(shortlink));
        PendingIntent linkPendingIntent = PendingIntent.getActivity(context, 0, linkIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationChannels.Miscellaneous.UPDATE_CHANNEL)
                .setSmallIcon(69)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setSmallIcon(R.drawable.ic_dns)
                .addAction(R.drawable.ic_open_link, buttonText, linkPendingIntent)
                .setColor(context.getColor(R.color.silverpoint_light));

        NotificationChannels.createAvailableUpdateChannel(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify(2, builder.build());
        }
    }
}
