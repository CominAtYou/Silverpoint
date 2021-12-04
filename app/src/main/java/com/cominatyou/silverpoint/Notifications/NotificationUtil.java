package com.cominatyou.silverpoint.Notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cominatyou.silverpoint.R;

public class NotificationUtil {
    public static void send(String title, String description, String buttonText, String shortlink, Context context) {
        Intent linkIntent = new Intent(Intent.ACTION_VIEW);
        linkIntent.setData(Uri.parse(shortlink));
        PendingIntent linkPendingIntent = PendingIntent.getActivity(context, 0, linkIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "incidents")
                .setSmallIcon(69)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(description))
                .setSmallIcon(R.drawable.ic_dns)
                .addAction(R.drawable.ic_open_link, buttonText, linkPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationChannels.createActiveIncidentChannel(context);

        NotificationManagerCompat.from(context).notify(1, builder.build());
    }
    public static void send(String title, String description, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "incidents")
                .setSmallIcon(69)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_dns)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationChannels.createActiveIncidentChannel(context);

        NotificationManagerCompat.from(context).notify(1, builder.build());
    }
    public static void sendUpdateNotification(String title, String description, String buttonText, String shortlink, Context context) {
        Intent linkIntent = new Intent(Intent.ACTION_VIEW);
        linkIntent.setData(Uri.parse(shortlink));
        PendingIntent linkPendingIntent = PendingIntent.getActivity(context, 0, linkIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "updates")
                .setSmallIcon(69)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setSmallIcon(R.drawable.ic_dns)
                .addAction(R.drawable.ic_open_link, buttonText, linkPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationChannels.createAvailableUpdateChannel(context);

        NotificationManagerCompat.from(context).notify(2, builder.build());
    }
}
