package com.appcode.annotations.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appcode.annotations.R;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.util.HistoryModification;
import com.appcode.annotations.util.Html;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private Context context;
    private NoteListener noteListener;
    private int layoutResId;

    public NoteAdapter(Context context, NoteListener noteListener, @LayoutRes int layoutResId) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.noteListener = noteListener;
        this.layoutResId = layoutResId;
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {

        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getMessage().equals(newItem.getMessage()) &&
                    oldItem.getRegistered() == newItem.getRegistered();
        }
    };

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (this.layoutResId > 0) ? this.layoutResId : R.layout.item_annotation_vert;
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
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

        holder.viewOption.setOnClickListener(v -> {
            if (noteListener != null) {
                noteListener.onClickOption(note, holder.viewOption, position);
            }
        });

        // title
        holder.textView.setText(note.getTitle());

        // last modification
        holder.tvHistory.setText(HistoryModification.findHistoryByTime(System.currentTimeMillis(), note.getLastModification()));

        // sub text
        if (holder.subTextView != null) {
            if (note.getMessage().isEmpty()) {
                holder.subTextView.setVisibility(View.GONE);
            } else {
                holder.subTextView.setVisibility(View.VISIBLE);
                holder.subTextView.setText(Html.stripHtml(note.getMessage()));
            }
        }
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        ImageView viewOption;
        TextView textView;
        TextView subTextView;
        TextView tvHistory;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            viewOption = itemView.findViewById(R.id.bt_option);
            subTextView = itemView.findViewById(R.id.subTextView);
            tvHistory = itemView.findViewById(R.id.tv_history);
        }
    }

    public interface NoteListener {
        void onClickNote(Note note, View view);

        void onLongClickNote(Note note, View view, int position);

        void onClickOption(Note note, View view, int position);
    }
}
