package com.upcode.annotations.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class EntriesCount {

    private static final String PREFS_NAME = "PREFS_ENTRIES_COUNT";
    private static final String ENTRIES_COUNT_KEY = "ENTRIES_COUNT_KEY";

    private SharedPreferences sharedPreferences;

    public static EntriesCount from(Context context) {
        return new EntriesCount(context);
    }

    private EntriesCount(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void increase() {
        if (sharedPreferences != null) {
            int count = getCount();

            // clear
            if (count >= 3) {
                count = 0;
            }

            sharedPreferences.edit().putInt(ENTRIES_COUNT_KEY, (count + 1)).apply();
            return;
        }

        throw new NullPointerException("SharedPreferences is Null");
    }

    public int getCount() {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(ENTRIES_COUNT_KEY, 0);
        }

        throw new NullPointerException("SharedPreferences is Null");
    }

}
