package com.example.musicmate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor;
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.github.kotvertolet.youtubejextractor.models.newModels.VideoPlayerConfig;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Stack;

public class MusicPlayer extends AppCompatActivity {
    public static ExoPlayer player;
    public static Song currentSong = null;
    public static ArrayList<Song> queue = null;
    private static ArrayList<Song> recentlyPlayedSongs = null;

    public MusicPlayer(Context context) {
        queue = new ArrayList<>();
        recentlyPlayedSongs = new ArrayList<>();
        player = new ExoPlayer.Builder(context.getApplicationContext()).setLooper(Looper.myLooper()).setAudioAttributes(AudioAttributes.DEFAULT, true).build();
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (isPlaying && player.getCurrentMediaItem() != null) {
                    afterlogin.goToPlayingActivity.setVisibility(View.VISIBLE);
                    if (playing.isCreated) {
                        playing.isHoldingSeekbar = false;
                    }
                } else if (!isPlaying && player.getCurrentMediaItem() == null) {
                    afterlogin.goToPlayingActivity.setVisibility(View.GONE);
                }
            }
        });
    }

    public static void clearQueue() {
        player.stop();
        queue.clear();
        player.clearMediaItems();
        currentSong = null;
    }

    public static void playAndOverride(MediaItem mediaItem, Song song) {
        clearQueue();
        currentSong = song;
        player.addMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    public static void setQueue(ArrayList<Song> newQueue) {
        queue = newQueue;
        currentSong = queue.remove(0);
    }

    public static void shuffleQueue(){
        player.getDuration();
        player.addListener(new Player.Listener() {
        });
        Collections.shuffle(queue);
    }

    public static void playFromUrlFromQueue(Context context) throws ExtractionException, YoutubeRequestException, VideoIsUnavailable {
        SharedPreferences sharedPreferences = context.getSharedPreferences("videoUrls", MODE_PRIVATE);
        if (!sharedPreferences.contains(currentSong.getid())) {
            YoutubeJExtractor youtubeJExtractor = new YoutubeJExtractor();
            VideoPlayerConfig videoData = youtubeJExtractor.extract(currentSong.getid());
            String dashManifest = videoData.getStreamingData().getAdaptiveAudioStreams().get(0).getUrl();
            currentSong.setDownloadUrl(dashManifest);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(currentSong.getid(), dashManifest);
            editor.commit();
        } else {
            String songLink = sharedPreferences.getString(currentSong.getid(), null);
            Uri uri = Uri.parse(songLink);
            Integer expirationDate = Integer.valueOf(uri.getQueryParameter("expire"));
            Date d = new Date(expirationDate);
            if (d.before(Calendar.getInstance().getTime())) {
                YoutubeJExtractor youtubeJExtractor = new YoutubeJExtractor();
                VideoPlayerConfig videoData = youtubeJExtractor.extract(currentSong.getid());
                String dashManifest = videoData.getStreamingData().getAdaptiveAudioStreams().get(0).getUrl();
                currentSong.setDownloadUrl(dashManifest);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(currentSong.getid(), dashManifest);
                editor.commit();
            } else {
                currentSong.setDownloadUrl(sharedPreferences.getString(currentSong.getid(), null));
            }
        }
        Log.wtf("tag", currentSong.getDownloadUrl());
        player.stop();
        player.clearMediaItems();
        player.addMediaItem(MediaItem.fromUri(currentSong.getDownloadUrl()));
        player.prepare();
        player.play();
        if (playing.isCreated) {
            playing.changeDetailsFromMusicPlayer();
            playing.isHoldingSeekbar = false;
        }
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == Player.STATE_ENDED) {
                    if (queue.size() > 0) {
                        setQueue(queue);
                        try {
                            recentlyPlayedSongs.add(currentSong);
                            playFromUrlFromQueue(context);
                        } catch (ExtractionException e) {
                            throw new RuntimeException(e);
                        } catch (YoutubeRequestException e) {
                            throw new RuntimeException(e);
                        } catch (VideoIsUnavailable e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        player.removeListener(this);
                    }
                }
            }
        });

    }
}
