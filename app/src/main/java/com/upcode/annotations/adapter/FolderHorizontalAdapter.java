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

public class FolderHorizontalAdapter extends ListAdapter<Folder, FolderHorizontalAdapter.ViewHolder> {

    private Context context;
    private FolderListener folderListener;

    public FolderHorizontalAdapter(Context context, FolderListener folderListener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.folderListener = folderListener;
    }

    private static DiffUtil.ItemCallback<Folder> DIFF_CALLBACK = new DiffUtil.ItemCallback<Folder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Folder oldItem, @NonNull Folder newItem) {
            return (oldItem.getId() == newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Folder oldItem, @NonNull Folder newItem) {
            return (oldItem.getTitle().equals(newItem.getTitle())) &&
                    (oldItem.getPassword().equals(newItem.getPassword())) &&
                    (oldItem.isLocked() == newItem.isLocked()) &&
                    (oldItem.isTemporarilyUnlocked() == newItem.isTemporarilyUnlocked()) &&
                    (oldItem.getRegistered() == newItem.getRegistered());
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_folder_hor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Folder folder = getItem(position);
        holder.textView.setText(folder.getTitle());
        holder.ivLock.setVisibility(folder.isLocked() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener((v -> {
            if (folderListener != null) {
                folderListener.onClickFolder(folder, v);
            }
        }));
        holder.itemView.setOnLongClickListener(v -> {
            if (folderListener != null) {
                folderListener.onLongClickFolder(folder, v);
            }
            return true;
        });
        holder.btOption.setOnClickListener(v -> {
            if (folderListener != null) {
                folderListener.onClickOption(folder, v);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView ivLock;
        View btOption;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.textView);
            this.btOption = itemView.findViewById(R.id.bt_option);
            this.ivLock = itemView.findViewById(R.id.iv_lock);
        }
    }
}
