package com.appcode.annotations.model;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    public String title;

    private boolean isLocked;

    private String password;

    private TagType tag;

    private long registered;

    Item() {
        this.title = "";
        this.isLocked = false;
        this.password = "";
        this.tag = TagType.NONE;
        this.registered = System.currentTimeMillis();
    }

    Item(Parcel in) {
        id = in.readInt();
        title = in.readString();
        isLocked = in.readByte() != 0;
        password = in.readString();
        registered = in.readLong();
        tag = TagType.findTag(in.readInt());
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
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

    public TagType getTag() {
        return tag;
    }

    public void setTag(TagType tag) {
        this.tag = tag;
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
        dest.writeInt(tag.getTag());
    }
}
