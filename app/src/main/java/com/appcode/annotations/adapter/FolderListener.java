package com.appcode.annotations.adapter;

import android.view.View;

import com.appcode.annotations.model.Folder;

public interface FolderListener {

    void onClickFolder(Folder folder, View view);

    void onClickOption(Folder folder, View view);

    void onLongClickFolder(Folder folder, View view);

}
