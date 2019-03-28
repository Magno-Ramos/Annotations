package com.appcode.annotations.viewmodel;

import android.app.Application;

import com.appcode.annotations.model.Folder;
import com.appcode.annotations.repository.FolderRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class FoldersViewModel extends AndroidViewModel {

    private FolderRepository folderRepository;
    private LiveData<List<Folder>> allFolders;

    public FoldersViewModel(@NonNull Application application) {
        super(application);
        folderRepository = new FolderRepository(application);
        allFolders = folderRepository.getAll();
    }

    public void insert(Folder folder) {
        folderRepository.insert(folder);
    }

    public void update(Folder folder) {
        folderRepository.update(folder);
    }

    public void delete(Folder folder) {
        folderRepository.delete(folder);
    }

    public LiveData<List<Folder>> getAllFolders() {
        return allFolders;
    }
}
