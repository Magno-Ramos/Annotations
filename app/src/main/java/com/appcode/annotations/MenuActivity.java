package com.appcode.annotations;

import android.os.Bundle;
import android.view.MenuItem;

import com.appcode.annotations.fragments.NotesFragment;
import com.appcode.annotations.fragments.TasksFragment;
import com.appcode.annotations.fragments.ThemesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MenuActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            startFragment(new NotesFragment());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_notes:
                startFragment(new NotesFragment());
                break;
            case R.id.item_tasks:
                startFragment(new TasksFragment());
                break;
            case R.id.item_themes:
                startFragment(new ThemesFragment());
                break;
        }
        return true;
    }

    private void startFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
