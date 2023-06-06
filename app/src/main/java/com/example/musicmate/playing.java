package com.example.musicmate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.util.Log;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class playing extends AppCompatActivity implements View.OnClickListener {

    private static ImageView coverImage;
    private static TextView songName, songArtist, songDuration;
    public static ImageView controlPlayPause, controlNext, controlPast;
    public static Boolean isCreated = false;
    private static SeekBar seekBar;
    public static Boolean isHoldingSeekbar = false;
    private int toSeekTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        getSupportActionBar().hide();

        coverImage = findViewById(R.id.coverImage);
        seekBar = findViewById(R.id.seekBar);
        songName = findViewById(R.id.SongName);
        songName.setSelected(true);
        songArtist = findViewById(R.id.songArtist);
        songDuration = findViewById(R.id.songDuration);
        controlPlayPause = findViewById(R.id.controlPlayPause);
        controlPlayPause.setOnClickListener(this);
        controlPlayPause.setImageResource(R.drawable.pausefilled);
        controlNext = findViewById(R.id.controlNext);
        controlNext.setOnClickListener(this);
        controlPast = findViewById(R.id.controlPast);
        controlPast.setOnClickListener(this);


        if (MusicPlayer.queue.size() == 0) {
            controlNext.setAlpha(0.3f);
            controlNext.setEnabled(false);
        } else {
            controlNext.setAlpha(1f);
            controlNext.setEnabled(true);
        }
        if (MusicPlayer.recentlyPlayedSongs.size() == 0) {
            controlPast.setAlpha(0.3f);
            controlPast.setEnabled(false);
        } else {
            controlPast.setAlpha(1f);
            controlPast.setEnabled(true);
        }

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
            @SuppressLint("SetTextI18n")
            public void run() {
                long position = MusicPlayer.player.getCurrentPosition();
//                Log.w("tag",String.valueOf(position));
                long duration = MusicPlayer.player.getDuration();
                seekBar.setMax((int) duration);
                if (!isHoldingSeekbar) {

                    seekBar.setProgress((int) position);
                    isHoldingSeekbar = false;
                }
                long millis = position;
                @SuppressLint("DefaultLocale") String currentTime = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                millis = MusicPlayer.player.getDuration();
                @SuppressLint("DefaultLocale") String songLength = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millis) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                songDuration.setText(currentTime + " / " + songLength);
                handler.postDelayed(this, 1000);
            }
        }, 1000);
        isHoldingSeekbar = false;

        MusicPlayer.player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == Player.STATE_READY) {
                    lockControls(false);
                    songDuration.setVisibility(View.VISIBLE);
                    changeDetailsFromMusicPlayer();
                }
            }
        });


        if (MusicPlayer.player.isPlaying()) {
            controlPlayPause.setImageResource(R.drawable.pausefilled);
        } else {
            controlPlayPause.setImageResource(R.drawable.playfilled);
        }

    }// end of onCreate

    public static void changeDetailsFromMusicPlayer() {
        long duration = MusicPlayer.player.getDuration();
        seekBar.setMax((int) duration);
        if (MusicPlayer.currentSong != null) {
            songName.setText(MusicPlayer.currentSong.getname());
            songArtist.setText(MusicPlayer.currentSong.getArtist());


            if (MusicPlayer.currentSong.getCoverImg() != null) {
                Picasso.get().load(MusicPlayer.currentSong.getCoverImg()).placeholder(R.drawable.songplaceholder).error(R.drawable.songplaceholder).into(coverImage);
            } else {
                coverImage.setImageResource(R.drawable.songplaceholder);
            }
        }


    }

    public static void lockControls(Boolean enabled) {
        if (enabled) {
            controlNext.setEnabled(false);
            controlNext.setAlpha(0.3f);
            controlPast.setEnabled(false);
            controlPast.setAlpha(0.3f);
            controlPlayPause.setEnabled(false);
            controlPlayPause.setAlpha(0.3f);
            seekBar.setEnabled(false);
            seekBar.setAlpha(0.3f);
        } else {
            if (MusicPlayer.queue.size() != 0) {
                controlNext.setEnabled(true);
                controlNext.setAlpha(1f);
            }
            if (MusicPlayer.recentlyPlayedSongs.size() != 0) {
                controlPast.setEnabled(true);
                controlPast.setAlpha(1f);
            }
            controlPlayPause.setEnabled(true);
            controlPlayPause.setAlpha(1f);
            seekBar.setEnabled(true);
            seekBar.setAlpha(1f);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicPlayer.player.isPlaying()) {
            controlPlayPause.setImageResource(R.drawable.pausefilled);
        } else {
            controlPlayPause.setImageResource(R.drawable.playfilled);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == controlPlayPause) {
            if (MusicPlayer.player.isPlaying()) {
                MusicPlayer.player.pause();
                controlPlayPause.setImageResource(R.drawable.playfilled);
            } else {
                MusicPlayer.player.play();
                controlPlayPause.setImageResource(R.drawable.pausefilled);
            }
        }
        if (view == controlNext) {
            try {
                MusicPlayer.skipSong();
            } catch (ExtractionException e) {
                throw new RuntimeException(e);
            } catch (YoutubeRequestException e) {
                throw new RuntimeException(e);
            } catch (VideoIsUnavailable e) {
                throw new RuntimeException(e);
            }
            if (MusicPlayer.queue.size() == 0) {
                controlNext.setAlpha(0.3f);
                controlNext.setEnabled(false);
            } else {
                controlNext.setAlpha(1f);
                controlNext.setEnabled(true);
            }
            songDuration.setVisibility(View.INVISIBLE);
            lockControls(true);
        }
        if (view == controlPast) {
            try {
                MusicPlayer.previousSong();
            } catch (ExtractionException e) {
                throw new RuntimeException(e);
            } catch (YoutubeRequestException e) {
                throw new RuntimeException(e);
            } catch (VideoIsUnavailable e) {
                throw new RuntimeException(e);
            }
            if (MusicPlayer.recentlyPlayedSongs.size() == 0) {
                controlPast.setAlpha(0.3f);
                controlPast.setEnabled(false);
            } else {
                controlPast.setAlpha(1f);
                controlPast.setEnabled(true);
            }
            songDuration.setVisibility(View.INVISIBLE);
            lockControls(true);
        }
    }
}