package com.example.helloandroid.util;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.example.helloandroid.R;
import com.example.helloandroid.database.userDBhelper;
import com.example.helloandroid.entity.myAlarm;
import com.example.helloandroid.helpers.myalarmhelper;

public class MyRingService extends Service {
    private Vibrator vibrator;
    private Ringtone alarmRingtone;
    private String uuid;

    public MyRingService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent closeIntent= new Intent(this,CloseAlarmActivity.class);
        uuid=intent.getStringExtra("uuid");
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//启用前台服务
//        Intent closeIntent = new Intent(this,CloseAlarmReceiver.class);
        PendingIntent closePendingIntent = PendingIntent.getActivity(this, 0, closeIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            builder = new NotificationCompat.Builder(this, MainActivity.NotificationChannelID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("闹钟时间到了")
                    .setContentText("点击关闭闹钟")
                    .setContentIntent(closePendingIntent)
                    .setOngoing(true)
                    .setFullScreenIntent(closePendingIntent,true)
                    .setAutoCancel(true);
        }

        startForeground(1,builder.build());

        // 创建Ringtone对象
        alarmRingtone = RingtoneManager.getRingtone(this, intent.getParcelableExtra("uri"));
        if (alarmRingtone != null)
        {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            alarmRingtone.setLooping(true);
        }
//         播放闹钟铃声
        if (alarmRingtone != null) {
            alarmRingtone.play();
        }

        boolean isvibrate=intent.getBooleanExtra("isViberate",false);
        if(isvibrate)
        {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null)
        {
            vibrator.vibrate(new long[]{0, 1000, 500}, 0);
        }
        }

    }return super.onStartCommand(intent, flags, startId);
    }
    public void onDestroy() {
        super.onDestroy();
        // 停止振动
//        wakeLock.release();
        if (vibrator != null) {
            vibrator.cancel();
        }

        //停止播放
        if(alarmRingtone!=null&&alarmRingtone.isPlaying())
        {
            alarmRingtone.stop();
        }

     userDBhelper   mhelper = userDBhelper.getInstance(this);
        mhelper.openWDB();
        mhelper.openRDB();
        myAlarm myAlarm= mhelper.findbyUUID_alarm(uuid);
        if(!myAlarm.isRepeated()){
            myAlarm.setIson(false);
                ContentValues contentValues=new ContentValues();

                    contentValues.put("ison",0);

                mhelper.openWDB().update(userDBhelper.DB_rings_info,contentValues,"alarmUUID = ?"
                        ,new String[]{uuid});
        }
        else{
            myalarmhelper.set_alarm(this.getApplicationContext(),myAlarm,false);
        }
mhelper.closeLink();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}