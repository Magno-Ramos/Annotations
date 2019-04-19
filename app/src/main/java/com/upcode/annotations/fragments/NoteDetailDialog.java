package com.upcode.annotations.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upcode.annotations.R;
import com.upcode.annotations.model.Note;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import jp.wasabeef.richeditor.RichEditor;

public class NoteDetailDialog extends DialogFragment {

    private View.OnClickListener onClickEditNote;

    private Note note;

    public static NoteDetailDialog getInstance(Note note, View.OnClickListener onClickEditNote) {
        NoteDetailDialog noteDetailDialog = new NoteDetailDialog();
        noteDetailDialog.onClickEditNote = onClickEditNote;
        noteDetailDialog.note = note;
        return noteDetailDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.WideDialog);
        setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_detail_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvAlarm = view.findViewById(R.id.tvAlarm);
        RichEditor editor = view.findViewById(R.id.textEditor);

        editor.setEditorFontSize(16);
        editor.setPadding(16,16,16,16);

        tvTitle.setText(note.getTitle());
        tvAlarm.setText(note.alarmIsEnabled() ? DateFormat.getDateTimeInstance().format(new Date(note.getAlarm())) : "...");
        tvAlarm.setCompoundDrawablesWithIntrinsicBounds(note.alarmIsEnabled() ? R.drawable.ic_alarm_on : R.drawable.ic_alarm_off, 0, 0, 0);
        tvAlarm.setTextColor(note.alarmIsEnabled() ? Color.parseColor("#555555") : Color.parseColor("#999999"));

        editor.setInputEnabled(false);
        editor.setHtml(note.getMessage());

        view.findViewById(R.id.btnDetail).setOnClickListener(onClickEditNote());
    }

    private View.OnClickListener onClickEditNote() {
        return v -> {
            if (onClickEditNote != null) {
                onClickEditNote.onClick(v);
            }

            dismiss();
        };
    }
}
