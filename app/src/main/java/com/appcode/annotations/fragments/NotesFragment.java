package com.appcode.annotations.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.appcode.annotations.EditNoteActivity;
import com.appcode.annotations.NotesActivity;
import com.appcode.annotations.R;
import com.appcode.annotations.adapter.FolderHorizontalAdapter;
import com.appcode.annotations.adapter.FolderListener;
import com.appcode.annotations.adapter.NoteAdapter;
import com.appcode.annotations.callback.Callback;
import com.appcode.annotations.controller.FabMenuController;
import com.appcode.annotations.controller.FolderController;
import com.appcode.annotations.controller.NoteController;
import com.appcode.annotations.model.Folder;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.util.FadeUtil;
import com.appcode.annotations.viewmodel.FoldersViewModel;
import com.appcode.annotations.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotesFragment extends Fragment implements MenuFragmentListener {

    private boolean emptyFolders = false;
    private boolean emptyNotes = false;

    private TextView emptyTextView;
    private View viewFolders;
    private View viewNotes;

    private FolderHorizontalAdapter folderAdapter;
    private NoteAdapter noteAdapter;

    private FolderController folderController;
    private NoteController noteController;

    private FabMenuController fabMenuController;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        emptyTextView = view.findViewById(R.id.emptyTextView);
        emptyTextView.setVisibility(View.GONE);

        viewFolders = view.findViewById(R.id.viewFolders);
        viewFolders.setVisibility(View.GONE);

        viewNotes = view.findViewById(R.id.viewNotes);
        viewNotes.setVisibility(View.INVISIBLE);

        setupFabMenu(view);
        setupRecyclerViewFolders(view);
        setupRecyclerViewNotes(view);
    }

    private void setupFabMenu(View view) {
        FloatingActionButton fabFolder = view.findViewById(R.id.fab_button_action_folder);
        fabFolder.setOnClickListener(item -> folderController.attemptCreateFolder());

        View viewLayerFabMenu = view.findViewById(R.id.view_layer_fab_menu);

        fabMenuController = new FabMenuController(context, viewLayerFabMenu);
        fabMenuController.setFabFolder(fabFolder);
        fabMenuController.setFabNote(view.findViewById(R.id.fab_button_action_note));
        fabMenuController.setFabMenuToggle(view.findViewById(R.id.fab_button_menu));
        fabMenuController.setFabListener(fabMenuListener());
    }

    private void setupRecyclerViewFolders(View view) {
        folderAdapter = new FolderHorizontalAdapter(context, folderListener);

        RecyclerView recyclerViewFolder = view.findViewById(R.id.recyclerViewFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewFolder.setAdapter(folderAdapter);

        FoldersViewModel foldersViewModel = ViewModelProviders.of(this).get(FoldersViewModel.class);
        foldersViewModel.getAllFolders().observe(this, folders -> {
            folderAdapter.submitList(folders);
            emptyFolders = folders.isEmpty();

            toggleVisibleFolders(emptyFolders);
            checkAndShowEmptyMessage();
        });

        folderController = new FolderController(context, foldersViewModel);
    }

    private void setupRecyclerViewNotes(View view) {
        noteAdapter = new NoteAdapter(context, noteListener, R.layout.item_note_outside_folder);

        RecyclerView recyclerViewNotes = view.findViewById(R.id.recyclerViewNotes);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewNotes.setAdapter(noteAdapter);

        NoteViewModel noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.findAllNotesOutsideFolder().observe(this, notes -> {
            noteAdapter.submitList(notes);
            emptyNotes = notes.isEmpty();

            toggleVisibleNotes(emptyNotes);
            checkAndShowEmptyMessage();
        });

        noteController = new NoteController(context, noteViewModel);
    }

    private void checkAndShowEmptyMessage() {
        if (emptyFolders && emptyNotes) {
            // show empty message
            FadeUtil.show(emptyTextView, 200);
        } else {
            // check if is visible
            if (emptyTextView.getVisibility() == View.VISIBLE) {
                // hide empty message
                FadeUtil.hide(emptyTextView, 200);
            }
        }
    }

    private void toggleVisibleNotes(boolean isEmpty) {
        if (isEmpty) {
            // hide
            FadeUtil.hide(viewNotes, 200);
        } else {
            // show
            if (viewNotes.getVisibility() != View.VISIBLE) {
                FadeUtil.show(viewNotes, 200);
            }
        }
    }

    private void toggleVisibleFolders(boolean isEmpty) {
        if (isEmpty) {
            // hide
            FadeUtil.hide(viewFolders, 200);
        } else {

            // show
            if (viewFolders.getVisibility() != View.VISIBLE) {
                FadeUtil.show(viewFolders, 200);
            }
        }
    }

    /**
     * Fab Listener used on FabMenuController
     *
     * @return Listener
     */
    private FabMenuController.FabListener fabMenuListener() {
        return new FabMenuController.FabListener() {
            @Override
            public void onClickFabFolder(View view) {
                folderController.attemptCreateFolder();
            }

            @Override
            public void onClickFabNote(View view) {
                noteController.attemptCreateNote(new Callback<Note>() {
                    @Override
                    public void onSuccess(Note note) {
                        startActivity(EditNoteActivity.buildUpdateIntent(context, note));
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    }
                });
            }
        };
    }

    /**
     * Folder Listener
     */
    private FolderListener folderListener = new FolderListener() {
        @Override
        public void onClickFolder(Folder folder, View view) {

            if (folder.isLocked() && !folder.isTemporarilyUnlocked()) {
                folderController.requestPassword(folder, folder1 -> {
                    startActivity(NotesActivity.buildIntent(context, folder1));
                    overridePendingTransition(R.anim.in_over_from_right, R.anim.anim_static);
                });
            } else {
                startActivity(NotesActivity.buildIntent(context, folder));
                overridePendingTransition(R.anim.in_over_from_right, R.anim.anim_static);
            }

        }

        @Override
        public void onLongClickFolder(Folder folder, View view) {
            onClickOption(folder, view);
        }

        @Override
        public void onClickOption(Folder folder, View view) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.inflate(R.menu.menu_popup_folder);
            popupMenu.setOnMenuItemClickListener(item1 -> {
                switch (item1.getItemId()) {
                    case R.id.item_rename:
                        folderController.attemptRenameFolder(folder);
                        break;

                    case R.id.item_delete:
                        folderController.attemptDeleteFolder(folder);
                        break;
                    case R.id.item_lock:
                        folderController.attemptLockFolder(folder);
                        break;

                    case R.id.item_unlock:
                        folderController.attemptUnlockFolder(folder);
                        break;
                }
                return false;
            });

            Menu menu = popupMenu.getMenu();
            menu.removeItem(folder.isLocked() ? R.id.item_lock : R.id.item_unlock);
            popupMenu.show();
        }
    };

    private NoteAdapter.NoteListener noteListener = new NoteAdapter.NoteListener() {

        @Override
        public void onClickNote(Note note, View view) {
            startActivityForResult(EditNoteActivity.buildUpdateIntent(context, note), 0);
        }

        @Override
        public void onLongClickNote(Note note, View view, int position) {
            onClickOption(note, view, position);
        }

        @Override
        public void onClickOption(Note note, View view, int position) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.inflate(R.menu.menu_popup_note);
            popupMenu.setOnMenuItemClickListener(item1 -> {
                switch (item1.getItemId()) {
                    case R.id.item_rename_note:
                        noteController.attemptRenameNote(note, noteUpdated -> {
                            if (noteUpdated != null) {
                                new Handler(Looper.getMainLooper()).post(() -> noteAdapter.notifyItemChanged(position));
                            }
                        });
                        break;
                    case R.id.item_edit_note:
                        onClickNote(note, view);
                        break;
                    case R.id.item_delete_note:
                        noteController.attemptDeleteNote(note);
                        break;
                }
                return false;
            });

            popupMenu.show();
        }
    };

    private void overridePendingTransition(int enterAnim, int exitAnim) {
        if (getActivity() != null) {
            getActivity().overridePendingTransition(enterAnim, exitAnim);
        }
    }

    @Override
    public boolean onBackPressed() {
        if (fabMenuController != null && fabMenuController.isOpened()) {
            fabMenuController.dismiss();
            return false;
        }
        return true;
    }
}
