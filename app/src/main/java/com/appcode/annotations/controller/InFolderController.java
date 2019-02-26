package com.appcode.annotations.controller;

import android.content.Context;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appcode.annotations.R;
import com.appcode.annotations.callback.OnCreateListener;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.viewmodel.FolderViewModel;

public class InFolderController {

    private Context context;
    private FolderViewModel folderViewModel;

    public InFolderController(Context context, FolderViewModel folderViewModel) {
        this.context = context;
        this.folderViewModel = folderViewModel;
    }

    public void attemptCreateNote(OnCreateListener<Note> onCreateListener) {
        new MaterialDialog.Builder(context)
                .iconRes(R.drawable.ic_note)
                .title(context.getString(R.string.new_note))
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Note.MIN_LENGTH, Note.MAX_LENGTH)
                .input(R.string.hint_title_of_note, R.string.empty_text, (dialog, input) -> {
                    Note note = new Note();
                    note.setTitle(input.toString().trim());

                    folderViewModel.insertNote(note, onCreateListener);

                })
                .show();
    }

    public void attemptDeleteNote(Note note) {
        new MaterialDialog.Builder(context)
                .title(R.string.delete)
                .content(R.string.question_delete_note)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive((dialog1, which) -> folderViewModel.deleteNote(note)).build().show();
    }
}
