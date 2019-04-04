package com.upcode.annotations.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

import com.upcode.annotations.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FabMenuController implements View.OnClickListener {

    private static final long ANIM_TIME = 200;

    private Context context;
    private ToggleCallback toggleCallback;
    private FabListener fabListener;

    private boolean isOpen = false;

    private View viewLayerFabMenu;
    private FloatingActionButton floatingActionButton;
    private FloatingActionButton fabFolder;
    private FloatingActionButton fabNote;

    public FabMenuController(Context context, View viewLayerFabMenu) {
        this.context = context;
        this.viewLayerFabMenu = viewLayerFabMenu;
        this.viewLayerFabMenu.setVisibility(View.INVISIBLE);
        this.viewLayerFabMenu.setOnClickListener(onClickLayer());
    }

    public void setFabMenuToggle (FloatingActionButton floatingActionButton){
        this.floatingActionButton = floatingActionButton;
        this.floatingActionButton.setOnClickListener(this);
    }

    public void setFabFolder(FloatingActionButton fabFolder) {
        this.fabFolder = fabFolder;
        this.fabFolder.setOnClickListener(onClickFabFolder());
    }

    public void setFabNote(FloatingActionButton fabNote) {
        this.fabNote = fabNote;
        this.fabNote.setOnClickListener(onClickFabNote());
    }

    private View.OnClickListener onClickLayer() {
        return v -> hideMenu();
    }

    private View.OnClickListener onClickFabNote() {
        return v -> {
            if (fabListener != null) {
                fabListener.onClickFabNote(v);
            }

            hideMenu();
        };
    }

    private View.OnClickListener onClickFabFolder() {
        return v -> {
            if (fabListener != null) {
                fabListener.onClickFabFolder(v);
            }

            hideMenu();
        };
    }

    @Override
    public void onClick(View v) {
        toggleMenu();
    }

    private void toggleMenu() {
        if (!isOpen) {
            showMenu();
        } else {
            hideMenu();
        }
    }

    private void showMenu() {
        // rotate
        RotateAnimation rotateAnimation = new RotateAnimation(0, -45, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(ANIM_TIME);
        rotateAnimation.setInterpolator(new OvershootInterpolator());
        rotateAnimation.setFillAfter(true);

        // up fab
        final FrameLayout.LayoutParams paramsFolder = (FrameLayout.LayoutParams) fabFolder.getLayoutParams();
        final FrameLayout.LayoutParams paramsNote = (FrameLayout.LayoutParams) fabNote.getLayoutParams();

        int from = (int) context.getResources().getDimension(R.dimen.fab_default_margin);
        int to = (int) context.getResources().getDimension(R.dimen.fab_folder_margin);
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.addUpdateListener(valueAnimator -> {
            paramsFolder.bottomMargin = (Integer) valueAnimator.getAnimatedValue();
            paramsNote.bottomMargin = (int) (((Integer) valueAnimator.getAnimatedValue()) * (1.8));
            fabFolder.requestLayout();
            fabNote.requestLayout();

        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                fabFolder.setVisibility(View.VISIBLE);
                fabNote.setVisibility(View.VISIBLE);
            }
        });
        animator.setInterpolator(new OvershootInterpolator());
        animator.setDuration(ANIM_TIME);

        // layer
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(ANIM_TIME);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                viewLayerFabMenu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewLayerFabMenu.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewLayerFabMenu.startAnimation(alphaAnimation);
        fabNote.startAnimation(alphaAnimation);
        fabFolder.startAnimation(alphaAnimation);

        animator.start();
        floatingActionButton.startAnimation(rotateAnimation);

        isOpen = !isOpen;

        if (toggleCallback != null) {
            toggleCallback.onOpen();
        }
    }

    private void hideMenu() {
        // rotation
        RotateAnimation rotateAnimation = new RotateAnimation(-45, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(ANIM_TIME);
        rotateAnimation.setInterpolator(new OvershootInterpolator());
        rotateAnimation.setFillAfter(true);

        // up fab
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fabFolder.getLayoutParams();
        final FrameLayout.LayoutParams paramsNote = (FrameLayout.LayoutParams) fabNote.getLayoutParams();

        int from = (int) context.getResources().getDimension(R.dimen.fab_folder_margin);
        int to = (int) context.getResources().getDimension(R.dimen.fab_default_margin);
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.addUpdateListener(valueAnimator -> {
            params.bottomMargin = (Integer) valueAnimator.getAnimatedValue();
            paramsNote.bottomMargin = (int) (((Integer) valueAnimator.getAnimatedValue()) * (1.8));
            fabFolder.requestLayout();
            fabNote.requestLayout();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fabFolder.setVisibility(View.GONE);
                fabNote.setVisibility(View.GONE);

            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(ANIM_TIME);

        // layer
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(ANIM_TIME);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewLayerFabMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewLayerFabMenu.startAnimation(alphaAnimation);
        fabNote.startAnimation(alphaAnimation);
        fabFolder.startAnimation(alphaAnimation);

        animator.start();
        floatingActionButton.startAnimation(rotateAnimation);

        isOpen = !isOpen;
        if (toggleCallback != null) {
            toggleCallback.onHide();
        }
    }

    public void setFabListener(FabListener fabListener) {
        this.fabListener = fabListener;
    }

    public boolean isOpened() {
        return isOpen;
    }

    public void dismiss() {
        this.hideMenu();
    }

    public interface ToggleCallback {
        void onOpen();

        void onHide();
    }

    public interface FabListener {
        void onClickFabFolder(View view);

        void onClickFabNote(View view);
    }
}
