package com.example.musicmate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleObserver;

import com.example.musicmate.databinding.ActivityAfterloginBinding;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

public class afterlogin extends AppCompatActivity implements View.OnClickListener {

    NotificationManager notificationManager;

    ActionBar actionBar;
    ActivityAfterloginBinding binding;
    public static String currentScreen;

    public static ImageView goToPlayingActivity;


    Fragment searchFrag = new SearchFrag();
    public Fragment playlistsFrag = new PlaylistsFrag();
    Fragment settingsFrag = new SettingsFrag();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        startService(new Intent(this, KillNotificationService.class));


        MusicPlayer.initiate(getApplicationContext());

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


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }


    }// end of onCreate

    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(createNotification.CHANNEL_ID,
                    "playback notification", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    private void replaceFragment(Fragment frag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, frag);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        if (view == goToPlayingActivity) {
            Intent intent = new Intent(afterlogin.this, playing.class);
            startActivity(intent);
        }
    }
}