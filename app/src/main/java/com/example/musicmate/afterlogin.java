package com.example.musicmate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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


        createChannel();

        if (MusicPlayer.player.getMediaItemCount() != 0) {
            goToPlayingActivity.setVisibility(View.VISIBLE);

        } else {
            goToPlayingActivity.setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if (data != null) {

            String[] path = data.getPath().substring(1).split("/");

            if (path[0] == "playlist") {
                FirebaseDatabase.getInstance().getReference("Playlist").child(path[1]).child(path[2]).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Playlist playlist = snapshot.getValue(Playlist.class);
                        Intent intent = new Intent(afterlogin.this, inPlaylist.class);
                        intent.putExtra("playlist", playlist);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
//            if(data.getQueryParameter("type") == "song"){
//
//            }
        }


    }// end of onCreate

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicPlayer.player.getMediaItemCount() != 0) {
            afterlogin.goToPlayingActivity.setVisibility(View.VISIBLE);
            if (playing.isCreated) {
                playing.isHoldingSeekbar = false;
            }
        } else {
            afterlogin.goToPlayingActivity.setVisibility(View.GONE);
        }
    }

    public void createChannel() {
        NotificationChannel channel = new NotificationChannel(createNotification.CHANNEL_ID,
                "playback notification", NotificationManager.IMPORTANCE_LOW);



        notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
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