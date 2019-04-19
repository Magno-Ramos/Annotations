package com.upcode.annotations.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.upcode.annotations.R;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.util.History;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private Context context;
    private NoteListener noteListener;

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {

        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getMessage().equals(newItem.getMessage()) &&
                    oldItem.getAlarm() == newItem.getAlarm() &&
                    oldItem.getFolderId() == newItem.getFolderId() &&
                    oldItem.getTag() == newItem.getTag() &&
                    oldItem.getLastModification() == newItem.getLastModification();
        }
    };

    public NoteAdapter(Context context, NoteListener noteListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.noteListener = noteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note_outside_folder, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = getItem(position);

        holder.itemView.setOnClickListener(v -> {
            if (noteListener != null) {
                noteListener.onClickNote(note, v);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (noteListener != null) {
                noteListener.onLongClickNote(note, v, position);
            }
            return true;
        });

        holder.viewOption.setOnClickListener(v -> noteListener.onClickOption(note, holder.viewOption, position));
        holder.btAlarm.setOnClickListener(v -> noteListener.onClickAlarmIcon(note, v, position));

        // title
        holder.textView.setText(note.getTitle());

        // last modification
        holder.tvHistory.setText(History.findHistoryByTime(System.currentTimeMillis(), note.getLastModification()));

        holder.btAlarm.setImageResource(note.alarmIsEnabled() ? R.drawable.ic_alarm_on : R.drawable.ic_alarm_off);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        View viewOption;
        TextView textView;
        TextView tvHistory;

        ImageButton btAlarm;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            viewOption = itemView.findViewById(R.id.bt_option);
            tvHistory = itemView.findViewById(R.id.tv_history);
            btAlarm = itemView.findViewById(R.id.bt_notification);
        }
    }

    public interface NoteListener {
        void onClickNote(Note note, View view);

        void onLongClickNote(Note note, View view, int position);

        void onClickOption(Note note, View view, int position);

        void onClickAlarmIcon(Note note, View view, int position);
    }
}
