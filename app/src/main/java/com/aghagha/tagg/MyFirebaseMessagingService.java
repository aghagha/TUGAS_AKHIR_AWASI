package com.aghagha.tagg;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    final String TAG = "MESSAGING SERVICE";

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri);


            Intent intent = new Intent(remoteMessage.getNotification().getClickAction());

            switch (remoteMessage.getNotification().getClickAction()){
                case("OPEN_BERITA"):
                    intent.putExtra("judul", remoteMessage.getData().get("judul"));
                    intent.putExtra("konten", remoteMessage.getData().get("konten"));
                    intent.putExtra("tanggal", remoteMessage.getData().get("tanggal"));
                    intent.putExtra("gambar", remoteMessage.getData().get("gambar"));
                    intent.putExtra("lampiran", remoteMessage.getData().get("lampiran"));
                    break;
                case("OPEN_TOPIK"):
                    intent.putExtra("judul", remoteMessage.getData().get("judul"));
                    intent.putExtra("konten", remoteMessage.getData().get("konten"));
                    intent.putExtra("tanggal", remoteMessage.getData().get("tanggal"));
                    intent.putExtra("gambar", remoteMessage.getData().get("gambar"));
                    intent.putExtra("id_topik", remoteMessage.getData().get("id_topik"));
                    break;
                case("OPEN_TUGAS"):
                    intent.putExtra("id", remoteMessage.getData().get("id"));
                    intent.putExtra("deadline", remoteMessage.getData().get("deadline"));
                    intent.putExtra("judul", remoteMessage.getData().get("judul"));
                    intent.putExtra("dibuat", remoteMessage.getData().get("dibuat"));
                    intent.putExtra("konten", remoteMessage.getData().get("konten"));
                    intent.putExtra("murid", remoteMessage.getData().get("murid"));
                    intent.putExtra("cek", remoteMessage.getData().get("cek"));
                    break;
            }

            PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

            mBuilder.setContentIntent(contentIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(69, mBuilder.build());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
