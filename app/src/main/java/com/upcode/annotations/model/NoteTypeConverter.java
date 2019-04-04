package com.upcode.annotations.model;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class NoteTypeConverter {
    @TypeConverter
    public static List<Note> toNotes(String notesStr){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Note>>() {}.getType();
        return  gson.fromJson(notesStr, type);
    }

    @TypeConverter
    public static String toString (List<Note> notes){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Note>>() {}.getType();
        return gson.toJson(notes, type);
    }
}
