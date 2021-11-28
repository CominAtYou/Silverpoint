package com.cominatyou.silverpoint;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationUtil {
    protected static void send(String title, String description, String shortlink, Context context) {
        Intent linkIntent = new Intent(Intent.ACTION_VIEW);
        linkIntent.setData(Uri.parse(shortlink));
        PendingIntent linkPendingIntent = PendingIntent.getActivity(context, 0, linkIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "incidents")
                .setSmallIcon(69)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(description))
//                .setContentIntent(linkPendingIntent)
                .setSmallIcon(R.drawable.ic_dns)
                .addAction(R.drawable.ic_open_link, "View Status", linkPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        CharSequence name = "Active Incidents";
        String channelDescription = "Active incidents are posted through this channel.";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("incidents", name, importance);
        channel.setDescription(channelDescription);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);

        NotificationManagerCompat.from(context).notify(1, builder.build());
    }
    protected static void send(String title, String description, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "incidents")
                .setSmallIcon(69)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_dns)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(description))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        CharSequence name = "Active Incidents";
        String channelDescription = "Active incidents are posted through this channel.";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("incidents", name, importance);
        channel.setDescription(channelDescription);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);

        NotificationManagerCompat.from(context).notify(1, builder.build());
    }
}
