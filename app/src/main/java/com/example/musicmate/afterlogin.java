package com.example.musicmate;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.musicmate.databinding.ActivityAfterloginBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class afterlogin extends AppCompatActivity {
    ActionBar actionBar;
    ActivityAfterloginBinding binding;
    public String currentScreen;


    Fragment searchFrag = new SearchFrag();
    public Fragment playlistsFrag = new PlaylistsFrag();
    Fragment settingsFrag = new SettingsFrag();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        new MusicPlayer(getApplicationContext());
//        MediaItem mediaItem = MediaItem.fromUri("https://cdn.discordapp.com/attachments/966001253991735326/1078424589429919906/door.ogg");
//        MusicPlayer.player.addMediaItem(mediaItem);
//        MusicPlayer.player.prepare();
//        MusicPlayer.player.play();



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
                    replaceFragment(searchFrag);
                    break;
                case R.id.playlists:
                    currentScreen = "playlists";
                    replaceFragment(playlistsFrag);
                    break;
                case R.id.settings:
                    currentScreen = "settings";
                    replaceFragment(settingsFrag);
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

}