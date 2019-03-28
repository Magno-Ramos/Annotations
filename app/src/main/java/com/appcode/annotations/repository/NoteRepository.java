package com.appcode.annotations.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.appcode.annotations.callback.OnCreateListener;
import com.appcode.annotations.callback.OnUpdateListener;
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

        if (findAll) {
            allNotes = noteDao.findAll();
        }
    }

    /**
     * find all notes by folder
     * @param id folder
     * @return list of notes
     */
    public LiveData<List<Note>> findAllNotesByFolderId(int id) {
        allNotes = noteDao.findAllByFolderId(id);
        return allNotes;
    }

    /**
     * insert note to database
     * @param note to insert
     * @param onCreateListener listener for result
     */
    public void insert(Note note, OnCreateListener<Note> onCreateListener) {
        new InsertNoteTask(noteDao, onCreateListener).execute(note);
    }

    /**
     * update note
     * @param note to update
     * @param updateListener listener for result
     */
    public void update(Note note, OnUpdateListener<Note> updateListener) {
        note.setLastModification(System.currentTimeMillis());
        new UpdateNoteTask(noteDao, updateListener).execute(note);
    }

    /**
     * delete note of database
     * @param note to delete
     */
    public void delete(Note note) {
        new DeleteNoteTask(noteDao).execute(note);
    }

    /**
     * find notes changed recents
     * @return list of notes
     */
    public LiveData<List<Note>> findRecentFiles() {
        return noteDao.findRecentFilesChanged();
    }

    /**
     * find all notes in database
     * @return list of notes
     */
    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<List<Note>> findAllNotesOutsideFolder() {
        return noteDao.findAllNotesOutsideFolder();
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

        private OnUpdateListener<Note> onUpdateListener;
        private NoteDao noteDao;

        private UpdateNoteTask(NoteDao noteDao, OnUpdateListener<Note> onUpdateListener) {
            this.noteDao = noteDao;
            this.onUpdateListener = onUpdateListener;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            if (onUpdateListener != null){
                Note note = noteDao.findById(notes[0].getId());
                onUpdateListener.onUpdate(note);
            }
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
