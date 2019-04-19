package com.upcode.annotations.model;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "folder_table")
public class Folder implements Parcelable {

    public static final int MIN_PASSWORD = 2;
    public static final int MAX_PASSWORD = 8;

    public static final int MIN_TITLE_LENGTH = 2;
    public static final int MAX_TITLE_LENGTH = 25;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String title;

    private boolean isLocked;

    private String password;

    private long registered;

    @Ignore
    private boolean temporarilyUnlocked;

    public Folder() {
        this.id = 0;
        this.title = "";
        this.isLocked = false;
        this.password = "";
        this.registered = System.currentTimeMillis();
        this.temporarilyUnlocked = false;
    }

    protected Folder(Parcel in) {
        id = in.readInt();
        title = in.readString();
        isLocked = in.readByte() != 0;
        password = in.readString();
        registered = in.readLong();
        temporarilyUnlocked = in.readByte() != 0;
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getRegistered() {
        return registered;
    }

    public void setRegistered(long registered) {
        this.registered = registered;
    }

    public boolean isTemporarilyUnlocked() {
        return temporarilyUnlocked;
    }

    public void setTemporarilyUnlocked(boolean temporarilyUnlocked) {
        this.temporarilyUnlocked = temporarilyUnlocked;

        // 2 minute
        Handler handler = new Handler();
        handler.postDelayed(() -> setTemporarilyUnlocked(false), 2 * 60 * 1000);
    }

    public Folder doClone() {
        Folder folder = new Folder();
        folder.setId(id);
        folder.setRegistered(registered);
        folder.setTitle(title);
        folder.setLocked(isLocked);
        folder.setPassword(password);
        folder.setTemporarilyUnlocked(temporarilyUnlocked);
        return folder;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeByte((byte) (isLocked ? 1 : 0));
        dest.writeString(password);
        dest.writeLong(registered);
        dest.writeByte((byte) (temporarilyUnlocked ? 1 : 0));
    }
}
