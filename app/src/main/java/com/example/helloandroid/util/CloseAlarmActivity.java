package com.example.helloandroid.util;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helloandroid.R;
import com.example.helloandroid.database.userDBhelper;
import com.example.helloandroid.entiy.myAlarm;
import com.example.helloandroid.helpers.myalarmhelper;

public class CloseAlarmActivity extends AppCompatActivity implements View.OnClickListener {
    Button button_close_alarm;
    private String uuid;


    public void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.close_alarm);
        button_close_alarm=findViewById(R.id.close_alarm);
        button_close_alarm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        NotificationManager notificationManager = (NotificationManager) v.getContext().getSystemService(v.getContext().NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmReceiver.NOTIFICATION_ID);
        Context context=v.getContext();

        Intent serviceIntent = new Intent(context, MyRingService.class);

        context.stopService(serviceIntent);
        finish();
    }

}
