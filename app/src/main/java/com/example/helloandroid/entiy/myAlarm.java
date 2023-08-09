package com.example.helloandroid.entiy;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.UUID;

public class myAlarm implements Parcelable{
    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    private int alarmId;

    private int hour;
    private int minutes;

    public boolean[] getRepeateddays() {
        return repeateddays;
    }

    public boolean isVibrationSetting() {
        return vibrationSetting;
    }

    public boolean isIson() {
        return ison;
    }

    private boolean isRepeated;
    private boolean [] repeateddays=new boolean[7];
    private Uri [] ringtoneUri;



    private String []ringtoneNames;
    private boolean vibrationSetting;
    private Parcel dest;

    public String[] getRepeatedAlarmIDs() {
        return repeatedAlarmIDs;
    }

    public void setRepeatedAlarmIDs(String[] repeatedAlarmIDs) {
        this.repeatedAlarmIDs = repeatedAlarmIDs;
    }
    public String[] getRingtoneNames() {
        return ringtoneNames;
    }
    private String[] repeatedAlarmIDs;
    protected myAlarm(Parcel in) {
        alarmId = in.readInt();
        hour = in.readInt();
        minutes = in.readInt();
        isRepeated = in.readByte() != 0;
        repeateddays = in.createBooleanArray();
        ringtoneUri = in.createTypedArray(Uri.CREATOR);
        ringtoneNames = in.createStringArray();
        vibrationSetting = in.readByte() != 0;
        ison = in.readByte() != 0;
        flags = in.readInt();
        isRepeated = in.readByte() != 0;
        repeateddays = in.createBooleanArray();
        ringtoneUri = in.createTypedArray(Uri.CREATOR);
        ringtoneNames = in.createStringArray();
        vibrationSetting = in.readByte() != 0;
    }

    public static final Creator<myAlarm> CREATOR = new Creator<myAlarm>() {
        @Override
        public myAlarm createFromParcel(Parcel in) {
            return new myAlarm(in);
        }

        @Override
        public myAlarm[] newArray(int size) {
            return new myAlarm[size];
        }
    };

    public void setIson(boolean ison) {
        this.ison = ison;
    }

    private boolean ison=true;
    private int flags;


    public myAlarm( String uuid,int hour, int minutes ,boolean [] repeateddays, Uri [] ringtoneUri, String []ringtoneNames, boolean vibrationSetting,boolean isRepeated
    ,boolean ison) {
        this.alarmId = UUID.randomUUID().hashCode();
       if(uuid!=null){this.alarmId=Integer.parseInt(uuid);}
        this.hour = hour;
        this.minutes = minutes;
        this.repeateddays = repeateddays;
        this.ringtoneUri = ringtoneUri;
        this.ringtoneNames = ringtoneNames;
        this.vibrationSetting = vibrationSetting;
        this.isRepeated=isRepeated;

        this.ison=ison;
    }



    // 添加getter方法以获取各个属性的值
    public int getAlarmId() {
        return alarmId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }


    public boolean[] repeateddays() {
        return repeateddays;
    }

    public Uri[] getRingtoneUri() {
        return ringtoneUri;
    }

    public boolean isRepeated() {
        return isRepeated;
    }

    public boolean getVibrationSetting() {
        return vibrationSetting;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

        dest.writeInt(alarmId);
        dest.writeInt(hour);
        dest.writeInt(minutes);
        dest.writeByte((byte) (isRepeated ? 1 : 0));
        dest.writeBooleanArray(repeateddays);
        dest.writeTypedArray(ringtoneUri, flags);
        dest.writeStringArray(ringtoneNames);
        dest.writeByte((byte) (vibrationSetting ? 1 : 0));
        dest.writeByte((byte) (ison ? 1 : 0));
        dest.writeInt(flags);
        dest.writeByte((byte) (isRepeated ? 1 : 0));
        dest.writeBooleanArray(repeateddays);
        dest.writeTypedArray(ringtoneUri, flags);
        dest.writeStringArray(ringtoneNames);
        dest.writeByte((byte) (vibrationSetting ? 1 : 0));
    }


}
