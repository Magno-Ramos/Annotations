package com.appcode.annotations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.appcode.annotations.adapter.NoteAdapter;
import com.appcode.annotations.callback.OnCreateListener;
import com.appcode.annotations.controller.InFolderController;
import com.appcode.annotations.model.Folder;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.viewmodel.FolderViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IntoFolderActivity extends AppCompatActivity implements NoteAdapter.NoteListener, OnCreateListener<Note> {

    public static final String KEY_INTENT_FOLDER = "KEY_INTENT_FOLDER";

    @BindView(R.id.text_view_empty_notes)
    TextView textViewEmpty;

    @BindView(R.id.recyclerViewNote)
    RecyclerView recyclerView;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    private NoteAdapter noteAdapter;
    private FolderViewModel folderViewModel;
    private InFolderController inFolderController;

    public static Intent buildIntent(Context context, Folder folder) {
        Intent intent = new Intent(context, IntoFolderActivity.class);
        intent.putExtra(KEY_INTENT_FOLDER, folder);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        setupActionBar();

        ButterKnife.bind(this);

        noteAdapter = new NoteAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down));
        recyclerView.setAdapter(noteAdapter);

        Folder folder = getIntent().getParcelableExtra(KEY_INTENT_FOLDER);
        setTitle(folder.getTitle());

        folderViewModel = ViewModelProviders.of(this).get(FolderViewModel.class);
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
            /*case R.id.item_add_note:
                inFolderController.attemptCreateNote(this);
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_static, R.anim.out_over_to_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == EditNoteActivity.RESULT_UPDATE || resultCode == EditNoteActivity.RESULT_DELETE) && data != null) {
            Note note = data.getParcelableExtra(EditNoteActivity.NOTE_INTENT_KEY);
            if (note != null) {

                switch (resultCode) {
                    case EditNoteActivity.RESULT_UPDATE:
                        folderViewModel.updateNote(note);
                        break;
                    case EditNoteActivity.RESULT_DELETE:
                        folderViewModel.deleteNote(note);
                        break;
                }
            }
        }
    }

    @Override
    public void onClickNote(Note note, View view) {
        startActivityForResult(EditNoteActivity.buildUpdateIntent(this, note), 0);
    }

    @Override
    public void onClickOption(Note note, View view) {
        PopupMenu popupMenu = new PopupMenu(IntoFolderActivity.this, view);
        popupMenu.inflate(R.menu.menu_popup_note);
        popupMenu.setOnMenuItemClickListener(item1 -> {
            switch (item1.getItemId()) {
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
