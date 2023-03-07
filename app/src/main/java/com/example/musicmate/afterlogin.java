package com.example.musicmate;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.musicmate.databinding.ActivityAfterloginBinding;
import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor;
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.github.kotvertolet.youtubejextractor.models.youtube.videoData.YoutubeVideoData;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

public class afterlogin extends AppCompatActivity  implements View.OnClickListener{
    ActionBar actionBar;
    ActivityAfterloginBinding binding;
    public String currentScreen;

    public static ImageView goToPlayingActivity;


    Fragment searchFrag = new SearchFrag();
    public Fragment playlistsFrag = new PlaylistsFrag();
    Fragment settingsFrag = new SettingsFrag();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new MusicPlayer(getApplicationContext());

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

        goToPlayingActivity = findViewById(R.id.goToPlayingSong);
        goToPlayingActivity.setOnClickListener(this);
    }

    private void replaceFragment(Fragment frag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, frag);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        if(view == goToPlayingActivity){
            Intent intent = new Intent(afterlogin.this,playing.class);
            startActivity(intent);
        }
    }
}