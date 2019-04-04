package com.upcode.annotations;

import android.os.Bundle;
import android.view.MenuItem;

import com.upcode.annotations.fragments.MenuFragmentListener;
import com.upcode.annotations.fragments.NotesFragment;
import com.upcode.annotations.fragments.TasksFragment;
import com.upcode.annotations.fragments.ThemesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private MenuFragmentListener menuFragmentListener;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            startFragment(Fragments.NOTES);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_notes:
                startFragment(Fragments.NOTES);
                break;
            case R.id.item_tasks:
                startFragment(Fragments.TASKS);
                break;
            case R.id.item_themes:
                startFragment(Fragments.THEMES);
                break;
        }
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
            case TASKS:
                fragment = new TasksFragment();
                break;
            case THEMES:
                fragment = new ThemesFragment();
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
        TASKS("Tasks"),
        THEMES("Themes");

        private String tag;

        Fragments(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }
}
