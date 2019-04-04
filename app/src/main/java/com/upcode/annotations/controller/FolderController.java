package com.upcode.annotations.controller;

import android.content.Context;
import android.text.InputType;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.upcode.annotations.R;
import com.upcode.annotations.model.Folder;
import com.upcode.annotations.viewmodel.FoldersViewModel;

public class FolderController {

    private Context context;
    private FoldersViewModel foldersViewModel;

    public FolderController(Context context, FoldersViewModel foldersViewModel) {
        this.context = context;
        this.foldersViewModel = foldersViewModel;
    }

    public void attemptRenameFolder(final Folder folder) {
        if (folder.isLocked() && !folder.isTemporarilyUnlocked()) {
            requestPassword(folder, this::showDialogRenameFolder);
        } else {
            showDialogRenameFolder(folder);
        }
    }

    public void attemptDeleteFolder(Folder folder) {
        if (folder.isLocked() && !folder.isTemporarilyUnlocked()) {
            requestPassword(folder, this::showDialogDeleteFolder);
        } else {
            showDialogDeleteFolder(folder);
        }
    }

    public void attemptLockFolder(final Folder folder) {
        if (!folder.isLocked()) {

            MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                    .title(R.string.password)
                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    .inputRange(Folder.MIN_PASSWORD, Folder.MAX_PASSWORD)
                    .autoDismiss(false)
                    .input(R.string.enter_a_password, R.string.empty_text, (dialog, input) -> {

                        Folder updated = new Folder();
                        updated.setId(folder.getId());
                        updated.setRegistered(folder.getRegistered());
                        updated.setTitle(folder.getTitle());

                        //update
                        String pass = input.toString().trim();

                        updated.setLocked(true);
                        updated.setPassword(pass);

                        foldersViewModel.update(updated);
                        dialog.dismiss();

                        Toast.makeText(context, R.string.folder_blocked_successfully, Toast.LENGTH_SHORT).show();
                    }).build();

            configAnimation(materialDialog);
            materialDialog.show();

        } else {
            Toast toast = Toast.makeText(context, R.string.folder_is_already_locked, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public void requestPassword(Folder folder, PasswordCallback passwordCallback) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(folder.getTitle())
                .autoDismiss(false)
                .inputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .inputRange(Folder.MIN_PASSWORD, Folder.MAX_PASSWORD)
                .input(R.string.password, R.string.empty_text, (dialog, input) -> {

                    if (input.toString().trim().equals(folder.getPassword())) {
                        folder.setTemporarilyUnlocked(true);
                        passwordCallback.onSuccess(folder);
                        dialog.dismiss();
                    } else {
                        if (dialog.getInputEditText() != null) {
                            dialog.getInputEditText().setError(context.getString(R.string.invalid_password));
                        } else {
                            Toast.makeText(context, context.getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();

        configAnimation(materialDialog);
        materialDialog.show();
    }

    public void attemptUnlockFolder(final Folder folder) {
        if (folder.isLocked()) {
            requestPassword(folder, folder1 -> {

                Folder updated = new Folder();
                updated.setId(folder.getId());
                updated.setRegistered(folder.getRegistered());
                updated.setTitle(folder.getTitle());

                //update
                updated.setLocked(false);
                updated.setPassword("");

                foldersViewModel.update(updated);
            });
        }
    }

    public void attemptCreateFolder() {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .iconRes(R.drawable.ic_folder)
                .title(context.getString(R.string.new_folder))
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Folder.MIN_TITLE_LENGTH, Folder.MAX_TITLE_LENGTH)
                .input(R.string.hint_title_of_folder, R.string.empty_text, (dialog, input) -> {
                    Folder folder = new Folder();
                    folder.setTitle(input.toString());

                    foldersViewModel.insert(folder);
                }).build();

        configAnimation(materialDialog);
        materialDialog.show();
    }

    private void showDialogRenameFolder(Folder folder) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.rename_folder)
                .inputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(Folder.MIN_TITLE_LENGTH, Folder.MAX_TITLE_LENGTH)
                .input(folder.getTitle(), folder.getTitle(), (dialog, input) -> {

                    Folder updated = new Folder();
                    updated.setId(folder.getId());
                    updated.setRegistered(folder.getRegistered());
                    updated.setTitle(input.toString());
                    updated.setLocked(folder.isLocked());
                    updated.setPassword(folder.getPassword());

                    foldersViewModel.update(updated);
                }).build();

        configAnimation(materialDialog);
        materialDialog.show();
    }

    private void showDialogDeleteFolder(Folder folder) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .title(R.string.delete)
                .content(R.string.question_delete_folder_with_notes)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> foldersViewModel.delete(folder)).build();

        configAnimation(materialDialog);
        materialDialog.show();
    }

    public interface PasswordCallback {
        void onSuccess(Folder folder);
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
