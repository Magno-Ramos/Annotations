package com.upcode.annotations.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.upcode.annotations.dao.FolderDao;
import com.upcode.annotations.database.AppDatabase;
import com.upcode.annotations.model.Folder;

import java.util.List;

import androidx.lifecycle.LiveData;

public class FolderRepository {

    private FolderDao folderDao;

    public static FolderRepository from(Context context) {
        return new FolderRepository(context);
    }

    private FolderRepository(Context context) {
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        folderDao = appDatabase.folderDao();
    }

    public LiveData<List<Folder>> getAll() {
        return folderDao.findAll();
    }

    public void insert(Folder folder) {
        new InsertFolderTask(folderDao).execute(folder);
    }

    public void renameFolder(Folder folder, String newName) {
        Folder folderUpdated = folder.doClone();
        folderUpdated.setTitle(newName);

        update(folderUpdated);
    }

    public void lockFolder(Folder folder, String password) {
        Folder folderUpdated = folder.doClone();
        folderUpdated.setLocked(true);
        folderUpdated.setPassword(password);

        update(folderUpdated);
    }

    public void unlockFolder(Folder folder) {
        Folder folderUpdated = folder.doClone();
        folderUpdated.setLocked(false);
        folderUpdated.setPassword("");

        update(folderUpdated);
    }

    private void update(Folder folder) {
        new UpdateFolderTask(folderDao).execute(folder);
    }

    public void delete(Folder folder) {
        new DeleteFolderTask(folderDao).execute(folder);
    }

    private static class InsertFolderTask extends AsyncTask<Folder, Void, Void> {

        private FolderDao folderDao;

        private InsertFolderTask(FolderDao folderDao) {
            this.folderDao = folderDao;
        }

        @Override
        protected Void doInBackground(Folder... folders) {
            folderDao.insert(folders[0]);
            return null;
        }
    }

    private static class UpdateFolderTask extends AsyncTask<Folder, Void, Void> {

        private FolderDao folderDao;

        private UpdateFolderTask(FolderDao folderDao) {
            this.folderDao = folderDao;
        }

        @Override
        protected Void doInBackground(Folder... folders) {
            folderDao.update(folders[0]);
            return null;
        }
    }

    private static class DeleteFolderTask extends AsyncTask<Folder, Void, Void> {

        private FolderDao folderDao;

        private DeleteFolderTask(FolderDao folderDao) {
            this.folderDao = folderDao;
        }

        @Override
        protected Void doInBackground(Folder... folders) {
            folderDao.delete(folders[0]);
            return null;
        }
    }
}
