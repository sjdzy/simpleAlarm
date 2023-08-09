package com.example.helloandroid.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.helloandroid.database.userDBhelper;
import com.example.helloandroid.entity.myAlarm;
import com.example.helloandroid.util.AlarmReceiver;

import java.util.Calendar;

public  class myalarmhelper {
    public static String set_alarm(Context context,String uuid, int hour, int minute, Uri[] uris, boolean isVibrate, boolean isrepeated,
                                     boolean[] reaptedDays,
                              String [] alarmNames,boolean ison,boolean isToast) {
        userDBhelper mhelper=userDBhelper.getInstance(context.getApplicationContext());
        mhelper.openRDB();
        mhelper.openWDB();

         myAlarm  myAlarm = new myAlarm(uuid,hour, minute, reaptedDays,
                    uris,alarmNames,isVibrate,isrepeated,ison);

        Calendar currentTime = Calendar.getInstance();
        int currentDayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK);
        // 创建一个Calendar对象，用于设置闹钟的时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        // 计算下一个设置闹钟的日期
        int daysToAdd = 0;
        if(isrepeated)
        {
        for (int i = 0; i < 7; i++) {
            int index = (currentDayOfWeek - 1 + i) % 7; // 修正星期几的索引
            if (reaptedDays[index]) {
                daysToAdd = i;

                break;
            }
        }
            calendar.add(Calendar.DAY_OF_WEEK, daysToAdd);
        }
        if(daysToAdd==0)//一次性闹钟和当天的重复闹钟
        {int add_day=0;
            if (hour < currentTime.get(Calendar.HOUR_OF_DAY) ||
                    (hour == currentTime.get(Calendar.HOUR_OF_DAY) && minute <= currentTime.get(Calendar.MINUTE))) {
                // 时间已经过了当前时间，设置为第二天

              if(!myAlarm.isRepeated())  { add_day=1;}
              else {
                  myAlarm.getRepeateddays()[currentDayOfWeek-1]=false;//当天设为0
                  boolean isoneday=true;
                  for (int i = 0; i < myAlarm.getRepeateddays().length; i++) {
                      if(myAlarm.getRepeateddays()[i]!=false) {isoneday=false; break;}
                  }

                  if(isoneday){add_day=7;}
                  else
                  {
                      for (int i = 0; i < 7; i++) {
                          int index = (currentDayOfWeek - 1 + i) % 7; // 修正星期几的索引
                          if (reaptedDays[index]) {
                              add_day = i;
                              break;
                          }
                      }
                  }
                  myAlarm.getRepeateddays()[currentDayOfWeek-1]=true;
              }
            }
            calendar.add(Calendar.DAY_OF_MONTH, add_day);
        }

        // 使用setExactAndAllowWhileIdle来确保在Doze模式下也能触发闹钟
            Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
            intent.putExtra("uuid", myAlarm.getAlarmId());
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), myAlarm.getAlarmId(), intent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            if(isToast) {
                if (PendingIntent.getBroadcast(context.getApplicationContext(), myAlarm.getAlarmId(), intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE) != null) {
                    long timeDifferenceInMillis = calendar.getTimeInMillis() - currentTime.getTimeInMillis();
                    long timeDifferenceInMinutes = timeDifferenceInMillis / (1000 * 60);
                    long hoursDifference = timeDifferenceInMinutes / 60;
                    long daysDifference=hoursDifference/24;
                    hoursDifference=hoursDifference%24;
                    long minutesDifference = timeDifferenceInMinutes % 60;
                    if (mhelper.findbyUUID_alarm(String.valueOf(myAlarm.getAlarmId())) == null) {
                        mhelper.openWDB();
                        mhelper.insert(myAlarm);
                    }
                    // 显示提示消息
                    String message = (hoursDifference == 0 && minutesDifference == 0) ? "还有不到1分钟响铃" :"闹钟将在 " +
                            (daysDifference!=0?daysDifference+"天":" ")+
                            hoursDifference + "小时 " + (minutesDifference) + " 分钟后响铃";
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "闹钟设置失败", Toast.LENGTH_SHORT).show();
                }
            }
        // MARK: 这里是一个重要的处理逻辑 有activity转换生命周期转换问题！！！！！！！！！！！！！！！！！！！！！
        if (alarmManager != null) {
            alarmManager = null;
        }
        return String.valueOf(myAlarm.getAlarmId());
    }
    public  static String set_alarm(Context context,myAlarm myAlarm,boolean isToast)
    {

        set_alarm(context.getApplicationContext(),String.valueOf( myAlarm.getAlarmId()),myAlarm.getHour(),myAlarm.getMinutes(),myAlarm.getRingtoneUri(),
                myAlarm.getVibrationSetting(),myAlarm.isRepeated(),myAlarm.repeateddays(),myAlarm.getRingtoneNames(),myAlarm.isIson(),isToast);

        return String.valueOf( myAlarm.getAlarmId());
    }

    public static boolean cancel_alarm(Context context,int id)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
// 创建与设置定时器时使用的相同PendingIntent
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("uuid", id);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_NO_CREATE);

if(alarmIntent!=null)
{ alarmManager.cancel(alarmIntent);
    if (alarmManager != null) {
        alarmManager = null;
    }
return  true;
}
return false;
    }
    public static String get_duration(int hour,int minute,boolean isrepeated,boolean reaptedDays[])
    {
        Calendar currentTime = Calendar.getInstance();
        int currentDayOfWeek = currentTime.get(Calendar.DAY_OF_WEEK);
        // 创建一个Calendar对象，用于设置闹钟的时间
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        // 计算下一个设置闹钟的日期
        int daysToAdd = 0;
        if(isrepeated)
        {
            for (int i = 0; i < 7; i++) {
                int index = (currentDayOfWeek - 1 + i) % 7; // 修正星期几索引
                if (reaptedDays[index]) {
                    daysToAdd = i;

                    break;
                }
            }
            calendar.add(Calendar.DAY_OF_WEEK, daysToAdd);
        }
        if(daysToAdd==0)//一次性闹钟和当天的重复闹钟
        {
            int add_day=0;
            if (hour < currentTime.get(Calendar.HOUR_OF_DAY) ||
                    (hour == currentTime.get(Calendar.HOUR_OF_DAY) && minute <= currentTime.get(Calendar.MINUTE))) {
                // 时间已经过了当前时间，设置为第二天

                if(!isrepeated)  { add_day=1;}
                else {
                    reaptedDays[currentDayOfWeek-1]=false;//当天设为0
                    boolean isoneday=true;
                    for (int i = 0; i < reaptedDays.length; i++) {
                        if(reaptedDays[i]!=false) {isoneday=false; break;}
                    }

                    if(isoneday){add_day=7;}
                    else
                    {
                        for (int i = 0; i < 7; i++) {
                            int index = (currentDayOfWeek - 1 + i) % 7; // 修正星期几的索引
                            if (reaptedDays[index]) {
                                add_day = i;
                                break;
                            }
                        }
                    }
                    reaptedDays[currentDayOfWeek-1]=true;
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, add_day);
        }
        long timeDifferenceInMillis = calendar.getTimeInMillis() - currentTime.getTimeInMillis();
        long timeDifferenceInMinutes = timeDifferenceInMillis / (1000 * 60);
        long hoursDifference = timeDifferenceInMinutes / 60;
        long daysDifference=hoursDifference/24;
        hoursDifference=hoursDifference%24;
        long minutesDifference = timeDifferenceInMinutes % 60;
        String message = (hoursDifference == 0 && minutesDifference == 0) ? "还有不到1分钟响铃" :"闹钟将在 " +
                (daysDifference!=0?daysDifference+"天":"")+
                hoursDifference + "小时" + (minutesDifference) + "分钟后响铃";
        return  message;
    }
    public static String reload_alarm(Context context,myAlarm myAlarm,boolean isToast)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
// 创建与设置定时器时使用的相同PendingIntent
        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("uuid", myAlarm.getAlarmId());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), myAlarm.getAlarmId(), intent, PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_NO_CREATE);

        if(alarmIntent==null) {
            set_alarm(context.getApplicationContext(), myAlarm,false);
        }

            if(isToast)
            { Toast.makeText(context,get_duration(myAlarm.getHour(),
                myAlarm.getMinutes(),myAlarm.isRepeated(),myAlarm.repeateddays()),Toast.LENGTH_SHORT).show();}
        return  String.valueOf( myAlarm.getAlarmId());
    }

}
