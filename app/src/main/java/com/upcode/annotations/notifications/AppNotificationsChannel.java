package com.upcode.annotations.notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;


public class AppNotificationsChannel {

    static final String ALARM_CHANNEL_ID = "com.upcode.channels.alarm_ID";

    private NotificationManagerCompat notificationManagerCompat;

    public static AppNotificationsChannel from(Context context) {
        return new AppNotificationsChannel(context);
    }

    private AppNotificationsChannel(Context context) {
        this.notificationManagerCompat = NotificationManagerCompat.from(context);
    }

    public void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAlarmChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createAlarmChannel() {
        String channelName = "Alarm Notifications";
        String channelDescription = "Notification used to display alarm of note";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        // sound
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // audio attributes
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        NotificationChannel channel = new NotificationChannel(ALARM_CHANNEL_ID, channelName, importance);
        channel.setSound(sound, audioAttributes);
        channel.setDescription(channelDescription);

        notificationManagerCompat.createNotificationChannel(channel);
    }
}
