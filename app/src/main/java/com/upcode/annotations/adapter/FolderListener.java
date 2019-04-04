package com.upcode.annotations.adapter;

import android.view.View;

import com.upcode.annotations.model.Folder;

public interface FolderListener {

    void onClickFolder(Folder folder, View view);

    void onClickOption(Folder folder, View view);

    void onLongClickFolder(Folder folder, View view);

}
