package com.cominatyou.silverpoint.notifications;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;

import com.cominatyou.silverpoint.R;

import java.util.Arrays;

public class NotificationChannels {
    public static class Miscellaneous {
        public static final String UPDATE_CHANNEL = "UPDATES";
    }

    public static class ActiveIncidents {
        /**
         * The notification channel for new incidents (the initial notification for the incident)
         */
        public static final String CHANNEL_NEW_INCIDENT = "NEW_INCIDENT";
        /**
         * The notification channel for incident updates
         */
        public static final String CHANNEL_INCIDENT_UPDATES = "INCIDENT_UPDATES";
    }

    public static void createActiveIncidentChannels(Context context) {
        context.getSystemService(NotificationManager.class).createNotificationChannelGroup(new NotificationChannelGroup("activeincidents", context.getString(R.string.incidents_notification_channel_group_name)));
        final NotificationChannel[] notificationChannels = {
                new NotificationChannel(ActiveIncidents.CHANNEL_NEW_INCIDENT, context.getString(R.string.new_incident_notification_channel_name), NotificationManager.IMPORTANCE_HIGH),
                new NotificationChannel(ActiveIncidents.CHANNEL_INCIDENT_UPDATES, context.getString(R.string.incident_updates_notification_channel_name), NotificationManager.IMPORTANCE_HIGH)
        };

        final Integer[] notificationChannelDescriptions = { R.string.new_incident_notification_channel_description, R.string.incident_updates_notification_channel_description };

        for (int i = 0; i < notificationChannels.length; i++) {
            notificationChannels[i].setDescription(context.getString(notificationChannelDescriptions[i]));
            notificationChannels[i].setGroup("activeincidents");
            notificationChannels[i].setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound), new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());
        }

        context.getSystemService(NotificationManager.class).createNotificationChannels(Arrays.asList(notificationChannels));
    }
    public static void createAvailableUpdateChannel(Context context) {
        String name = context.getString(R.string.available_updates_notification_channel_name);
        String channelDescription = context.getString(R.string.available_updates_notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        context.getSystemService(NotificationManager.class).createNotificationChannelGroup(new NotificationChannelGroup("miscellaneous", context.getString(R.string.miscellaneous_notification_channel_group_name)));

        NotificationChannel channel = new NotificationChannel(Miscellaneous.UPDATE_CHANNEL, name, importance);
        channel.setDescription(channelDescription);
        channel.setGroup("miscellaneous");
        channel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound), new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());

        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }
}
