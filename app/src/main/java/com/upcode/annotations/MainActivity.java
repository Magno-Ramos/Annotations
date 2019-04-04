package com.upcode.annotations;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.upcode.annotations.adapter.FolderHorizontalAdapter;
import com.upcode.annotations.adapter.FolderListener;
import com.upcode.annotations.adapter.NoteAdapter;
import com.upcode.annotations.callback.Callback;
import com.upcode.annotations.controller.FabMenuController;
import com.upcode.annotations.controller.FolderController;
import com.upcode.annotations.controller.NoteController;
import com.upcode.annotations.model.Folder;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.viewmodel.FoldersViewModel;
import com.upcode.annotations.viewmodel.NoteViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private boolean emptyFolders = false;
    private boolean emptyNotes = false;

    private TextView emptyTextView;

    private AdView adView;

    private View viewFolders;
    private View viewNotes;

    private FolderHorizontalAdapter folderAdapter;
    private NoteAdapter noteAdapter;

    private FolderController folderController;
    private NoteController noteController;

    private FabMenuController fabMenuController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emptyTextView = findViewById(R.id.emptyTextView);
        emptyTextView.setVisibility(View.GONE);

        viewFolders = findViewById(R.id.viewFolders);
        viewFolders.setVisibility(View.GONE);

        viewNotes = findViewById(R.id.viewNotes);
        viewNotes.setVisibility(View.INVISIBLE);

        setupFabMenu();
        setupRecyclerViewFolders();
        setupRecyclerViewNotes();
        setupAds();

        /*int xScreen = getResources().getDisplayMetrics().widthPixels;
        int item = getResources().getDimensionPixelSize(R.dimen.item_note_width);
        int spanCount = (xScreen / item);*/
    }

    private void setupFabMenu() {
        FloatingActionButton fabFolder = findViewById(R.id.fab_button_action_folder);
        fabFolder.setOnClickListener(view -> folderController.attemptCreateFolder());

        View viewLayerFabMenu = findViewById(R.id.view_layer_fab_menu);

        fabMenuController = new FabMenuController(this, viewLayerFabMenu);
        fabMenuController.setFabFolder(fabFolder);
        fabMenuController.setFabNote(findViewById(R.id.fab_button_action_note));
        fabMenuController.setFabMenuToggle(findViewById(R.id.fab_button_menu));
        fabMenuController.setFabListener(fabMenuListener());
    }

    private void setupRecyclerViewFolders() {
        folderAdapter = new FolderHorizontalAdapter(this, folderListener);

        RecyclerView recyclerViewFolder = findViewById(R.id.recyclerViewFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewFolder.setAdapter(folderAdapter);

        FoldersViewModel foldersViewModel = ViewModelProviders.of(this).get(FoldersViewModel.class);
        foldersViewModel.getAllFolders().observe(this, folders -> {
            folderAdapter.submitList(folders);
            emptyFolders = folders.isEmpty();
            viewFolders.setVisibility(emptyFolders ? View.GONE : View.VISIBLE);
            checkEmpty();
        });

        folderController = new FolderController(this, foldersViewModel);
    }

    private void setupRecyclerViewNotes() {
        noteAdapter = new NoteAdapter(this, noteListener, R.layout.item_note_outside_folder);

        RecyclerView recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotes.setAdapter(noteAdapter);

        NoteViewModel noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.findAllNotesOutsideFolder().observe(this, notes -> {
            noteAdapter.submitList(notes);
            emptyNotes = notes.isEmpty();
            viewNotes.setVisibility(emptyNotes ? View.INVISIBLE : View.VISIBLE);
            checkEmpty();
        });

        noteController = new NoteController(this, noteViewModel);
    }

    private void setupAds() {
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

    private void checkEmpty() {
        emptyTextView.setVisibility((emptyFolders && emptyNotes) ? View.VISIBLE : View.GONE);
        viewFolders.setVisibility((emptyFolders && emptyNotes) ? View.GONE : View.VISIBLE);
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
                        startActivity(EditNoteActivity.buildUpdateIntent(MainActivity.this, note));
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    }
                });
            }
        };
    }

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

    @Override
    public void onBackPressed() {
        if (fabMenuController != null && fabMenuController.isOpened()) {
            fabMenuController.dismiss();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Folder Listener
     */
    private FolderListener folderListener = new FolderListener() {
        @Override
        public void onClickFolder(Folder folder, View view) {

            if (folder.isLocked() && !folder.isTemporarilyUnlocked()) {
                folderController.requestPassword(folder, folder1 -> {
                    startActivityForResult(NotesActivity.buildIntent(MainActivity.this, folder1), 0);
                    overridePendingTransition(R.anim.in_over_from_right, R.anim.anim_static);
                });
            } else {
                startActivityForResult(NotesActivity.buildIntent(MainActivity.this, folder), 0);
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

    private NoteAdapter.NoteListener noteListener = new NoteAdapter.NoteListener() {
        @Override
        public void onClickNote(Note note, View view) {
            startActivityForResult(EditNoteActivity.buildUpdateIntent(MainActivity.this, note), 0);
        }

        @Override
        public void onLongClickNote(Note note, View view, int position) {
            onClickOption(note, view, position);
        }

        @Override
        public void onClickOption(Note note, View view, int position) {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
            popupMenu.inflate(R.menu.menu_popup_note);
            popupMenu.setOnMenuItemClickListener(item1 -> {
                switch (item1.getItemId()) {
                    case R.id.item_rename_note:
                        noteController.attemptRenameNote(note, noteUpdated -> {
                            // check if is updated
                            if (noteUpdated != null) {
                                runOnUiThread(() -> noteAdapter.notifyItemChanged(position));
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

}
