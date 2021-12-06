package com.cominatyou.silverpoint.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationChannels {
    public static void createActiveIncidentChannel(Context context) {
        CharSequence name = "Active Incidents";
        String channelDescription = "Active incidents are posted through this channel.";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("incidents", name, importance);
        channel.setDescription(channelDescription);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }
    public static void createAvailableUpdateChannel(Context context) {
        CharSequence name = "Available Updates";
        String channelDescription = "Notifications for when required updates are available.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("updates", name, importance);
        channel.setDescription(channelDescription);
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }
}
