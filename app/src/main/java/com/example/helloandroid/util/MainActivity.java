package com.example.helloandroid.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloandroid.R;
import com.example.helloandroid.adapters.SongSpinnerAdapter;
import com.example.helloandroid.adapters.myAlarmsRecylcerViewAdapter;
import com.example.helloandroid.database.userDBhelper;
import com.example.helloandroid.entity.myAlarm;
import com.example.helloandroid.entity.song;
import com.example.helloandroid.helpers.NotificationHelper;
import com.example.helloandroid.helpers.myalarmhelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainActivity extends AppCompatActivity implements View.OnClickListener,  AdapterView.OnItemSelectedListener {
    public  static  String NotificationChannelID="channel_id";
    private static final int REQUEST_CODE_PERMISSIONS = 100;

    private final String[] permissions = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SET_ALARM,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.USE_FULL_SCREEN_INTENT,
            Manifest.permission.VIBRATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.DISABLE_KEYGUARD,
    };

    private RecyclerView myRecyclerView;
    Cursor cursor;
    Button find_button;
    Button stop_button;
    Button delete_song_button;
    Button save_button;
    Button add_ring_button;
   List<song>songs;
    Spinner find_spinner;
    MediaPlayer mediaPlayer;
    AlarmManager alarmManager;

    RingtoneManager ringtoneManager ;
    private userDBhelper mhelper;
    private ArrayList<myAlarm> myAlarms;
    private myAlarmsRecylcerViewAdapter myAlarmsRecylcerViewAdapter;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      myRecyclerView=findViewById(R.id.myRecyclerview);
        find_spinner=findViewById(R.id.spinner_songs);
        find_button=findViewById(R.id.button);
        stop_button=findViewById(R.id.stopPlay);
        add_ring_button=findViewById(R.id.add_ring);
        save_button=findViewById(R.id.save_button);
        delete_song_button=findViewById(R.id.delete_song);
        alarmManager=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        ringtoneManager=new RingtoneManager(this);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);

        delete_song_button.setOnClickListener(this);
        find_button.setOnClickListener(this);
        stop_button.setOnClickListener(this);
        find_spinner.setOnItemSelectedListener(this);
        save_button.setOnClickListener(this);
       add_ring_button.setOnClickListener(this);

       checkBox=findViewById(R.id.checkbox);


       myAlarms=new ArrayList<>();
        songs = new ArrayList<>();
        mediaPlayer=new MediaPlayer();
        requestPermissions();

        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);



    }

    private void requestPermissions() {
        boolean areAllcommited=true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                areAllcommited=false;
            }
        }
      if(!areAllcommited)
      {
          ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
      }

    }
    private  void createChannelforNotification()
    {
        Random random = new Random();

        // 生成一个随机索引
        int randomIndex = random.nextInt(songs.size());
        NotificationHelper notificationHelper=new NotificationHelper(MainActivity.NotificationChannelID,"闹钟",songs.get(randomIndex).getUri() );
        notificationHelper.CreatemyNotificationChannel(this);
    }
    @Override
    protected void onStart() {
        super.onStart();

            mhelper = userDBhelper.getInstance(this);
            mhelper.openRDB();
            mhelper.openWDB();
        if(songs.isEmpty())
        {reloadsongs();}
      else
      {createChannelforNotification();}
reloadAlarms();
    }
    @SuppressLint("Range")
    private void reloadAlarms() {

        if (mhelper.isTableExists(userDBhelper.DB_rings_info)&&!mhelper.isTableEmpty(userDBhelper.DB_rings_info))
        {
            myAlarms.clear();
            cursor=mhelper.openRDB().query(userDBhelper.DB_rings_info,null,null,null,null,null,null,null);
            if (cursor != null && cursor.moveToNext()) {
                do {
                    if(mhelper.findbyUUID_alarm(cursor.getString(cursor.getColumnIndex(userDBhelper.Column_uuid_alarm)))!=null)
                    {
                        myAlarm myAlarm=mhelper.findbyUUID_alarm(cursor.getString(cursor.getColumnIndex(userDBhelper.Column_uuid_alarm)));
                        myAlarms.add(myAlarm);
                        myalarmhelper.reload_alarm(this,myAlarm,false);
                    }
                } while (cursor.moveToNext());
            }

        }
        if(!myAlarms.isEmpty())
        {
            myAlarmsRecylcerViewAdapter = new myAlarmsRecylcerViewAdapter(this, myAlarms, new myAlarmsRecylcerViewAdapter.OnClickListener() {
                @Override
                public void onSwitchClick(int position, boolean isChecked) {
                    ContentValues contentValues=new ContentValues();
                    if(isChecked)
                    {
                        contentValues.put("ison",1);
                        myalarmhelper.reload_alarm(MainActivity.this,myAlarms.get(position),true);
                        myAlarms.get(position).setIson(true);
                    }
                    else {
                        contentValues.put("ison",0);
                        myAlarms.get(position).setIson(false);
                    }

                    mhelper.openWDB().update(userDBhelper.DB_rings_info,contentValues,"alarmUUID = ?"
                            ,new String[]{String.valueOf( myAlarms.get(position).getAlarmId())});
                  myRecyclerView.setAdapter(myAlarmsRecylcerViewAdapter);

                }
                @Override
                public boolean onlinearLayoutLongClick(String uuid,int position) {
                    //添加确定按钮
                    //添加返回按钮
                    new AlertDialog.Builder(MainActivity.this).setTitle("信息提示")//设置对话框标题

                            .setMessage("是否删除闹钟？")

                            .setPositiveButton("是", (dialog, which) -> {
                                mhelper.DeleteByUUID_alarm(uuid);
                                myAlarmsRecylcerViewAdapter.removeItem(position);
                                dialog.dismiss();
                                //不用取消通知，因为通知在创建时会查询数据库，如果没有就不会创建通知

                                if(myalarmhelper.cancel_alarm(MainActivity.this,Integer.parseInt(uuid)))
                                { Toast.makeText(getApplicationContext(),"删除成功!",Toast.LENGTH_SHORT).show();}
                            }).setNegativeButton("否", (dialog, which) -> {
                                        dialog.dismiss();
                                    }
                            ).show();//在按键响应事件中显示此对话框

                    return true;
                }

                @Override
                public boolean onlinearLayoutClick(String uuid) {
                    Toast.makeText(MainActivity.this,"dsa",Toast.LENGTH_SHORT).show();

                    return false;
                }

                @Override
                public String getRemainingtime(myAlarm myAlarm) {
                    String mes=myalarmhelper.get_duration(myAlarm.getHour(),myAlarm.getMinutes(),myAlarm.isRepeated(),myAlarm.repeateddays());
                    return mes;
                }


            });
        myRecyclerView.setAdapter(myAlarmsRecylcerViewAdapter);}
    }
    @SuppressLint("Range")
    private void reloadsongs() {
        if(mhelper.isTableExists(userDBhelper.DB_songs_info)&&!mhelper.isTableEmpty(userDBhelper.DB_songs_info))
        {  songs.clear();
        cursor = mhelper.openRDB().query(userDBhelper.DB_songs_info, null, null, null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            do {
                Uri uri= cursor.getString(cursor.getColumnIndex(userDBhelper.Column_Uri))==null?null:Uri.parse(cursor.getString(cursor.getColumnIndex(userDBhelper.Column_Uri)));
                songs.add(new song(cursor.getLong(cursor.getColumnIndex(userDBhelper.Column_ID)),
                        cursor.getString(cursor.getColumnIndex(userDBhelper.Column_Artist)),
                        cursor.getString(cursor.getColumnIndex(userDBhelper.Column_Title)),
                        cursor.getString(cursor.getColumnIndex(userDBhelper.Column_Data)),
                        cursor.getString(cursor.getColumnIndex(userDBhelper.Column_Display_name)),
                        cursor.getInt(cursor.getColumnIndex(userDBhelper.Column_Duration)),
                       uri ));
                // 处理查询结果Uri.parse(cursor.getString(cursor.getColumnIndex(userDBhelper.Column_Uri)))
            } while (cursor.moveToNext());
        }
        if (songs != null) {
            showSongs(songs);
        }
    }}

    @Override
    protected void onStop() {
        super.onStop();
      mhelper.closeLink();
    }

    private void showSongs(@NonNull List<song> list) {

        SongSpinnerAdapter songSpinnerAdapter = new SongSpinnerAdapter(this, list);
        find_spinner.setAdapter(songSpinnerAdapter);
    }


    private void Initsongs()
    {
        loadMusic();
        if (songs != null) {
            showSongs(songs);
            mhelper.clearTable(userDBhelper.DB_songs_info);
            for (int i = 0; i < songs.size(); i++) {
                mhelper.insert(songs.get(i));
            }
            Toast.makeText(this,"成功找到"+songs.size()+"首铃声/本地歌曲",Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onClick(View v) {
        try {
            int id = v.getId();
            if (id == R.id.button) {
              Initsongs();
            }
            //播放，暂停按钮
            else if (id == R.id.stopPlay) {

                if(mediaPlayer!=null)
                {
                    if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                } else {
                        mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }}
            } else if (id == R.id.delete_song) {deleteSelectedsong();

            } else if (id == R.id.save_button)
            {
                mhelper.clearTable(userDBhelper.DB_songs_info);
                if(songs!=null)
                {
                    for (int i = 0; i<songs.size() ; i++) {
                        mhelper.insert(songs.get(i));
                    }
                    Toast.makeText(this,"保存成功！",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this,"歌曲库为空！",Toast.LENGTH_SHORT).show();
                }


            }
            else if (id==R.id.add_ring)
            {
                if(songs.isEmpty()){Toast.makeText(this,"无本地音乐,请给闹钟添加至少一首音乐!",Toast.LENGTH_SHORT).show();}
              else {
                  Intent intent=new Intent(MainActivity.this, Add_ringActivity.class);
                String[] uris=new String[songs.size()];
                String[] Alarmnames=new String[songs.size()];
                    for (int i = 0; i < songs.size(); i++) {
                        uris[i]=songs.get(i).getUri().toString();
                        Alarmnames[i]=songs.get(i).getTitle();
                    }
                intent.putExtra("uris",uris);
                    intent.putExtra("alarmNames",Alarmnames);
              startActivity(intent);}
            }
        }
        catch (Exception e){
            String errorMessage = "发生异常: " + e.getMessage();
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteSelectedsong()
    {
        if (!songs.isEmpty()) {
            ArrayAdapter<song> adapter = (ArrayAdapter<song>) find_spinner.getAdapter();
            int selectedPosition = find_spinner.getSelectedItemPosition();

                adapter.remove(adapter.getItem(selectedPosition));

//                    只有一首歌，删除成功
            if (songs.isEmpty()) {
                mediaPlayer.reset();
                return;
            }
            mediaPlayer.reset();
//                 删除第一项
            AdapterView.OnItemSelectedListener listener=find_spinner.getOnItemSelectedListener();
            if(listener!=null)
            {
                int i=find_spinner.getSelectedItemPosition()-1;
                listener.onItemSelected(find_spinner,find_spinner.getSelectedView(),i,find_spinner.getSelectedItemId());
            }
        }
        else {
            Toast.makeText(this, "歌曲库为空！", Toast.LENGTH_SHORT).show();
        }

    }
    private void loadMusic() {
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        cursor = this.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null);
        songs.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                songs.add(new song(cursor.getInt(0), cursor.getString(1),cursor.getString(2),
                        cursor.getString(3),cursor.getString(4),cursor.getInt(5),Uri.fromFile(new File(cursor.getString(3)))));
            }
        }
//
        if(checkBox.isChecked()) {
            cursor = ringtoneManager.getCursor();
            if (cursor != null && cursor.moveToNext()) {
                do {
                    mediaPlayer = MediaPlayer.create(this, ringtoneManager.getRingtoneUri(cursor.getPosition()));
                    int Duration = mediaPlayer.getDuration();
                    songs.add((new song(cursor.getLong(RingtoneManager.ID_COLUMN_INDEX), cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX), Duration, ringtoneManager.getRingtoneUri(cursor.getPosition()))));
                    mediaPlayer.reset();

                } while (cursor.moveToNext());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // 检查权限是否全部被授予
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                if(!mhelper.isTableExists(userDBhelper.DB_songs_info)||mhelper.isTableEmpty(userDBhelper.DB_songs_info))
                {Initsongs();}
                // 权限全部被授予，可以继续执行相关操作
            } else {

                for (int i = 0; i < grantResults.length; i++) {
                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
                // 权限未被授予，需要处理权限请求被拒绝的情况
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

          mhelper=userDBhelper.getInstance(this);
        mhelper.openRDB();
        mhelper.openWDB();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        try {
            if(mediaPlayer.isPlaying())
            {mediaPlayer.stop();}
            mediaPlayer.reset();

           mediaPlayer.setDataSource(this,songs.get(position).getUri());
            // 准备MediaPlayer
            mediaPlayer.prepare();//此处等待播放按钮，可能会造成延迟，异步未准备好

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer.isPlaying()){mediaPlayer.stop();}
        mediaPlayer.release();
        mediaPlayer=null;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}