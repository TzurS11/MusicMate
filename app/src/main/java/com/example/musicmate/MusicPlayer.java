package com.example.musicmate;

import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaActionSound;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.github.kotvertolet.youtubejextractor.YoutubeJExtractor;
import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;
import com.github.kotvertolet.youtubejextractor.models.newModels.VideoPlayerConfig;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultAllocator;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Stack;


public class MusicPlayer extends Service {
    public static ExoPlayer player;
    public static Song currentSong = null;
    public static ArrayList<Song> queue = null;
    public static ArrayList<Song> recentlyPlayedSongs = null;
    private static Context applicationContext = null;


    public static void initiate(Context context) {
        applicationContext = context.getApplicationContext();
        queue = new ArrayList<>();
        recentlyPlayedSongs = new ArrayList<>();


        int MIN_BUFFER_DURATION = 3000; // 3 seconds
        int MAX_BUFFER_DURATION = 3000; // 3 seconds
        int MIN_PLAYBACK_RESUME_BUFFER = 1500; // 1.5 seconds
        int MIN_PLAYBACK_START_BUFFER = 500; // 0.5 seconds
        LoadControl loadControl = new DefaultLoadControl.Builder().setAllocator(new DefaultAllocator(true, 16)).setBufferDurationsMs(MIN_BUFFER_DURATION, MAX_BUFFER_DURATION, MIN_PLAYBACK_START_BUFFER, MIN_PLAYBACK_RESUME_BUFFER).setTargetBufferBytes(-1).setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl();


        player = new ExoPlayer.Builder(applicationContext).setLooper(Looper.myLooper()).setAudioAttributes(AudioAttributes.DEFAULT, true).build();

        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (player.isPlaying()) {
                    createNotification.createNotification(applicationContext, currentSong, R.drawable.pausefilled, 1, queue.size(),true);
                } else {
                    createNotification.createNotification(applicationContext, currentSong, R.drawable.playfilled, 1, queue.size(),false);
                }
//                if (queue.size() == 0 && player.getPlaybackState() == Player.STATE_ENDED) {
//                    createNotification.destroyNotification(applicationContext);
//                }

                Player.Listener.super.onIsPlayingChanged(isPlaying);
                if (player.getMediaItemCount() != 0) {
                    afterlogin.goToPlayingActivity.setVisibility(View.VISIBLE);
                    if (playing.isCreated) {
                        playing.isHoldingSeekbar = false;
                    }
                } else {
                    afterlogin.goToPlayingActivity.setVisibility(View.GONE);
                }
            }
        });
        player.setPlayWhenReady(true);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);

                if (playbackState == Player.STATE_ENDED) {
                    if (queue.size() != 0) {
                        recentlyPlayedSongs.add(currentSong);
                    }
                    if (queue.size() > 0) {
                        try {
                            currentSong = MusicPlayer.queue.remove(0);
                            Log.wtf("song end", "ended song: " + currentSong.getname());
                            playFromUrlFromQueue();
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

                if (playbackState == Player.STATE_READY) {
                    if (playing.isCreated) {
                        playing.changeDetailsFromMusicPlayer();
                        playing.isHoldingSeekbar = false;
                        if (MusicPlayer.queue.size() == 0) {
                            playing.controlNext.setAlpha(0.3f);
                            playing.controlNext.setEnabled(false);
                        } else {
                            playing.controlNext.setAlpha(1f);
                            playing.controlNext.setEnabled(true);
                        }
                        if (MusicPlayer.recentlyPlayedSongs.size() == 0) {
                            playing.controlPast.setAlpha(0.3f);
                            playing.controlPast.setEnabled(false);
                        } else {
                            playing.controlPast.setAlpha(1f);
                            playing.controlPast.setEnabled(true);
                        }
                    }
                }
            }
        });
    }


    public static void skipSong() throws ExtractionException, YoutubeRequestException, VideoIsUnavailable {
        if (queue.size() > 0) {
            recentlyPlayedSongs.add(MusicPlayer.currentSong);
//            MusicPlayer.setQueue(MusicPlayer.queue); // moving song to next
            currentSong = MusicPlayer.queue.remove(0);
            playFromUrlFromQueue();
        }
    }

    public static void previousSong() throws ExtractionException, YoutubeRequestException, VideoIsUnavailable {
        if (recentlyPlayedSongs.size() > 0) {
            queue.add(0, currentSong);
            currentSong = MusicPlayer.recentlyPlayedSongs.remove(recentlyPlayedSongs.size() - 1);
            playFromUrlFromQueue();
        }
    }


    public static void clearQueue() {
        player.stop();
        queue.clear();
        recentlyPlayedSongs.clear();
        player.clearMediaItems();
        currentSong = null;
    }

    public static void playAndOverride(MediaItem mediaItem, Song song) {
        clearQueue();
        currentSong = song;
        player.setMediaItem(mediaItem);
        player.prepare();
//        player.play();
    }

    public static void setQueue(ArrayList<Song> newQueue) {
        queue = newQueue;
        currentSong = queue.remove(0);
    }

    public static void shuffleQueue() {
        player.getDuration();
        player.addListener(new Player.Listener() {
        });
        Collections.shuffle(queue);
    }

    public static void playFromUrlFromQueue() throws ExtractionException, YoutubeRequestException, VideoIsUnavailable {
        new FetchStreamableUrlTask().execute();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class FetchStreamableUrlTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            try {
                // fetch the streamable url using YoutubeJExtractor
                YoutubeJExtractor youtubeJExtractor = new YoutubeJExtractor();
                VideoPlayerConfig videoData = youtubeJExtractor.extract(currentSong.getid());
                String dashManifest = Objects.requireNonNull(videoData.getStreamingData()).getAdaptiveAudioStreams().get(0).getUrl();
                currentSong.setDownloadUrl(dashManifest);
                return dashManifest;
            } catch (Exception e) {
                Log.e("FetchStreamableUrlTask", "Error fetching streamable URL", e);
                return null;
            }
        }

        protected void onPostExecute(String streamableUrl) {
            // update the UI with the streamable url
            if (streamableUrl != null) {
                 
                player.setMediaItem(MediaItem.fromUri(currentSong.getDownloadUrl()));
                player.prepare();
//        player.play();

            } else {
                // handle the error
                // ...
            }
        }
    }
}
