package com.example.musicmate;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

public class MusicPlayer extends AppCompatActivity {
    public static ExoPlayer player;

    public MusicPlayer(Context context) {
        player = new ExoPlayer.Builder(context.getApplicationContext()).build();
    }
}
