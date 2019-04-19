package com.upcode.annotations.database;

import android.content.Context;
import android.os.AsyncTask;

import com.upcode.annotations.dao.FolderDao;
import com.upcode.annotations.dao.NoteDao;
import com.upcode.annotations.model.Folder;
import com.upcode.annotations.model.Note;
import com.upcode.annotations.model.TagType;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class, Folder.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract NoteDao noteDao();

    public abstract FolderDao folderDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "note_database")
                    .addMigrations(new Migration(1, 2) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                            // Since we didn't alter the table, there's nothing else to do here.
                        }
                    })
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // new WelcomeNoteTask(instance, context.getString(R.string.welcome_title), context.getString(R.string.welcome_message)).execute();
                        }
                    })
                    .build();
        }
        return instance;
    }

    private static class MigrateTask extends AsyncTask<Void, Void, Void> {

        private NoteDao noteDao;

        private MigrateTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }

    private static class WelcomeNoteTask extends AsyncTask<Void, Void, Void> {

        private NoteDao noteDao;
        private String welcomeTitle;
        private String welcomeMessage;

        WelcomeNoteTask(AppDatabase appDatabase, String welcomeTitle, String welcomeMessage) {
            this.noteDao = appDatabase.noteDao();
            this.welcomeTitle = welcomeTitle;
            this.welcomeMessage = welcomeMessage;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Note note = new Note();
            note.setTitle(welcomeTitle);
            note.setMessage(welcomeMessage);
            note.setTag(TagType.GREEN);

            noteDao.insert(note);
            return null;
        }
    }
}
