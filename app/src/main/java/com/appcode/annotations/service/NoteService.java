package com.appcode.annotations.service;

import com.appcode.annotations.callback.Callback;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.repository.NoteRepository;

import java.util.List;

import androidx.lifecycle.LiveData;

public class NoteService implements Service<Note> {

    private NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public void fetchAll(Callback<LiveData<List<Note>>> callback) {
        callback.onSuccess(this.noteRepository.getAllNotes());
    }

    @Override
    public void save(Note note, Callback<Note> callback) {
        this.noteRepository.insert(note, callback::onSuccess);
    }

    @Override
    public void edit(Note note, Callback<Note> callback) {

    }

    @Override
    public void delete(Note note, Callback<Note> callback) {
        this.noteRepository.delete(note);
    }
}
