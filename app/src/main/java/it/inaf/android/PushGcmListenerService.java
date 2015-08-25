/* Copyright (c) 2015 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.Date;

public class PushGcmListenerService extends GcmListenerService {

    private static int ID = 0;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        sendNotification(data);
    }

    private void sendNotification(Bundle data) {
        String title = data.getString("title");
        String desc = data.getString("message");

        Date date = new Date(data.getString("date"));
        String a = date.toString();

        Intent intent = new Intent(this, FeedListActivity.class);
        intent.putExtra("feed_type", "events");
        intent.putExtra("feed_url", "http://www.media.inaf.it/category/eventi/feed");
        intent.putExtra("nav_position", R.id.drawer_section_3);
        intent.putExtra("top_activity", true);
        intent.putExtra("item_title", title);
        intent.putExtra("item_description", desc);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(title)
                .setContentText(desc)
                .setWhen(date.getTime())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setGroup("events")
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(ID, notificationBuilder.build());
        ID++;
    }
}
