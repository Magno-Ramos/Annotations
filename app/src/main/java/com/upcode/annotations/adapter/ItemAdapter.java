package com.upcode.annotations.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.upcode.annotations.R;
import com.upcode.annotations.model.Folder;
import com.upcode.annotations.model.Item;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.model.TagType;

public class ItemAdapter extends ListAdapter<Item, ItemAdapter.NoteViewHolder> {

    private Context context;
    private OnClickListener onClickListener;

    public ItemAdapter(@NonNull Context context, OnClickListener onClickListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.onClickListener = onClickListener;
    }

    private static DiffUtil.ItemCallback<Item> DIFF_CALLBACK = new DiffUtil.ItemCallback<Item>() {

        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            /*boolean oldIsNote = (oldItem instanceof Note);
            boolean newIsNote = (newItem instanceof Note);

            boolean valid = oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getPassword().equals(newItem.getPassword()) &&
                    (oldItem.isLocked() && newItem.isLocked()) &&
                    (oldIsNote && newIsNote);

            // verifica se são anotações
            if (oldIsNote && newIsNote) {

                // se for, adiciona a verificação da mensagem
                valid = valid && (((Note) oldItem).getMessage().equals(((Note) newItem).getMessage()));
            }*/

            return true;
        }
    };

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int position) {

        Item item = getItem(position);
        // boolean isNote = (item instanceof Note);

        // title
        noteViewHolder.textView.setText(item.getTitle());

        // click
       /* noteViewHolder.item.setOnClickListener(v -> {
            if (onClickListener != null) {
                if (isNote) {
                    onClickListener.onClickNote((Note) item);
                } else {
                    // onClickListener.onClickFolder((Folder) item);
                }
            }
        });

        noteViewHolder.viewOption.setOnClickListener(v -> {
            if (onClickListener != null) {

                if (isNote) {
                    onClickListener.onClickOption((Note) item, v);
                } else {
                  //  onClickListener.onClickOption((Folder) item, v);
                }
            }
        });*/

        // lock
        setImageViewLock(noteViewHolder.imageViewLock, item.isLocked());

        // tag
        setImageViewTagType(noteViewHolder.viewTag, item.getTag());

        // image
        // noteViewHolder.imageView.setImageResource(isNote ? R.drawable.ic_note : R.drawable.ic_folder);
    }

    private void setImageViewLock(ImageView imageView, boolean locked) {
        imageView.setImageResource(locked ? R.drawable.ic_lock_green : R.drawable.ic_lock_open);
    }

    private void setImageViewTagType(View view, TagType tagType) {
        switch (tagType) {
            case RED:
                // imageView.setImageResource(R.drawable.ic_tag_red);
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.red_color));
                break;
            default:
                // imageView.setImageResource(R.drawable.ic_tag_white);
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    public Item findItem(int position) {
        return getItem(position);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        View item;
        ImageView imageView;
        View viewTag;
        ImageView imageViewLock;
        ImageView viewOption;
        TextView textView;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            imageViewLock = itemView.findViewById(R.id.image_view_lock);
            viewTag = itemView.findViewById(R.id.viewTag);
            viewOption = itemView.findViewById(R.id.bt_option);
            item = itemView.findViewById(R.id.itemView);
        }
    }

    public interface OnClickListener {
        void onClickNote(Note note);

        void onClickFolder(Folder folder);

        void onClickOption(Note note, View view);

        void onClickOption(Folder folder, View view);
    }
}
