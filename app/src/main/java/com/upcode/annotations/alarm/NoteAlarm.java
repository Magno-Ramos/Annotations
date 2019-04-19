package com.upcode.annotations.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.upcode.annotations.model.Note;
import com.upcode.annotations.receiver.AlarmReceiver;
import com.upcode.annotations.util.Html;

import java.text.DateFormat;
import java.util.Date;

public class NoteAlarm {

    private static final String TAG = "AppMain";

    public static void scheduleAlarm(Context context, Note note) {
        Log.d(TAG, "scheduleAlarm: " + DateFormat.getDateTimeInstance().format(new Date(note.getAlarm())));

        PendingIntent pendingIntent = buildPendingIntent(context, note);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, note.getAlarm(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, note.getAlarm(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, note.getAlarm(), pendingIntent);
        }
    }

    public static void cancelAlarm(Context context, Note note) {
        Log.d(TAG, "cancelAlarm: ");
        PendingIntent pendingIntent = buildPendingIntent(context, note);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private static PendingIntent buildPendingIntent(Context context, Note note) {

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("title", note.getTitle());
        intent.putExtra("message", Html.stripHtml(note.getMessage()));
        intent.putExtra("noteId", note.getId());

        return PendingIntent.getBroadcast(context, note.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
