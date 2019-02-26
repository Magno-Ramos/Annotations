package com.appcode.annotations.database;

import android.content.Context;
import android.os.AsyncTask;

import com.appcode.annotations.dao.FolderDao;
import com.appcode.annotations.dao.NoteDao;
import com.appcode.annotations.model.Folder;
import com.appcode.annotations.model.Note;
import com.appcode.annotations.model.TagType;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class, Folder.class}, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    public abstract FolderDao folderDao();

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new WelcomeNoteTask(instance).execute();
        }
    };

    private static class WelcomeNoteTask extends AsyncTask<Void, Void, Void> {

        private NoteDao noteDao;
        private FolderDao folderDao;

        WelcomeNoteTask(NoteDatabase noteDatabase) {
            this.folderDao = noteDatabase.folderDao();
            this.noteDao = noteDatabase.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Folder folder = new Folder();
            folder.setTitle("Bem vindo");

            Note note = new Note();
            note.setTitle("Bem vindo");
            note.setMessage("<div style=\"text-align: center;\"><b>Seja bem ao nosso aplicativo.&nbsp;</b></div><div style=\"text-align: center;\"><br></div>&nbsp;Salve suas anotações e se organize facilmente \uD83D\uDE00");
            note.setTag(TagType.GREEN);

            long id = folderDao.insert(folder);
            note.setFolderId(id);
            noteDao.insert(note);
            return null;
        }
    }
}
