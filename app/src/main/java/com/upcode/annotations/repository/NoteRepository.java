package com.upcode.annotations.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.upcode.annotations.Callback;
import com.upcode.annotations.alarm.NoteAlarm;
import com.upcode.annotations.dao.NoteDao;
import com.upcode.annotations.database.AppDatabase;
import com.upcode.annotations.model.Note;

import java.util.List;

import androidx.lifecycle.LiveData;

public class NoteRepository {

    private static final String TAG = "NoteRepository";

    private NoteDao noteDao;
    private Context context;

    public static NoteRepository from(Context context) {
        return new NoteRepository(context);
    }

    private NoteRepository(Context context) {
        this.context = context;
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        noteDao = appDatabase.noteDao();
    }

    public LiveData<List<Note>> findAllNotesByFolderId(int id) {
        Log.d(TAG, "findAllNotesByFolderId: ");
        UpdateNotesWithObsoleteAlarms.start(noteDao);
        return noteDao.findAllByFolderId(id);
    }

    /*public LiveData<List<Note>> findRecentFiles() {
        Log.d(TAG, "findRecentFiles: ");
        UpdateNotesWithObsoleteAlarms.start(noteDao);
        return noteDao.findRecentNotesChanged();
    }*/

    public LiveData<List<Note>> findAllNotesOutsideFolder() {
        Log.d(TAG, "findAllNotesOutsideFolder: ");
        UpdateNotesWithObsoleteAlarms.start(noteDao);
        return noteDao.findAllNotesOutsideFolder();
    }

    public void findAllNotesByAlarmHasAfter(long time, Callback<List<Note>> callback) {
        FindNoteTask.findAllNotesByAlarmHasAfter(time, noteDao, callback);
    }

    public void insert(Note note, Callback<Note> noteCallback) {
        new InsertNoteTask(noteDao).execute(note);
        Log.d(TAG, "insert: " + note.toString());
    }

    private void update(Note note) {
        note.setLastModification(System.currentTimeMillis());
        new UpdateNoteTask(noteDao).execute(note);

        Log.d(TAG, "update: " + note.toString());
    }

    public void delete(Note note) {
        // remove alarm if exists
        if (note.alarmIsEnabled()) {
            NoteAlarm.cancelAlarm(context, note);
        }

        new DeleteNoteTask(noteDao).execute(note);

        Log.d(TAG, "delete: " + note.toString());
    }

    public void renameNote(Note note, String newTitle) {
        Note noteToUpdate = note.doClone();
        noteToUpdate.setTitle(newTitle);

        update(noteToUpdate);
        Log.d(TAG, "renameNote: ");
    }

    public void removeAlarm(Note note) {
        Note noteToUpdate = note.doClone();
        noteToUpdate.setAlarm(-1);

        update(noteToUpdate);
        Log.d(TAG, "removeAlarm: ");
    }

    public Note setAlarm(Note note, long datetime) {
        Note noteUpdated = note.doClone();
        noteUpdated.setAlarm(datetime);

        update(noteUpdated);
        Log.d(TAG, "setAlarm: ");
        return noteUpdated;
    }

    public void updateContent(Note note, String content) {
        Note noteUpdated = note.doClone();
        noteUpdated.setMessage(content);

        update(noteUpdated);
        Log.d(TAG, "updateContent: ");
    }

    public void removeAlarmById(int noteId) {
        findNoteById(noteId, new Callback<Note>() {
            @Override
            public void onResult(Note note) {
                note.setAlarm(-1);
                update(note);
            }
        });

        Log.d(TAG, "removeAlarmById: ");
    }

    private void findNoteById(int note, Callback<Note> noteCallback) {
        FindNoteTask.findNoteById(note, noteDao, noteCallback);
    }

    public void findAllNotesWithInvalidAlarms(long time, Callback<List<Note>> listCallback) {
        FindNoteTask.findAllNotesByAlarmHasBefore(time, noteDao, listCallback);
    }

    private static class UpdateNotesWithObsoleteAlarms extends AsyncTask<Void, Void, Void> {

        private NoteDao noteDao;
        private Callback<Void> callback;

        static void start(NoteDao noteDao) {
            new UpdateNotesWithObsoleteAlarms(noteDao, null).execute();
        }

        private UpdateNotesWithObsoleteAlarms(NoteDao noteDao, Callback<Void> callback) {
            this.noteDao = noteDao;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (Note note : noteDao.findNotesByAlarmEnableAndHasObsolete(System.currentTimeMillis())) {
                note.setAlarm(-1);
                noteDao.update(note);

                Log.d(TAG, "UpdateNotesWithObsoleteAlarms: " + note.toString());
            }

            if (callback != null) {
                callback.onResult(null);
            }
            return null;
        }
    }

    private static class InsertNoteTask extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        private InsertNoteTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteTask extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        private UpdateNoteTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class FindNoteTask extends AsyncTask<Integer, Void, Void> {

        private NoteDao noteDao;
        private Object variable;

        private Callback<List<Note>> listCallback;
        private Callback<Note> callback;
        private FindType findType;

        static void findNoteById(int noteId, NoteDao noteDao, Callback<Note> callback) {
            FindNoteTask findNoteTask = new FindNoteTask(noteDao, null, callback);
            findNoteTask.findType = FindType.BY_ID;
            findNoteTask.execute(noteId);
        }

        static void findNoteLastInserted(NoteDao noteDao, Callback<Note> callback) {
            FindNoteTask findNoteTask = new FindNoteTask(noteDao, null, callback);
            findNoteTask.findType = FindType.BY_LAST_INSERTED;
            findNoteTask.execute();
        }

        static void findAllNotesByAlarmHasAfter(long time, NoteDao noteDao, Callback<List<Note>> callback) {
            FindNoteTask findNoteTask = new FindNoteTask(noteDao, callback, null);
            findNoteTask.variable = time;
            findNoteTask.findType = FindType.BY_VALID_ALARM;
            findNoteTask.execute();
        }

        static void findAllNotesByAlarmHasBefore(long time, NoteDao noteDao, Callback<List<Note>> callback) {
            FindNoteTask findNoteTask = new FindNoteTask(noteDao, callback, null);
            findNoteTask.variable = time;
            findNoteTask.findType = FindType.BY_INVALID_ALARM;
            findNoteTask.execute();
        }

        private FindNoteTask(NoteDao noteDao, Callback<List<Note>> listCallback, Callback<Note> callback) {
            this.noteDao = noteDao;
            this.listCallback = listCallback;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            Note note;
            switch (findType) {
                case BY_ID:
                    int id = integers[0];
                    note = noteDao.findNoteById(id);
                    callback.onResult(note);
                    break;

                case BY_LAST_INSERTED:
                    note = noteDao.findLastNoteInserted();
                    callback.onResult(note);
                    break;

                case BY_VALID_ALARM:
                    List<Note> notes = noteDao.findAllNotesByAlarmHasAfter((Long) variable);
                    listCallback.onResult(notes);
                    break;

                case BY_INVALID_ALARM:
                    listCallback.onResult(noteDao.findNotesByAlarmEnableAndHasObsolete((Long) variable));
                    break;
            }

            return null;
        }

        private enum FindType {
            BY_ID,
            BY_LAST_INSERTED,
            BY_VALID_ALARM,
            BY_INVALID_ALARM
        }
    }

    private static class DeleteNoteTask extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        private DeleteNoteTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            Note note = notes[0];
            noteDao.delete(note);
            return null;
        }
    }
}
