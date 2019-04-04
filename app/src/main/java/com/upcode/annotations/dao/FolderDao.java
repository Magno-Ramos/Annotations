package com.upcode.annotations.dao;

import com.upcode.annotations.model.Folder;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

@Dao
public abstract class FolderDao {

    @Transaction
    public void delete(Folder folder) {
        deleteNotesByFolder(folder.getId());
        deleteFolder(folder);
    }

    @Insert
    public abstract long insert(Folder folder);

    @Update
    public abstract void update(Folder folder);

    @Delete
    abstract void deleteFolder(Folder folder);

    @Query("DELETE FROM note_table WHERE folder_id = :folderId")
    abstract void deleteNotesByFolder(int folderId);

    @Query("SELECT * FROM folder_table ORDER BY isLocked DESC")
    public abstract LiveData<List<Folder>> findAll();

    @Query("SELECT * FROM folder_table WHERE id = :folderId")
    public abstract LiveData<Folder> findById(int folderId);
}
