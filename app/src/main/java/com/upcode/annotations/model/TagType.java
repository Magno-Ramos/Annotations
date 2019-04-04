package com.upcode.annotations.model;

public enum TagType {

    NONE(0),
    RED(1),
    BLUE(2),
    YELLOW(3),
    BLACK(4),
    GREEN(5),
    BROWN(6),
    PINK(7);


    private int tag;

    TagType(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public static TagType findTag(int tag) {
        switch (tag) {
            case 1:
                return RED;
            case 2:
                return BLUE;
            case 3:
                return YELLOW;
            case 4:
                return BLACK;
            case 5:
                return GREEN;
            case 6:
                return BROWN;
            case 7:
                return PINK;
            default:
                return NONE;

        }
    }
}
