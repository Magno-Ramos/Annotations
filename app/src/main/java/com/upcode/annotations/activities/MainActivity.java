package com.upcode.annotations.activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.upcode.annotations.R;
import com.upcode.annotations.RewardedVideoAdCallback;
import com.upcode.annotations.fragments.MenuFragmentListener;
import com.upcode.annotations.fragments.NotesFragment;
import com.upcode.annotations.preferences.EntriesCount;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int entriesCountToShowAds = 3;

    private RewardedVideoAd mRewardedVideoAd;

    private MenuFragmentListener menuFragmentListener;
    private BottomNavigationView bottomNavigationView;

    private int currentItemId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            startFragment(Fragments.NOTES);
        }

        EntriesCount entriesCount = EntriesCount.from(this);
        entriesCount.increase();

        if (entriesCount.getCount() >= entriesCountToShowAds) {
            showAds();
        }
    }

    private void showAds() {
        MobileAds.initialize(this, getString(R.string.ads_id));

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdCallback() {
            @Override
            public void onRewardedVideoAdLoaded() {
                super.onRewardedVideoAdLoaded();
                mRewardedVideoAd.show();
            }
        });
        mRewardedVideoAd.loadAd(getString(R.string.ads_reward_id), new AdRequest.Builder().build());
    }

    @Override
    public void onResume() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mRewardedVideoAd != null)
            mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (currentItemId > 0 && (currentItemId == item.getItemId())) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.item_notes:
                startFragment(Fragments.NOTES);
                break;
        }

        currentItemId = item.getItemId();
        return true;
    }

    private void startFragment(Fragments type) {
        Fragment fragment;

        switch (type) {
            case NOTES:
            default:
                NotesFragment notesFragment = new NotesFragment();
                menuFragmentListener = notesFragment;
                fragment = notesFragment;
                break;

        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitNowAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (menuFragmentListener != null && !menuFragmentListener.onBackPressed()) {
            return;
        }

        if (bottomNavigationView.getSelectedItemId() != bottomNavigationView.getMenu().getItem(0).getItemId()) {
            bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(0).getItemId());
            return;
        }

        super.onBackPressed();
    }

    private enum Fragments {
        NOTES("Notes"),
        SETTINGS("");

        private String tag;

        Fragments(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }
}
