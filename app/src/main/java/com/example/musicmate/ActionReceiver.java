package com.example.musicmate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.kotvertolet.youtubejextractor.exception.ExtractionException;
import com.github.kotvertolet.youtubejextractor.exception.VideoIsUnavailable;
import com.github.kotvertolet.youtubejextractor.exception.YoutubeRequestException;




public class ActionReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getStringExtra("action");
        if (action.equals("previous")) {
            try {
                MusicPlayer.previousSong();
            } catch (ExtractionException e) {
                throw new RuntimeException(e);
            } catch (YoutubeRequestException e) {
                throw new RuntimeException(e);
            } catch (VideoIsUnavailable e) {
                throw new RuntimeException(e);
            }
        }
        if (action.equals("next")) {
            try {
                MusicPlayer.skipSong();
            } catch (ExtractionException e) {
                throw new RuntimeException(e);
            } catch (YoutubeRequestException e) {
                throw new RuntimeException(e);
            } catch (VideoIsUnavailable e) {
                throw new RuntimeException(e);
            }
        }
        if (action.equals("playPause")) {
            if (MusicPlayer.player.isPlaying()) {
                MusicPlayer.player.pause();
                if (playing.isCreated) {
                    playing.controlPlayPause.setImageResource(R.drawable.playfilled);
                }
            } else {
                MusicPlayer.player.play();
                if (playing.isCreated) {
                    playing.controlPlayPause.setImageResource(R.drawable.pausefilled);
                }
            }
        }
    }
}
