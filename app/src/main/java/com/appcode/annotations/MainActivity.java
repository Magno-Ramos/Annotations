package com.appcode.annotations;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.appcode.annotations.adapter.FolderHorizontalAdapter;
import com.appcode.annotations.adapter.FolderListener;
import com.appcode.annotations.adapter.NoteAdapter;
import com.appcode.annotations.controller.FolderController;
import com.appcode.annotations.model.Folder;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.viewmodel.FoldersViewModel;
import com.appcode.annotations.viewmodel.NoteViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private boolean emptyFolders = false;
    private boolean emptyNotes = false;

    private TextView emptyTextView;

    private AdView adView;
    private View viewFolders;
    private View viewNotes;

    private RecyclerView recyclerViewNote;
    private RecyclerView recyclerViewFolder;

    private NoteViewModel noteViewModel;

    private FolderHorizontalAdapter folderAdapter;
    private NoteAdapter noteAdapter;

    private FolderController folderController;
    // private NoteController noteController;
    // private FabMenuController fabMenuController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FloatingActionButton fabMenu = findViewById(R.id.fab_button_menu);
        FloatingActionButton fabFolder = findViewById(R.id.fab_button_action_folder);
        fabFolder.setOnClickListener(view -> folderController.attemptCreateFolder());
        // FloatingActionButton fabNote = findViewById(R.id.fab_button_action_note);

        /*int xScreen = getResources().getDisplayMetrics().widthPixels;
        int item = getResources().getDimensionPixelSize(R.dimen.item_note_width);
        int spanCount = (xScreen / item);*/

        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setVisibility(View.GONE);

        View viewLayerFabMenu = findViewById(R.id.view_layer_fab_menu);
        viewLayerFabMenu.setVisibility(View.GONE);

        viewFolders = findViewById(R.id.viewFolders);
        viewFolders.setVisibility(View.GONE);

        viewNotes = findViewById(R.id.viewNotes);
        viewNotes.setVisibility(View.GONE);

        recyclerViewFolder = findViewById(R.id.recyclerViewFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewNote = findViewById(R.id.recyclerViewNote);
        recyclerViewNote.setLayoutManager(new LinearLayoutManager(this));

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, notes -> {

            if (recyclerViewFolder != null && notes != null) {
                createAdapterFolderNote();
                noteAdapter.submitList(notes);
            }

            emptyNotes = (notes == null || notes.isEmpty());
            checkEmpty();
        });

        FoldersViewModel foldersViewModel = ViewModelProviders.of(this).get(FoldersViewModel.class);
        foldersViewModel.getAllFolders().observe(this, folders -> {

            if (recyclerViewNote != null && folders != null) {
                createAdapterFolderNote();
                folderAdapter.submitList(folders);
            }

            emptyFolders = (folders == null || folders.isEmpty());
            checkEmpty();
        });

        folderController = new FolderController(this, foldersViewModel);
        // noteController = new NoteController(this, noteViewModel);

        /*fabMenuController = new FabMenuController(this, fabMenu, fabFolder, fabNote, viewLayerFabMenu);
        fabMenuController.setFabListener(fabMenuListener());*/

        MobileAds.initialize(this, getString(R.string.admob_id));

        adView = findViewById(R.id.adView);
        adView.loadAd(new AdRequest.Builder().build());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createAdapterFolderNote() {
        if (folderAdapter == null) {
            folderAdapter = new FolderHorizontalAdapter(this, folderListener);
            recyclerViewFolder.setAdapter(folderAdapter);
        }

        if (noteAdapter == null) {
            noteAdapter = new NoteAdapter(this, noteListener);
            recyclerViewNote.setAdapter(noteAdapter);
            recyclerViewNote.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
    }

    private void checkEmpty() {
        emptyTextView.setVisibility((emptyFolders && emptyNotes) ? View.VISIBLE : View.GONE);
        viewFolders.setVisibility(emptyFolders ? View.GONE : View.VISIBLE);
        viewNotes.setVisibility(emptyNotes ? View.GONE : View.VISIBLE);
    }

    /**
     * Fab Listener used on FabMenuController
     *
     * @return Listener
     */
    /*private FabMenuController.FabListener fabMenuListener() {
        return new FabMenuController.FabListener() {
            @Override
            public void onClickFabFolder(View view) {
                folderController.attemptCreateFolder();
            }

            @Override
            public void onClickFabNote(View view) {
                noteController.attemptCreateNote();
            }
        };
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Note Listener
     */
    private NoteAdapter.NoteListener noteListener = new NoteAdapter.NoteListener() {
        @Override
        public void onClickNote(Note note, View view) {
            startActivityForResult(EditNoteActivity.buildUpdateIntent(MainActivity.this, note), 0);
        }

        @Override
        public void onClickOption(Note note, View view) {
            // menu option
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
            popupMenu.inflate(R.menu.menu_popup_note);
            popupMenu.setOnMenuItemClickListener(item1 -> {
                switch (item1.getItemId()) {
                    case R.id.item_edit_note:
                        onClickNote(note, view);
                        break;
                    /*case R.id.item_delete_note:
                        noteController.attemptDeleteNote(note);
                        break;*/
                }
                return false;
            });

            popupMenu.show();
        }
    };

    /**
     * Folder Listener
     */
    private FolderListener folderListener = new FolderListener() {
        @Override
        public void onClickFolder(Folder folder, View view) {

            if (folder.isLocked() && !folder.isTemporarilyUnlocked()) {
                folderController.requestPassword(folder, folder1 -> {
                    startActivityForResult(IntoFolderActivity.buildIntent(MainActivity.this, folder1), 0);
                    overridePendingTransition(R.anim.in_over_from_right, R.anim.anim_static);
                });
            } else {
                startActivityForResult(IntoFolderActivity.buildIntent(MainActivity.this, folder), 0);
                overridePendingTransition(R.anim.in_over_from_right, R.anim.anim_static);
            }

        }

        @Override
        public void onLongClickFolder(Folder folder, View view) {
            onClickOption(folder, view);
        }

        @Override
        public void onClickOption(Folder folder, View view) {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((resultCode == EditNoteActivity.RESULT_CREATE || resultCode == EditNoteActivity.RESULT_UPDATE || resultCode == EditNoteActivity.RESULT_DELETE) && data != null) {
            Note note = data.getParcelableExtra(EditNoteActivity.NOTE_INTENT_KEY);
            if (note != null) {

                switch (resultCode) {
                    case EditNoteActivity.RESULT_CREATE:
                        noteViewModel.insert(note);
                        break;
                    case EditNoteActivity.RESULT_UPDATE:
                        noteViewModel.update(note);
                        break;
                    case EditNoteActivity.RESULT_DELETE:
                        noteViewModel.delete(note);
                        break;
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        /*if (fabMenuController.isOpen()) {
            fabMenuController.toggleMenu();
            return;
        }*/
        super.onBackPressed();
    }
}
