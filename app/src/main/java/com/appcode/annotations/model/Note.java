package com.appcode.annotations.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "note_table")
@TypeConverters(TagTypeConverter.class)
public class Note implements Parcelable {

    public static final int MIN_LENGTH = 2;
    public static final int MAX_LENGTH = 25;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String title;

    private TagType tag;

    private long registered;

    @ColumnInfo(name = "last_modification")
    private long lastModification;

    private String message;

    @ColumnInfo(name = "folder_id")
    private long folderId;

    public Note() {
        this.id = 0;
        this.message = "";
        this.title = "";
        this.tag = TagType.NONE;
        this.registered = System.currentTimeMillis();
        this.lastModification = registered;
        this.folderId = 0;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        registered = in.readLong();
        lastModification = in.readLong();
        message = in.readString();
        folderId = in.readLong();
        tag = TagType.findTag(in.readInt());
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
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

    public TagType getTag() {
        return tag;
    }

    public void setTag(TagType tag) {
        this.tag = tag;
    }

    public long getRegistered() {
        return registered;
    }

    public void setRegistered(long registered) {
        this.registered = registered;
    }

    public long getLastModification() {
        return lastModification;
    }

    public void setLastModification(long lastModification) {
        this.lastModification = lastModification;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getFolderId() {
        return folderId;
    }

    public void setFolderId(long folderId) {
        this.folderId = folderId;
    }

    public Note doClone() {
        Note c = new Note();
        c.setId(getId());
        c.setTitle(getTitle());
        c.setFolderId(getFolderId());
        c.setMessage(getMessage());
        c.setLastModification(getLastModification());
        c.setTag(getTag());
        c.setRegistered(getRegistered());
        return c;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeLong(registered);
        dest.writeLong(lastModification);
        dest.writeString(message);
        dest.writeLong(folderId);
        dest.writeInt(tag.getTag());
    }
}
