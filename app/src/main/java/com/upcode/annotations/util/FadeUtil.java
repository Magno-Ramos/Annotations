package com.upcode.annotations.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class FadeUtil {

    public static void show(View view, long duration) {
        if (view != null) {
            view.animate()
                    .alpha(1)
                    .alphaBy(0)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            view.setVisibility(View.VISIBLE);
                        }
                    })
                    .start();
        }
    }

    public static void hide(View view, long duration) {
        if (view != null) {
            view.animate()
                    .alpha(0)
                    .alphaBy(1)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            view.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

}
