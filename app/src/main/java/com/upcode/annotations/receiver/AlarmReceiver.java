package com.upcode.annotations.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.upcode.annotations.notifications.AppNotifications;
import com.upcode.annotations.service.UpdateNoteService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        int noteId = intent.getIntExtra("noteId", -1);

        AppNotifications.showAlarmNotification(context, title, message, noteId);

        Intent serviceUpdateNote = new Intent(context, UpdateNoteService.class);
        serviceUpdateNote.putExtra("noteId", noteId);

        context.startService(serviceUpdateNote);
    }
}
