package com.upcode.annotations.theme;

import com.upcode.annotations.R;

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
