package com.appcode.annotations.controller;

import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appcode.annotations.R;
import com.appcode.annotations.callback.Callback;
import com.appcode.annotations.callback.OnUpdateListener;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.viewmodel.NoteViewModel;

public class NoteController {

    private Context context;
    private NoteViewModel noteViewModel;

    public NoteController(Context context, NoteViewModel noteViewModel) {
        this.context = context;
        this.noteViewModel = noteViewModel;
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

                    noteViewModel.insert(note, callback);
                })
                .show();
    }

    public void attemptDeleteNote(Note note) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.delete)
                .content(R.string.question_delete_note)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive((dialog1, which) -> noteViewModel.delete(note)).build();
        dialog.show();
    }

    public void attemptRenameNote(Note note, OnUpdateListener<Note> updateListener) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(context.getString(R.string.rename))
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Note.MIN_LENGTH, Note.MAX_LENGTH)
                .input(context.getString(R.string.hint_title_of_note), note.getTitle(), (dialog, input) -> {
                    note.setTitle(input.toString().trim());
                    noteViewModel.updateNote(note, updateListener);
                }).build();
        materialDialog.show();
    }
}
