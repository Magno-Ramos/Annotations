package com.upcode.annotations.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;
import com.upcode.annotations.Callback;
import com.upcode.annotations.R;
import com.upcode.annotations.alarm.NoteAlarm;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.repository.NoteRepository;

import java.util.Calendar;
import java.util.List;

import androidx.lifecycle.LiveData;

public class NoteController {

    private Context context;
    private NoteRepository noteRepository;

    public static NoteController getInstance(Context context, NoteRepository noteRepository) {
        return new NoteController(context, noteRepository);
    }

    private NoteController(Context context, NoteRepository noteRepository) {
        this.context = context;
        this.noteRepository = noteRepository;
    }

    public NoteRepository getNoteRepository() {
        return noteRepository;
    }

    public void attemptCreateNote(Callback<Note> callback) {
        new MaterialDialog.Builder(context)
                .iconRes(R.drawable.ic_file_primary)
                .title(context.getString(R.string.new_note))
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Note.MIN_LENGTH, Note.MAX_LENGTH)
                .input(R.string.hint_title_of_note, R.string.empty_text, (dialog, input) -> {
                    Note note = new Note();
                    note.setTitle(input.toString().trim());

                    noteRepository.insert(note, callback);
                })
                .show();
    }

    public void attemptCreateNote(int folderId, Callback<Note> callback) {
        new MaterialDialog.Builder(context)
                .iconRes(R.drawable.ic_file_primary)
                .title(context.getString(R.string.new_note))
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Note.MIN_LENGTH, Note.MAX_LENGTH)
                .input(R.string.hint_title_of_note, R.string.empty_text, (dialog, input) -> {
                    Note note = new Note();
                    note.setFolderId(folderId);
                    note.setTitle(input.toString().trim());

                    noteRepository.insert(note, callback);
                })
                .show();
    }

    public void attemptDeleteNote(Note note) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.delete)
                .content(R.string.question_delete_note)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive((dialog1, which) -> noteRepository.delete(note)).build();
        dialog.show();
    }

    public void attemptDeleteNote(Note note, Callback<Boolean> callback) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.delete)
                .content(R.string.question_delete_note)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive((dialog1, which) -> {
                    noteRepository.delete(note);
                    callback.onResult(true);
                }).build();
        dialog.show();
    }

    public void attemptRenameNote(Note note) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(context.getString(R.string.rename))
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Note.MIN_LENGTH, Note.MAX_LENGTH)
                .input(context.getString(R.string.hint_title_of_note), note.getTitle(), (dialog, input) -> {

                    String newTitle = input.toString().trim();
                    noteRepository.renameNote(note, newTitle);
                }).build();
        materialDialog.show();
    }

    public void attemptSetAlarm(Callback<Long> callback) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, y, m, dayOfMonth) -> {
            // day
            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (viewTime, hour, min) -> {

                //time
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);

                callback.onResult(calendar.getTimeInMillis());

            }, hourOfDay, minute, true);

            timePickerDialog.show();

        }, year, month, day);
        datePickerDialog.show();
    }

    public void updateContent(Note note, String content) {
        noteRepository.updateContent(note, content);
    }

    public void removeAlarm(Note note) {
        noteRepository.removeAlarm(note);

        // cancel alarm
        NoteAlarm.cancelAlarm(context, note);
    }

    public void setAlarm(Note note, long datetime) {
        Note noteUpdated = noteRepository.setAlarm(note, datetime);

        // set alarm
        NoteAlarm.scheduleAlarm(context, noteUpdated);
    }

    public LiveData<List<Note>> findAllNotesByFolderId(int id) {
        return noteRepository.findAllNotesByFolderId(id);
    }

    public LiveData<List<Note>> findAllNotesOutsideFolder() {
        return noteRepository.findAllNotesOutsideFolder();
    }

    public void findAllNotesWithValidAlarms(Callback<List<Note>> callback) {
        noteRepository.findAllNotesByAlarmHasAfter(System.currentTimeMillis(), callback);
    }

    public void findAllNotesWithInvalidAlarms(Callback<List<Note>> listCallback) {
        noteRepository.findAllNotesWithInvalidAlarms(System.currentTimeMillis(), listCallback);
    }

    public void removeAlarmById(int noteId) {
        noteRepository.removeAlarmById(noteId);
    }
}
