package com.cominatyou.silverpoint.notifications.snoozing;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

public class SnoozeHandler {
    private static final Map<SnoozeDuration, Long> milliseconds = Map.of(
        SnoozeDuration.THREE_DAYS, 259200000L,
        SnoozeDuration.WEEK, 604800000L,
        SnoozeDuration.MONTH, 30L * 86400 * 1000, // close enough, this is like 30 days
        SnoozeDuration.INDEFINITE, Long.MAX_VALUE
    );

    public static void set(SnoozeDuration duration, Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        if (duration == SnoozeDuration.TOMORROW) {
            final ZonedDateTime now = ZonedDateTime.now();
            final long tomorrow = now.toLocalDate().atStartOfDay(now.getZone()).plusDays(1).toInstant().toEpochMilli();

            sharedPreferences.edit().putLong("notificationsnooze", tomorrow).apply();
            return;
        }

        if (duration == SnoozeDuration.INDEFINITE) {
            sharedPreferences.edit().putLong("notificationsnooze", Objects.requireNonNull(milliseconds.get(duration))).apply();
            return;
        }

        sharedPreferences.edit().putLong("notificationsnooze", System.currentTimeMillis() + Objects.requireNonNull(milliseconds.get(duration))).apply();
    }
}
