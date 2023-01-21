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
        context.getSystemService(NotificationManager.class).createNotificationChannelGroups(
                Arrays.asList(new NotificationChannelGroup("activeincidents", "Incidents"), new NotificationChannelGroup("miscellaneous", "Miscellaneous")));
        final NotificationChannel[] notificationChannels = {
                new NotificationChannel(ActiveIncidents.CHANNEL_NEW_INCIDENT, "New Incidents", NotificationManager.IMPORTANCE_HIGH),
                new NotificationChannel(ActiveIncidents.CHANNEL_INCIDENT_UPDATES, "Incident Updates", NotificationManager.IMPORTANCE_HIGH)
        };

        final String[] notificationChannelDescriptions = { "Notifications for new incidents.", "Notifications regarding updates for existing incidents." };


        for (int i = 0; i < notificationChannels.length; i++) {
            notificationChannels[i].setDescription(notificationChannelDescriptions[i]);
            notificationChannels[i].setGroup("activeincidents");
            notificationChannels[i].setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound), new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());
            context.getSystemService(NotificationManager.class).createNotificationChannel(notificationChannels[i]);
        }

    }
    public static void createAvailableUpdateChannel(Context context) {
        CharSequence name = "Available Updates";
        String channelDescription = "Notifications for when required updates are available.";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(Miscellaneous.UPDATE_CHANNEL, name, importance);
        channel.setDescription(channelDescription);
        channel.setGroup("miscellaneous");
        channel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound), new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }
}
