package com.appcode.annotations.theme;

import com.appcode.annotations.R;

import androidx.annotation.StyleRes;

public enum Theme {
    LIGHT(R.style.AppTheme),
    DARK(R.style.AppTheme_Dark);

    private int style;

    Theme(@StyleRes int style) {
        this.style = style;
    }

    public int getStyle() {
        return style;
    }
}
