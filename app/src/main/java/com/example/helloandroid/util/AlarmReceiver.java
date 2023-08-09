package com.example.helloandroid.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.helloandroid.database.userDBhelper;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;
    @Override
    public void onReceive(Context context, @NonNull Intent intent)
    {
        int uuid=intent.getIntExtra("uuid",0);
       userDBhelper muserDBhelper=userDBhelper.getInstance(context);
       muserDBhelper.openRDB();
    if(muserDBhelper.findbyUUID_alarm(String.valueOf( uuid))!=null)
{
    if(muserDBhelper.findbyUUID_alarm(String.valueOf( uuid)).isIson())
        {   showNotification(context,muserDBhelper.findbyUUID_alarm(String.valueOf( uuid)).getRingtoneUri(),
                muserDBhelper.findbyUUID_alarm(String.valueOf( uuid )).getVibrationSetting()
        ,String.valueOf(uuid));}}
        muserDBhelper.closeLink();
    }

    private void showNotification(Context context, Uri []uris,boolean isViberate,String uuid) {

        // 创建通知
        Random random = new Random();
        // 生成一个随机索引
        int randomIndex = random.nextInt(uris.length);
        Intent closeIntent = new Intent(context, MyRingService.class);
        closeIntent.putExtra("uri",uris[randomIndex]);
        closeIntent.putExtra("uuid",uuid);
        closeIntent.putExtra("isViberate",isViberate);
//        PendingIntent closePendingIntent = PendingIntent.getBroadcast(context, 0, closeIntent, PendingIntent.FLAG_IMMUTABLE);
//       builder.setContentIntent(closePendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, closeIntent);
        } else {
            context.startService(closeIntent);
        }
// 显示通知
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
//        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

}
