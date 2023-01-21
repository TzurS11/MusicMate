package com.example.musicmate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.musicmate.databinding.ActivityAfterloginBinding;
import com.example.musicmate.databinding.ActivityMainBinding;

public class afterlogin extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    ActivityAfterloginBinding binding;
    String currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAfterloginBinding.inflate(getLayoutInflater());





        setContentView(binding.getRoot());
        replaceFragment(new SearchFrag());
        currentScreen = "search";


        actionBar = getSupportActionBar();
        actionBar.hide();


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.search:
                    currentScreen = "search";
                    replaceFragment(new SearchFrag());
                    break;
                case R.id.playlists:
                    currentScreen = "playlists";
                    replaceFragment(new PlaylistsFrag());
                    break;
                case R.id.settings:
                    currentScreen = "settings";
                    replaceFragment(new SettingsFrag());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment frag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, frag);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
    }
}