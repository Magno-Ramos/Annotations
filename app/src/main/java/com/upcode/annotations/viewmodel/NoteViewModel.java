package com.upcode.annotations.viewmodel;

import android.app.Application;

import com.upcode.annotations.callback.Callback;
import com.upcode.annotations.callback.OnUpdateListener;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.repository.NoteRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class NoteViewModel extends AndroidViewModel {

    private NoteRepository noteRepository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new NoteRepository(application, true);
    }

    public void insert(Note note, Callback<Note> noteCallback) {
        noteRepository.insert(note, noteCallback::onSuccess);
    }

    public void update(Note note) {
        noteRepository.update(note, null);
    }

    public void delete(Note note) {
        noteRepository.delete(note);
    }

    public LiveData<List<Note>> findAllNotesOutsideFolder() {
        return noteRepository.findAllNotesOutsideFolder();
    }

    public LiveData<List<Note>> findRecentFilesChanged() {
        return noteRepository.findRecentFiles();
    }

    public void updateNote(Note note, OnUpdateListener<Note> updateListener) {
        noteRepository.update(note, updateListener);
    }
}
