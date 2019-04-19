package com.upcode.annotations;

import android.content.Context;

import com.upcode.annotations.dao.NoteDao;
import com.upcode.annotations.database.AppDatabase;
import com.upcode.annotations.model.Note;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SimpleEntityReadWriteTest {

    private NoteDao noteDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        noteDao = db.noteDao();
    }

    @After
    public void closeDb(){
        db.close();
    }

    @Test
    public void writeUserAndReadInList(){
        Note note = new Note();
        note.setId(1);
        note.setTitle("george");
        noteDao.insert(note);

        Note byId = noteDao.findNoteById(note.getId());
        assertThat(byId, equalTo(note));
    }
}