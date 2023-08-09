package com.example.helloandroid.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.helloandroid.entity.myAlarm;
import com.example.helloandroid.entity.song;

public  class  userDBhelper extends SQLiteOpenHelper {

    public static final String Column_Title="title";
    public static final String Column_Artist="artist";
    public static final String Column_ID="id";
    public static final String Column_Data="data";
    public static final String Column_Display_name="display_name";
    public static final String Column_Duration="duration";
    public static final String Column_Uri="uri";
    public static final String DB_songs_info ="song_info";
    public static final String DB_rings_info="rings_info";
    public  static final String DB_name="user.db";
    public  static  final String Column_uuid_alarm="alarmUUID";
    public  static final String Column_hour_ALarm="hour";
    public  static final String Column_minutes_ALarm="minutes";
    public  static final String Column_uris_ALarm="uris";
    public  static final String Column_ison_ALarm="ison";
    public  static final String Column_vibrationSetting_ALarm="vibrationSetting";
    public  static final String Column_ringtonenames_ALarm="ringtonenames";
    public  static final String Column_isrepeated_ALarm="isrepeated";
    public static  final  String Column_repeatedDays="repeatedDays";

    private static userDBhelper myhelper;
   private  static  int DB_version=2;
   private  SQLiteDatabase mRDB=null;
   private   SQLiteDatabase mWDB=null;
    private ContentValues values;

    private userDBhelper(@Nullable Context context) {
        super(context, DB_name, null, DB_version);
    }

    public static userDBhelper getInstance(Context context)
    {
        if(myhelper==null)
        {
            myhelper=new userDBhelper(context);

        }
return  myhelper;

    }
    public boolean isTableEmpty(String tableName) {
        String countQuery = "SELECT COUNT(*) FROM " + tableName;
        Cursor cursor = mRDB.rawQuery(countQuery, null);
        int rowCount = 0;
        if (cursor.moveToFirst()) {
            rowCount = cursor.getInt(0);
        }
        cursor.close();
        return rowCount == 0;
    }


    public boolean isTableExists( String tableName) {
        Cursor cursor = mRDB.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = ?",
                new String[]{tableName});
        boolean tableExists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }
        return tableExists;
    }
    public SQLiteDatabase openRDB()
    {
        if(mRDB==null||!mRDB.isOpen())
        {mRDB=myhelper.getReadableDatabase();}
        return mRDB;
    }
    public SQLiteDatabase openWDB()
    {
        if(mWDB==null||!mWDB.isOpen())
        {
            mWDB=myhelper.getWritableDatabase();
        }
        return mWDB;
    }
    public void closeLink()
    {
        if(mRDB!=null&&mRDB.isOpen())
        {
            mRDB.close();
            mRDB=null;
        }
        if(mWDB!=null&&mWDB.isOpen())
        {
            mWDB.close();
            mWDB=null;
        }
    }
    public long insert(song song)
    {
        String insertQuery = "INSERT INTO " + DB_songs_info + " (title, artist, id, data, display_name, duration, uri) VALUES (?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = mWDB.compileStatement(insertQuery);
        statement.bindString(1, song.getTitle());
        statement.bindString(2, song.getArtist());
        statement.bindLong(3, song.getID());
        statement.bindString(4, song.getData());
        statement.bindString(5, song.getDisplay_name());
        statement.bindLong(6, song.getDuration());
        statement.bindString(7, song.getUri() != null ? song.getUri().toString() : null);
       return statement.executeInsert();
    }
    public long insert(myAlarm myAlarm)
    {
        // 将URI数组转换为字符串
        StringBuilder uriString = new StringBuilder();
        for (Uri uri : myAlarm.getRingtoneUri()) {
            uriString.append(uri.toString()).append(",");
        }
        StringBuilder nameString=new StringBuilder();
        for (String name:myAlarm.getRingtoneNames()) {
            nameString.append(name).append("@");
        }
        StringBuilder repeatedDaysString = new StringBuilder();
        for (boolean day : myAlarm.getRepeateddays()) {
            repeatedDaysString.append(day==true?"1":"0").append(",");
        }
        mWDB.beginTransaction();
        try {
            String insertQuery = "INSERT INTO " + DB_rings_info + " (alarmUUID, hour, minutes, uris, vibrationSetting, ringtonenames, ison, isrepeated,repeatedDays) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            SQLiteStatement statement = mWDB.compileStatement(insertQuery);
            statement.bindLong(1, myAlarm.getAlarmId());
            statement.bindLong(2, myAlarm.getHour());
            statement.bindLong(3, myAlarm.getMinutes());
            statement.bindString(4, uriString.toString());
            statement.bindLong(5, myAlarm.getVibrationSetting() ? 1 : 0);
            statement.bindString(6, nameString.toString());
            statement.bindLong(7, 1);
            statement.bindLong(8, myAlarm.isRepeated() ? 1 : 0);// 1 for true, 0 for false
            statement.bindString(9,repeatedDaysString.toString());
            statement.executeInsert();
            mWDB.setTransactionSuccessful(); // 标记事务执行成功
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWDB.endTransaction(); // 结束事务
        }

        values = null; // 释放资源

        return myAlarm.getAlarmId();

    }
    public long deletebyID(long ID)
    {
      return    mWDB.delete(DB_songs_info,"ID=?",new String[]{String.valueOf(ID)});
    }
    public boolean DeleteByUUID_alarm(String uuid)
    {
        if(isTableExists(DB_rings_info)){
            return mWDB.delete(DB_rings_info,"alarmUUID=?",new String[]{uuid})>0?true:false;}
        else return false;
    }
    @SuppressLint     ("Range")
    public myAlarm findbyUUID_alarm(String uuid)
{
    String selection = userDBhelper.Column_uuid_alarm + " = ?";
    String[] selectionArgs = {uuid}; // 这里查询主键为uuid的记录
    Cursor cursor =  mRDB.query(DB_rings_info,null,selection,selectionArgs,null,null,null,null);
    if (cursor != null&& cursor.moveToNext()) {
        // 找到了匹配的记录
        // 在此处处理查询结果
        String[] uriss=cursor.getString(cursor.getColumnIndex(Column_uris_ALarm)).split(",");
        Uri [] uris=new Uri[uriss.length];
        for (int i = 0; i < uriss.length; i++) {
            uris[i]=Uri.parse(uriss[i]);
        }
        String[] repeateds=cursor.getString(cursor.getColumnIndex(Column_repeatedDays)).split(",");
        boolean[]repeated=new boolean[7];
        for (int i = 0; i <7 ; i++) {
            repeated[i]=(repeateds[i].equals("1"));
        }
    String s=  cursor.getString(cursor.getColumnIndex(Column_uuid_alarm));
        myAlarm alarm=new myAlarm(cursor.getString(cursor.getColumnIndex(Column_uuid_alarm)),
                cursor.getInt(cursor.getColumnIndex(Column_hour_ALarm)),
        cursor.getInt(cursor.getColumnIndex(Column_minutes_ALarm)),
                repeated,uris,
                cursor.getString(cursor.getColumnIndex(Column_ringtonenames_ALarm)).split("@")
                ,cursor.getInt(cursor.getColumnIndex(Column_vibrationSetting_ALarm))!=0?true:false,
                cursor.getInt(cursor.getColumnIndex(Column_isrepeated_ALarm))==0?false:true
                ,cursor.getInt(cursor.getColumnIndex(Column_ison_ALarm))!=0?true:false);
        return alarm;
    } else {
        return  null;
        // 没有找到匹配的记录
        // 在此处进行相应处理，比如显示提示信息等
    }
}
    public boolean clearTable(String TB_name)
    {
        if(isTableExists(TB_name)){
        return mWDB.delete(TB_name,null,null)>0?true:false;}
        else return false;

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTB1="CREATE TABLE IF NOT EXISTS "+DB_songs_info+" ("+
                "title VARCHAR NOT NULL,"+
                "artist VARCHAR ,"+
                "id LONG   PRIMARY KEY NOT NULL,"+
                "data VARCHAR ," +
                "display_name VARCHAR ,"+
                "duration INTEGER ,"+
                "uri VARCHAR); " ;

       String createTB2="CREATE TABLE IF NOT EXISTS "+DB_rings_info+" ("+
               "alarmUUID VARCHAR PRIMARY KEY NOT NULL,"+
               "hour INTEGER NOT NULL,"+
               "minutes INTEGER NOT NULL," +
               "uris VARCHAR NOT NULL,"+
               "ison INTEGER NOT NULL,"+
               "isrepeated INTEGER NOT NULL,"+
               "repeatedDays VARCHAR NOT NULL,"+
               "vibrationSetting INTEGER NOT NULL," +
               "ringtonenames VARCHAR NOT NULL); ";

       db.execSQL(createTB1);
       db.execSQL(createTB2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
