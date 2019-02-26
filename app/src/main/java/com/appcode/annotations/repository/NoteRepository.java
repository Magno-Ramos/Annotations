package com.appcode.annotations.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.appcode.annotations.callback.OnCreateListener;
import com.appcode.annotations.dao.NoteDao;
import com.appcode.annotations.database.NoteDatabase;
import com.appcode.annotations.model.Note;

import java.util.List;

import androidx.lifecycle.LiveData;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    public NoteRepository(Application application, boolean findAll) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();

        if (findAll)
            allNotes = noteDao.findAll();
    }

    public LiveData<List<Note>> findAllNotesByFolderId(int id) {
        allNotes = noteDao.findAllByFolderId(id);
        return allNotes;
    }

    public void insert(Note note, OnCreateListener<Note> onCreateListener) {
        new InsertNoteTask(noteDao, onCreateListener).execute(note);
    }

    public void update(Note note) {
        note.setLastModification(System.currentTimeMillis());
        new UpdateNoteTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteTask(noteDao).execute(note);
    }

    public LiveData<List<Note>> findRecentFiles() {
        return noteDao.findRecentFilesChangeds();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    private static class InsertNoteTask extends AsyncTask<Note, Void, Void> {

        private OnCreateListener<Note> onCreateListener;
        private NoteDao noteDao;

        private InsertNoteTask(NoteDao noteDao, OnCreateListener<Note> onCreateListener) {
            this.noteDao = noteDao;
            this.onCreateListener = onCreateListener;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            long id = noteDao.insert(notes[0]);
            if (onCreateListener != null) {
                Note note = noteDao.findById(id);
                onCreateListener.onCreate(note);
            }
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

    private static class DeleteNoteTask extends AsyncTask<Note, Void, Void> {

        private NoteDao noteDao;

        private DeleteNoteTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }
}
