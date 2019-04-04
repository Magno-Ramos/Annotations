package com.upcode.annotations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.upcode.annotations.adapter.NoteAdapter;
import com.upcode.annotations.callback.OnCreateListener;
import com.upcode.annotations.controller.InFolderController;
import com.upcode.annotations.model.Folder;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.viewmodel.FolderViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotesActivity extends AppCompatActivity implements NoteAdapter.NoteListener, OnCreateListener<Note> {

    public static final String KEY_INTENT_FOLDER = "INTENT_KEY_FOLDER";

    private TextView textViewEmpty;

    private NoteAdapter noteAdapter;
    private InFolderController inFolderController;

    public static Intent buildIntent(Context context, Folder folder) {
        Intent intent = new Intent(context, NotesActivity.class);
        intent.putExtra(KEY_INTENT_FOLDER, folder);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        setupActionBar();

        FloatingActionButton fab = findViewById(R.id.fab);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNote);
        textViewEmpty = findViewById(R.id.text_view_empty_notes);

        noteAdapter = new NoteAdapter(this, this, R.layout.item_note_outside_folder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_right));
        recyclerView.setAdapter(noteAdapter);

        Folder folder = getIntent().getParcelableExtra(KEY_INTENT_FOLDER);
        setTitle(folder.getTitle());

        FolderViewModel folderViewModel = ViewModelProviders.of(this).get(FolderViewModel.class);
        folderViewModel.findAllNotes(folder.getId()).observe(this, notes -> {
            noteAdapter.submitList(notes);
            textViewEmpty.setVisibility(View.GONE);
            if (notes.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
                textViewEmpty.animate()
                        .setDuration(200)
                        .alpha(1)
                        .start();
            }

        });

        inFolderController = new InFolderController(this, folderViewModel);
        fab.setOnClickListener(v -> inFolderController.attemptCreateNote(this));
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.ic_folder_white);
            actionBar.setDisplayUseLogoEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_into_folder, menu);
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
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_static, R.anim.out_over_to_right);
    }

    @Override
    public void onClickNote(Note note, View view) {
        startActivityForResult(EditNoteActivity.buildUpdateIntent(this, note), 0);
    }

    @Override
    public void onLongClickNote(Note note, View view, int position) {
        onClickOption(note, view, position);
    }

    @Override
    public void onClickOption(Note note, View view, int position) {
        PopupMenu popupMenu = new PopupMenu(NotesActivity.this, view);
        popupMenu.inflate(R.menu.menu_popup_note);
        popupMenu.setOnMenuItemClickListener(item1 -> {
            switch (item1.getItemId()) {
                case R.id.item_rename_note:
                    inFolderController.attemptRenameNote(note, noteUpdated -> {
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
                    inFolderController.attemptDeleteNote(note);
                    break;
            }
            return false;
        });

        popupMenu.show();
    }

    @Override
    public void onCreate(Note note) {
        startActivityForResult(EditNoteActivity.buildUpdateIntent(this, note), 0);
    }
}
