package com.appcode.annotations.viewmodel;

import android.app.Application;

import com.appcode.annotations.callback.OnCreateListener;
import com.appcode.annotations.callback.OnUpdateListener;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.repository.NoteRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class FolderViewModel extends AndroidViewModel {

    private NoteRepository noteRepository;
    private int folderId;

    public FolderViewModel(Application application) {
        super(application);
        this.noteRepository = new NoteRepository(application, false);
    }

    public LiveData<List<Note>> findAllNotes(int folderId) {
        this.folderId = folderId;
        return noteRepository.findAllNotesByFolderId(folderId);
    }

    public void insertNote(Note note, OnCreateListener<Note> onCreateListener) {
        note.setFolderId(folderId);
        noteRepository.insert(note, onCreateListener);
    }

    public void deleteNote(Note note) {
        noteRepository.delete(note);
    }

    public void updateNote(Note note) {
        noteRepository.update(note, null);
    }

    public void updateNote(Note note, OnUpdateListener<Note> updateListener) {
        noteRepository.update(note, updateListener);
    }
}
