package com.upcode.annotations.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.upcode.annotations.Callback;
import com.upcode.annotations.R;
import com.upcode.annotations.activities.EditNoteActivity;
import com.upcode.annotations.activities.NotesActivity;
import com.upcode.annotations.adapter.FolderAdapter;
import com.upcode.annotations.adapter.FolderListener;
import com.upcode.annotations.adapter.NoteAdapter;
import com.upcode.annotations.controller.FabMenuController;
import com.upcode.annotations.controller.FolderController;
import com.upcode.annotations.controller.NoteController;
import com.upcode.annotations.helpers.SwipeToDeleteHelper;
import com.upcode.annotations.model.Folder;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.repository.FolderRepository;
import com.upcode.annotations.repository.NoteRepository;
import com.upcode.annotations.util.FadeUtil;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotesFragment extends Fragment implements MenuFragmentListener {

    private boolean emptyFolders = false;
    private boolean emptyNotes = false;

    private TextView emptyTextView;
    private View viewFolders;
    private View viewNotes;
    private View rootView;

    private FolderAdapter folderAdapter;
    private NoteAdapter noteAdapter;

    private FolderController folderController;
    private NoteController noteController;

    private FabMenuController fabMenuController;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.folderController = FolderController.getInstance(context, FolderRepository.from(context));
        this.noteController = NoteController.getInstance(context, NoteRepository.from(context));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rootView = view.findViewById(R.id.rootView);

        emptyTextView = view.findViewById(R.id.emptyTextView);
        emptyTextView.setVisibility(View.GONE);

        viewFolders = view.findViewById(R.id.viewFolders);
        viewFolders.setVisibility(View.GONE);

        viewNotes = view.findViewById(R.id.viewNotes);
        viewNotes.setVisibility(View.GONE);

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
        folderAdapter = new FolderAdapter(context, folderListener);

        RecyclerView recyclerViewFolder = view.findViewById(R.id.recyclerViewFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewFolder.setAdapter(folderAdapter);

        folderController.getAllFolders().observe(this, folders -> {
            folderAdapter.submitList(folders);
            emptyFolders = folders.isEmpty();

            toggleVisibleFolders(emptyFolders);
            checkAndShowEmptyMessage();
        });
    }

    private void setupRecyclerViewNotes(View view) {
        noteAdapter = new NoteAdapter(context, noteListener);

        RecyclerView recyclerViewNotes = view.findViewById(R.id.recyclerViewNotes);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(context));

        // touch helper
        SwipeToDeleteHelper swipeToDeleteHelper = new SwipeToDeleteHelper() {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note note = noteAdapter.getCurrentList().get(position);

                // delete
                noteController.getNoteRepository().delete(note);


                Snackbar snackbar = Snackbar.make(rootView, "Removed", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", v -> {
                    noteController.getNoteRepository().insert(note, null);
                });
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteHelper);
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes);

        noteController.findAllNotesOutsideFolder().observe(this, notes -> {
            noteAdapter.submitList(notes);
            emptyNotes = notes.isEmpty();

            toggleVisibleNotes(emptyNotes);
            checkAndShowEmptyMessage();
        });

        recyclerViewNotes.setAdapter(noteAdapter);
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
                    public void onResult(Note note) {

                        // start activity after note created
                        startActivity(EditNoteActivity.buildUpdateIntent(context, note));
                        overridePendingTransition();
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
                    overridePendingTransition();
                });
            } else {
                startActivity(NotesActivity.buildIntent(context, folder));
                overridePendingTransition();
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

            startActivity(EditNoteActivity.buildUpdateIntent(context, note));
            overridePendingTransition();

            // show dialog
            /*NoteDetailDialog.getInstance(note, v -> {
                startActivity(EditNoteActivity.buildUpdateIntent(context, note));
                overridePendingTransition();
            }).show(getChildFragmentManager(), "NoteDetailDialog");*/

        }

        @Override
        public void onLongClickNote(Note note, View view, int position) {
            onClickOption(note, view, position);
        }

        @Override
        public void onClickOption(Note note, View view, int position) {
            showNotePopMenuItem(note, view);
        }

        @Override
        public void onClickAlarmIcon(Note note, View view, int position) {

            if (note.alarmIsEnabled()) {

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_alarm, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.item_delete_alarm:
                            // remove alarm
                            noteController.removeAlarm(note);
                            break;
                        case R.id.item_edit_alarm:

                            // set new alarm
                            attemptSetAlarm(note);
                            break;
                    }

                    return true;
                });
                popupMenu.show();

            } else {
                attemptSetAlarm(note);
            }
        }

    };

    private void attemptSetAlarm(Note note) {
        noteController.attemptSetAlarm(new Callback<Long>() {
            @Override
            public void onResult(Long time) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.setTimeInMillis(time);

                Calendar currentDate = Calendar.getInstance();

                if (selectedDate.after(currentDate)) {
                    noteController.setAlarm(note, time);
                } else {
                    Toast.makeText(context, R.string.invalid_date, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void overridePendingTransition() {
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.in_over_from_right, R.anim.anim_static);
        }
    }

    private void showNotePopMenuItem(Note note, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_popup_note);

        popupMenu.setOnMenuItemClickListener(item1 -> {
            switch (item1.getItemId()) {
                case R.id.item_rename_note:
                    noteController.attemptRenameNote(note);
                    break;
                case R.id.item_edit_note:
                    noteListener.onClickNote(note, view);
                    break;
                case R.id.item_delete_note:
                    noteController.attemptDeleteNote(note);
                    break;
            }
            return false;
        });
        popupMenu.show();
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
