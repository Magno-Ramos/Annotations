package com.upcode.annotations.model;

import androidx.room.TypeConverter;

public class TagTypeConverter {

    @TypeConverter
    public static TagType fromInt(int tag) {
        return TagType.findTag(tag);
    }

    @TypeConverter
    public static int toInt(TagType tagType) {
        return tagType.getTag();
    }
}
