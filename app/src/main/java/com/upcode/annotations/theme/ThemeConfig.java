package com.upcode.annotations.theme;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.upcode.annotations.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.content.ContextCompat;

public class ThemeConfig {

    private static final String TAG = "AppMain";

    private Context context;

    private ThemeConfig(Context context) {
        this.context = context;
    }

    public static ThemeConfig get(Context context) {
        return new ThemeConfig(context);
    }

    public void configView(View view, Theme theme) {
        Log.d(TAG, "Theme configView");
        if (view instanceof BottomNavigationView) {
            configBottomNavigationView((BottomNavigationView) view, theme);
        }
    }

    private void configBottomNavigationView(BottomNavigationView navigationView, Theme theme) {
        switch (theme) {
            case LIGHT:
                Log.d(TAG, "configBottomNavigationView LIGHT");
                navigationView.setItemIconTintList(ContextCompat.getColorStateList(context, R.color.item_text_color_light));
                navigationView.setItemTextColor(ContextCompat.getColorStateList(context, R.color.item_text_color_light));
                break;
            case DARK:
                navigationView.setItemIconTintList(ContextCompat.getColorStateList(context, R.color.item_text_color_dark));
                navigationView.setItemTextColor(ContextCompat.getColorStateList(context, R.color.item_text_color_dark));
                Log.d(TAG, "configBottomNavigationView DARK");
                break;
        }
    }
}
