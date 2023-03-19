package com.example.musicmate;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import java.io.IOException;
import java.net.URL;

public class createNotification {
    public static final String CHANNEL_ID = "channel1";
    public static final int NOTIFICATION_ID = 69420;

    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static NotificationCompat.Builder notification;

    public static void destroyNotification(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancel(NOTIFICATION_ID);
        return;
    }

    @SuppressLint("MissingPermission")
    public static void createNotification(Context context, Song song, int playbutton, int pos, int size, Boolean playing) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");
            Bitmap icon = null;
            try {
                URL url = new URL(song.getCoverImg());
                icon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                Drawable appIcon = context.getApplicationContext().getDrawable(R.drawable.watermark);
                Bitmap appIconBitmap = ((BitmapDrawable) appIcon).getBitmap();

                Intent intentPrevious = new Intent(context, ActionReceiver.class);
                intentPrevious.putExtra("action", "previous");
                PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, 1, intentPrevious, PendingIntent.FLAG_IMMUTABLE);

                Intent intentPlayPause = new Intent(context, ActionReceiver.class);
                intentPlayPause.putExtra("action", "playPause");
                PendingIntent pendingIntentPlayPause = PendingIntent.getBroadcast(context, 2, intentPlayPause, PendingIntent.FLAG_IMMUTABLE);

                Intent intentNext = new Intent(context, ActionReceiver.class);
                intentNext.putExtra("action", "next");
                PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, 3, intentNext, PendingIntent.FLAG_IMMUTABLE);

                notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setLargeIcon(icon)
                        .setContentTitle(song.getname())
                        .setContentText(song.getArtist())
                        .setSmallIcon(IconCompat.createWithBitmap(appIconBitmap))
                        .setOnlyAlertOnce(true)
                        .setShowWhen(false)
                        .setOngoing(playing)
                        .setColorized(true)
                        .setSilent(true)
                        .setPriority(NotificationCompat.PRIORITY_LOW);
                Integer available = 0;


                if (MusicPlayer.recentlyPlayedSongs.size() > 0) {
                    available++;
                    notification.addAction(new NotificationCompat.Action(R.drawable.nextpassbackwards, "Previous", pendingIntentPrevious));
                }
                notification.addAction(new NotificationCompat.Action(playbutton, "Play / Pause", pendingIntentPlayPause));
                available++;
                if (MusicPlayer.queue.size() > 0) {
                    notification.addAction(new NotificationCompat.Action(R.drawable.nextpast, "Next", pendingIntentNext));
                    available++;
                }

                switch (available) {
                    case 1:
                        notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0)
                                .setMediaSession(mediaSessionCompat.getSessionToken()));
                        break;
                    case 2:
                        notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1)
                                .setMediaSession(mediaSessionCompat.getSessionToken()));
                        break;
                    case 3:
                        notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mediaSessionCompat.getSessionToken()));
                        break;
                    default:
                        notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0)
                                .setMediaSession(mediaSessionCompat.getSessionToken()));
                        break;
                }


                Notification builtNotification = notification.build();
                notificationManagerCompat.notify(NOTIFICATION_ID, builtNotification);


            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
