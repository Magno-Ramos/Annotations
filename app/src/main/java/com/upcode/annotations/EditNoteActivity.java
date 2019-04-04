package com.upcode.annotations;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.repository.NoteRepository;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.richeditor.RichEditor;

public class EditNoteActivity extends AppCompatActivity {

    public static final String NOTE_INTENT_KEY = "note";

    @BindView(R.id.rich_editor)
    RichEditor editor;

    private Note initNote;
    private Note note;

    private NoteRepository noteRepository;

    public static Intent buildUpdateIntent(Context context, Note note) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(NOTE_INTENT_KEY, note);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        ButterKnife.bind(this);

        noteRepository = new NoteRepository(getApplication(), false);

        if (getIntent() != null) {
            note = getIntent().getParcelableExtra(NOTE_INTENT_KEY);
            if (note != null) {
                initNote = note.doClone();
                editor.setHtml(note.getMessage());
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(note.getTitle());
        }

        editor.setEditorFontSize(18);
        editor.setPadding(10, 10, 10, 10);
        editor.setPlaceholder(getString(R.string.message));
        editor.focusEditor();
        editor.setOnTextChangeListener(text -> note.setMessage(text));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_context_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.item_save) {
            saveAndFinish();
        } else if (item.getItemId() == R.id.item_delete) {
            deleteAndFinish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAndFinish() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.delete)
                .content(R.string.question_delete_note)
                .positiveText(R.string.confirm)
                .onPositive((dialog1, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra(NOTE_INTENT_KEY, note);

                    this.noteRepository.delete(note);
                    finish();
                }).build();
        dialog.show();
    }

    private void saveAndFinish() {
        this.noteRepository.update(note, null);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!(initNote.getMessage().equals(note.getMessage()))) {

            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(R.string.save);
            builder.content(R.string.question_save_changes);
            builder.positiveText(R.string.yes);
            builder.onPositive((dialog, which) -> saveAndFinish());
            builder.negativeText(R.string.not);
            builder.onNegative((dialog, which) -> {
                dialog.dismiss();
                super.onBackPressed();
            });
            builder.show();

        } else {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.bt_italic, R.id.bt_bold, R.id.bt_center, R.id.bt_left, R.id.bt_right})
    public void onClickEdit(View view) {
        switch (view.getId()) {
            case R.id.bt_bold:
                editor.setBold();
                break;

            case R.id.bt_italic:
                editor.setItalic();
                break;

            case R.id.bt_center:
                editor.setAlignCenter();
                break;

            case R.id.bt_left:
                editor.setAlignLeft();
                break;

            case R.id.bt_right:
                editor.setAlignRight();
                break;

        }
    }
}
