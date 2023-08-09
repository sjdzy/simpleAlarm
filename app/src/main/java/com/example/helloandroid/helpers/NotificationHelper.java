package com.example.helloandroid.helpers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;

public class NotificationHelper {


    private  String channel_id;
   private NotificationChannel channel = null;
    private CharSequence channel_name;
    private Uri uri;

    public  NotificationHelper(String id, @NonNull CharSequence name, Uri uri)
    {
        this.channel_name=name;
        this.channel_id=id;
        this.uri=uri;
    }

    public String CreatemyNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    this.channel_id,
                    this.channel_name,
                    NotificationManager.IMPORTANCE_HIGH
            );
        }
        // 设置铃声

        // 获取系统的 NotificationManager
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        // 创建通知渠道

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);

            channel.enableVibration(true);


//            channel.setSound(this.uri,audioAttributes);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                channel.setAllowBubbles(true);
//            }            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            channel.setDescription("自定义闹钟，在本地媒体库中随机播放音乐");
            notificationManager.createNotificationChannel(channel);
        }

        return this.channel_id;
    }
}
