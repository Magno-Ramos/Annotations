package com.upcode.annotations.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.upcode.annotations.controller.NoteController;
import com.upcode.annotations.repository.NoteRepository;

import androidx.annotation.Nullable;

public class UpdateNoteService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AppMain", "UpdateNoteService onStartCommand");

        int noteId = intent.getIntExtra("noteId", -1);
        if (noteId > 0) {
            NoteController noteController = NoteController.getInstance(this, NoteRepository.from(this));
            noteController.removeAlarmById(noteId);
        }

        return Service.START_NOT_STICKY;
    }
}
