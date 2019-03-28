package com.appcode.annotations.service;

import com.appcode.annotations.callback.Callback;
import com.appcode.annotations.model.Folder;
import com.appcode.annotations.repository.FolderRepository;

import java.util.List;

import androidx.lifecycle.LiveData;

public class FolderService implements Service<Folder> {

    private FolderRepository repository;

    public FolderService(FolderRepository repository) {
        this.repository = repository;
    }

    @Override
    public void fetchAll(Callback<LiveData<List<Folder>>> callback) {
        callback.onSuccess(this.repository.getAllFolders());
    }

    @Override
    public void save(Folder folder, Callback<Folder> callback) {
        this.repository.insert(folder);
    }

    @Override
    public void edit(Folder folder, Callback<Folder> callback) {

    }

    @Override
    public void delete(Folder folder, Callback<Folder> callback) {

    }
}
