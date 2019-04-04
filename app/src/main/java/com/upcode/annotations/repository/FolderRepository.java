package com.upcode.annotations.repository;

import android.app.Application;
import android.os.AsyncTask;

import com.upcode.annotations.dao.FolderDao;
import com.upcode.annotations.database.NoteDatabase;
import com.upcode.annotations.model.Folder;

import java.util.List;

import androidx.lifecycle.LiveData;

public class FolderRepository {

    private FolderDao folderDao;
    private LiveData<List<Folder>> allFolders;

    public FolderRepository(Application application) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        folderDao = noteDatabase.folderDao();
        allFolders = folderDao.findAll();
    }

    public LiveData<List<Folder>> getAll() {
        return allFolders;
    }

    public void insert(Folder folder) {
        new InsertFolderTask(folderDao).execute(folder);
    }

    public void update(Folder folder) {
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
