package com.cominatyou.silverpoint.notifications;

public class ActiveIncidentNotificationChannels {
    /**
     * The notification channel for new incidents (the initial notification for the incident)
     */
    public final String CHANNEL_NEW_INCIDENT = "NEW_INCIDENT";
    /**
     * The notification channel for incident updates
     */
    public final String CHANNEL_INCIDENT_UPDATES = "INCIDENT_UPDATES";

    protected ActiveIncidentNotificationChannels() {}
}
