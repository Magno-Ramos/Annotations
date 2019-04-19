package com.upcode.annotations.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.upcode.annotations.Callback;
import com.upcode.annotations.alarm.NoteAlarm;
import com.upcode.annotations.controller.NoteController;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.repository.NoteRepository;

import java.util.List;

import androidx.annotation.Nullable;

public class CheckAlarmService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AppMain", "onStartCommand: CheckAlarmService");


        // re-set alarm has active
        NoteController noteController =  NoteController.getInstance(this, NoteRepository.from(this));
        noteController.findAllNotesWithValidAlarms(new Callback<List<Note>>() {
                    @Override
                    public void onResult(List<Note> notes) {
                        for (Note note : notes) {
                            NoteAlarm.scheduleAlarm(CheckAlarmService.this, note);
                            Log.d("AppMain", "alarm added from " + note.getTitle());
                        }
                    }
                });

        // remove alarms others alarms
        noteController.findAllNotesWithInvalidAlarms(new Callback<List<Note>>() {
            @Override
            public void onResult(List<Note> notes) {
                for (Note note : notes) {
                    NoteAlarm.cancelAlarm(CheckAlarmService.this, note);
                    Log.d("AppMain", "alarm removed from " + note.getTitle());
                }
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }
}
