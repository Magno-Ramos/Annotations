package com.upcode.annotations.controller;

import android.content.Context;
import android.text.InputType;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.upcode.annotations.R;
import com.upcode.annotations.callback.OnCreateListener;
import com.upcode.annotations.callback.OnUpdateListener;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.viewmodel.FolderViewModel;

public class InFolderController {

    private Context context;
    private FolderViewModel folderViewModel;

    public InFolderController(Context context, FolderViewModel folderViewModel) {
        this.context = context;
        this.folderViewModel = folderViewModel;
    }

    public void attemptCreateNote(OnCreateListener<Note> onCreateListener) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .iconRes(R.drawable.ic_file_primary)
                .title(context.getString(R.string.new_note))
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Note.MIN_LENGTH, Note.MAX_LENGTH)
                .input(R.string.hint_title_of_note, R.string.empty_text, (dialog, input) -> {
                    Note note = new Note();
                    note.setTitle(input.toString().trim());

                    folderViewModel.insertNote(note, onCreateListener);
                }).build();

        configAnimation(materialDialog);
        materialDialog.show();
    }

    public void attemptDeleteNote(Note note) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.delete)
                .content(R.string.question_delete_note)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive((dialog1, which) -> folderViewModel.deleteNote(note)).build();

        configAnimation(materialDialog);
        materialDialog.show();
    }

    public void attemptRenameNote(Note note, OnUpdateListener<Note> updateListener) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(context.getString(R.string.rename))
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Note.MIN_LENGTH, Note.MAX_LENGTH)
                .input(context.getString(R.string.hint_title_of_note), note.getTitle(), (dialog, input) -> {
                    note.setTitle(input.toString().trim());
                    folderViewModel.updateNote(note, updateListener);
                }).build();

        configAnimation(materialDialog);
        materialDialog.show();
    }

    public void attemptRenameNote(Note note) {
        attemptRenameNote(note, null);
    }

    private void configAnimation(MaterialDialog materialDialog) {
        if (materialDialog != null && !materialDialog.isShowing()) {
            Window window = materialDialog.getWindow();
            if (window != null) {
                window.getAttributes().windowAnimations = R.style.DialogAnimation;
            }
        }
    }
}
