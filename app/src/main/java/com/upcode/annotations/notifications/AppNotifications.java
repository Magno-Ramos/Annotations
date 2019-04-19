package com.upcode.annotations.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.upcode.annotations.R;
import com.upcode.annotations.activities.MainActivity;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AppNotifications {


    public static void showAlarmNotification(Context context, String title, String message, int noteId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("noteId", noteId);

        boolean messageIsEmpty = message.trim().isEmpty();

        String contentTitle = context.getString(R.string.reminder) + (messageIsEmpty ? "" : " - " + title);
        String contentText = messageIsEmpty ? title : message;

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppNotificationsChannel.ALARM_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_note_notification);
        builder.setContentTitle(contentTitle);
        builder.setContentText(contentText);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        builder.setCategory(NotificationCompat.CATEGORY_ALARM);
        builder.setContentIntent(PendingIntent.getActivity(context, noteId, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(contentTitle).bigText(contentText));
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setSound(alarmSound);
        builder.setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(noteId, builder.build());
    }
}
