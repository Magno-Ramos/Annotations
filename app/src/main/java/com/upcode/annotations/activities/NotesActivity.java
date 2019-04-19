package com.upcode.annotations.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.upcode.annotations.Callback;
import com.upcode.annotations.R;
import com.upcode.annotations.adapter.NoteAdapter;
import com.upcode.annotations.controller.NoteController;
import com.upcode.annotations.model.Folder;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.repository.NoteRepository;

import java.util.Calendar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotesActivity extends AppCompatActivity implements NoteAdapter.NoteListener {

    public static final String KEY_INTENT_FOLDER = "INTENT_KEY_FOLDER";

    private TextView textViewEmpty;

    private NoteAdapter noteAdapter;
    private NoteController noteController;
    private Folder folder;

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

        noteAdapter = new NoteAdapter(this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_from_right));
        recyclerView.setAdapter(noteAdapter);

        folder = getIntent().getParcelableExtra(KEY_INTENT_FOLDER);
        setTitle(folder.getTitle());

        noteController = NoteController.getInstance(this, NoteRepository.from(this));
        noteController.findAllNotesByFolderId(folder.getId()).observe(this, notes -> {
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

        noteController = NoteController.getInstance(this, NoteRepository.from(this));
        fab.setOnClickListener(v -> noteController.attemptCreateNote(folder.getId(), new Callback<Note>() {
            @Override
            public void onResult(Note note) {

                // start activity after note created
                startActivity(EditNoteActivity.buildUpdateIntent(NotesActivity.this, note));
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        }));
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
        startActivity(EditNoteActivity.buildUpdateIntent(this, note));
        overridePendingTransition(R.anim.in_over_from_right, R.anim.anim_static);
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
                    noteController.attemptRenameNote(note);
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

    @Override
    public void onClickAlarmIcon(Note note, View view, int position) {
        if (note.alarmIsEnabled()) {
            PopupMenu popupMenu = new PopupMenu(NotesActivity.this, view);
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
                    Toast.makeText(NotesActivity.this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
