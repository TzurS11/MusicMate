package com.example.musicmate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.util.Log;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class playing extends AppCompatActivity {

    ActionBar actionBar;
    private static ImageView coverImage;
    public static Boolean isCreated = false;
    private static SeekBar seekBar;
    public static Boolean isHoldingSeekbar = false;
    private int toSeekTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        actionBar = getSupportActionBar();
        actionBar.hide();

        coverImage = findViewById(R.id.coverImage);
        seekBar = findViewById(R.id.seekBar);

        isCreated = true;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (isHoldingSeekbar = true) {
                    toSeekTime = i;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isHoldingSeekbar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isHoldingSeekbar = false;
                MusicPlayer.player.seekTo(toSeekTime);
            }
        });


        changeDetailsFromMusicPlayer();

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                long position = MusicPlayer.player.getCurrentPosition();
//                Log.w("tag",String.valueOf(position));
                long duration = MusicPlayer.player.getDuration();
                seekBar.setMax((int) duration);
                if (!isHoldingSeekbar) {

                    seekBar.setProgress((int) position);
                    isHoldingSeekbar = false;
                }

//                String time = String.format("%02d:%02d:%02d",
//                        TimeUnit.MILLISECONDS.toHours(millis),
//                        TimeUnit.MILLISECONDS.toMinutes(millis) -
//                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
//                        TimeUnit.MILLISECONDS.toSeconds(millis) -
//                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//                millis = MusicPlayer.player.getDuration();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
        isHoldingSeekbar = false;
    }

    public static void changeDetailsFromMusicPlayer() {
        long duration = MusicPlayer.player.getDuration();
        seekBar.setMax((int) duration);

        if (MusicPlayer.currentSong.getCoverImg() != null) {
            Picasso.get().load(MusicPlayer.currentSong.getCoverImg()).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(coverImage);
        } else {
            coverImage.setImageResource(R.drawable.songplaceholder);
        }
    }

    public static void changeDetailsFromGivenSong() {

    }
}