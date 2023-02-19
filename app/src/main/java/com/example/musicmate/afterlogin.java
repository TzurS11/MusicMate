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

public class afterlogin extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    ActivityAfterloginBinding binding;
    public String currentScreen;


    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public Boolean datasourceExist = false;


    Fragment searchFrag = new SearchFrag();
    Fragment playlistsFrag = new PlaylistsFrag();
    Fragment settingsFrag = new SettingsFrag();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .build()
//        );


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
//    public MediaPlayer getMediaPlayer(){
//        return mediaPlayer;
//    }


    @Override
    public void onClick(View view) {
    }
}