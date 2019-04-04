package com.upcode.annotations.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.upcode.annotations.R;
import com.upcode.annotations.model.Folder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class FolderAdapter extends ListAdapter<Folder, FolderAdapter.FolderViewHolder> {

    private Context context;
    private FolderListener folderListener;

    public FolderAdapter(Context context, FolderListener folderListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.folderListener = folderListener;
    }

    private static final DiffUtil.ItemCallback<Folder> DIFF_CALLBACK = new DiffUtil.ItemCallback<Folder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Folder oldItem, @NonNull Folder newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Folder oldItem, @NonNull Folder newItem) {
            return oldItem.getTitle().trim().equals(newItem.getTitle().trim()) && oldItem.getPassword().equals(newItem.getPassword());
        }
    };

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder_m, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = getItem(position);
        holder.item.setOnClickListener(v -> {
            if (folderListener != null) {
                folderListener.onClickFolder(folder, holder.itemView);
            }
        });

        holder.item.setOnLongClickListener(v -> {
            if (folderListener != null){
                folderListener.onLongClickFolder(folder, v);
            }
            return true;
        });

        holder.viewOption.setOnClickListener(v -> {
            if (folderListener != null) {
                folderListener.onClickOption(folder, holder.viewOption);
            }
        });

        // title
        holder.textView.setText(folder.getTitle());

        // image
        holder.imageView.setImageResource(folder.isLocked() ? R.drawable.ic_folder_88_pass : R.drawable.ic_folder_88);

        // lock
        setImageViewLock(holder.imageViewLock, folder.isLocked());
    }

    private void setImageViewLock(ImageView imageView, boolean locked) {
        imageView.setImageResource(locked ? R.drawable.ic_lock_green : R.drawable.ic_lock_open);
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {

        View item;
        ImageView imageView;
        View viewTag;
        ImageView imageViewLock;
        ImageView viewOption;
        TextView textView;

        FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            imageViewLock = itemView.findViewById(R.id.image_view_lock);
            viewTag = itemView.findViewById(R.id.viewTag);
            viewOption = itemView.findViewById(R.id.bt_option);
            item = itemView.findViewById(R.id.itemView);
        }
    }
}
