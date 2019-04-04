package com.upcode.annotations.dao;

import com.upcode.annotations.model.Note;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Note note);

    @Delete
    void delete(Note note);

    @Update
    void update(Note note);

    @Query("SELECT * FROM note_table WHERE folder_id = 0")
    LiveData<List<Note>> findAll();

    @Query("SELECT * FROM note_table WHERE folder_id = :id")
    LiveData<List<Note>> findAllByFolderId(int id);

    @Query("SELECT * FROM note_table ORDER BY last_modification ASC LIMIT 5")
    LiveData<List<Note>> findRecentFilesChanged();

    @Query("SELECT * FROM note_table WHERE id = :id")
    Note findById(long id);

    @Query("SELECT * FROM note_table WHERE folder_id = 0")
    LiveData<List<Note>> findAllNotesOutsideFolder();
}
