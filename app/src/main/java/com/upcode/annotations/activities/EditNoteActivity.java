package com.upcode.annotations.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.upcode.annotations.Callback;
import com.upcode.annotations.R;
import com.upcode.annotations.controller.NoteController;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.repository.NoteRepository;

import java.text.DateFormat;
import java.util.Date;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import jp.wasabeef.richeditor.RichEditor;

public class EditNoteActivity extends AppCompatActivity {

    public static final String NOTE_INTENT_KEY = "note";

    private RichEditor editor;

    private String content;
    private Note note;
    private NoteController noteController;

    public static Intent buildUpdateIntent(Context context, Note note) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(NOTE_INTENT_KEY, note);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        this.editor = findViewById(R.id.rich_editor);
        findViewById(R.id.bt_bold).setOnClickListener(onClickRichOption());
        findViewById(R.id.bt_italic).setOnClickListener(onClickRichOption());
        findViewById(R.id.bt_left).setOnClickListener(onClickRichOption());
        findViewById(R.id.bt_right).setOnClickListener(onClickRichOption());
        findViewById(R.id.bt_center).setOnClickListener(onClickRichOption());

        this.noteController = NoteController.getInstance(this, NoteRepository.from(this));

        if (getIntent() != null) {
            note = getIntent().getParcelableExtra(NOTE_INTENT_KEY);
            if (note != null) {
                content = note.getMessage();
                editor.setHtml(content);
                setTitle(note.getTitle());
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editor.setEditorFontSize(18);
        editor.setPadding(10, 10, 10, 10);
        editor.setPlaceholder(getString(R.string.message));
        editor.focusEditor();
        editor.setOnTextChangeListener(text -> this.content = text);

        TextView tvAlarm = findViewById(R.id.tvAlarm);
        tvAlarm.setText(note.alarmIsEnabled() ? DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(new Date(note.getAlarm())) : "...");
        tvAlarm.setCompoundDrawablesWithIntrinsicBounds(note.alarmIsEnabled() ? R.drawable.ic_alarm_on : R.drawable.ic_alarm_off, 0, 0, 0);
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
        this.noteController.attemptDeleteNote(note, new Callback<Boolean>() {
            @Override
            public void onResult(Boolean deleted) {
                if (deleted)
                    finish();
            }
        });
    }

    private void saveAndFinish() {
        this.noteController.updateContent(note, content);
        this.finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_static, R.anim.out_over_to_right);
    }

    @Override
    public void onBackPressed() {
        boolean isModified = !content.equals(note.getMessage());

        if (isModified) {
            attemptSaveContent();
        } else {
            this.finish();
        }
    }

    private void attemptSaveContent() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.save);
        builder.content(R.string.question_save_changes);
        builder.positiveText(R.string.yes);
        builder.onPositive((dialog, which) -> saveAndFinish());
        builder.negativeText(R.string.not);
        builder.onNegative((dialog, which) -> {
            dialog.dismiss();
            this.finish();
        });
        builder.show();
    }

    private View.OnClickListener onClickRichOption() {
        return view -> {
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
        };
    }

}
