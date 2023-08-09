package com.example.helloandroid.util;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloandroid.R;
import com.example.helloandroid.adapters.RepeatedSettingAdapter;
import com.example.helloandroid.database.userDBhelper;
import com.example.helloandroid.helpers.myalarmhelper;

import java.util.ArrayList;
import java.util.Arrays;

public class Add_ringActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button button_backtomain;
    Button button_save_ring;
    private TimePicker timePicker;
    private AlarmManager alarmManager;
    private userDBhelper mhelper;
    private Switch switch_vibrate;
    TextView textView_custom;
    private Spinner spinner_repeatedSetting;
    private boolean[] selectedDays = new boolean[7];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {setContentView(R.layout.activity_add_ring);
            button_backtomain = findViewById(R.id.Back_to_main);
            button_save_ring = findViewById(R.id.save_ring);
            timePicker = findViewById(R.id.TimePicker);
            timePicker.setIs24HourView(true);
            switch_vibrate=findViewById(R.id.switch_viberate);
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            spinner_repeatedSetting=findViewById(R.id.Spinner_repeated);
            textView_custom=findViewById(R.id.textview_custom);

       spinner_repeatedSetting.setOnItemSelectedListener(this);
            button_backtomain.setOnClickListener(this);
            button_save_ring.setOnClickListener(this);
            textView_custom.setOnClickListener(this);
        }
   catch (Exception e){String s= e.toString();
        int s4=2;}
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.Back_to_main) {
            finish();
        }
        else if(id==R.id.save_ring)
        {
            //uri和名称都是上个活动传递过来的
            String [] names=getIntent().getStringArrayExtra("alarmNames");
            String [] urisS=getIntent().getStringArrayExtra("uris");
            Uri []uris=new Uri[urisS.length];
            for (int i = 0; i < urisS.length; i++) {
                uris[i]=Uri.parse(urisS[i]);
            }
            boolean atLeastOneSelected = false;
            for (boolean selected : selectedDays) {
                if (selected) {
                    atLeastOneSelected = true;
                    break;
                }
            }
            myalarmhelper.set_alarm(this,null,timePicker.getHour(),timePicker.getMinute(),
                    uris,switch_vibrate.isChecked(),atLeastOneSelected,selectedDays,names,true,true);
            finish();
    }
        else if (id==R.id.textview_custom) {

            if(spinner_repeatedSetting.getSelectedItem().equals("自定义")){showCustomRepeatDialog();}
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mhelper = userDBhelper.getInstance(this);
        mhelper.openRDB();
        mhelper.openWDB();
        ArrayList<String> list= new ArrayList<>();
        list.add("只响一次");
        list.add("每天");
        list.add("周一到周五");
        list.add("自定义");
        RepeatedSettingAdapter repeatedSettingAdapter=new RepeatedSettingAdapter(this,list);
        spinner_repeatedSetting.setAdapter(repeatedSettingAdapter);
    }

    private void showCustomRepeatDialog() {
        final String[] daysOfWeek = {"周日","周一", "周二", "周三", "周四", "周五", "周六"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("自定义重复日期");
        builder.setMultiChoiceItems(daysOfWeek, selectedDays, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedDays[which] = isChecked;
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                 StringBuilder  stringBuilder=new StringBuilder();

                if( selectedDays[1])stringBuilder.append("周一").append(" ");
                if( selectedDays[2])stringBuilder.append("周二").append(" ");
                if( selectedDays[3])stringBuilder.append("周三").append(" ");
                if( selectedDays[4])stringBuilder.append("周四").append(" ");
                if( selectedDays[5])stringBuilder.append("周五").append(" ");
                if( selectedDays[6])stringBuilder.append("周六").append(" ");
                if( selectedDays[0])stringBuilder.append("周日").append(" ");
                if (Arrays.equals(selectedDays,new boolean[]{true,true,true,true,true,true,true})){spinner_repeatedSetting.setSelection(1);} else if
                (Arrays.equals(selectedDays,new boolean[]{false,true,true,true,true,true,false})){spinner_repeatedSetting.setSelection(2);}
                else if (Arrays.equals(selectedDays,new boolean[]{false,false,false,false,false,false,false})){spinner_repeatedSetting.setSelection(0);}
                else{textView_custom.setText(stringBuilder.toString());}
                dialog.dismiss();

        }});
        builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    builder.show();};

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item=spinner_repeatedSetting.getAdapter().getItem(position).toString();
        if(item.equals("每天"))
        {
            textView_custom.setText(" ");
            for (int i = 0; i <7 ; i++) {
                selectedDays[i]=true;
            }
        }
        else if (item.equals("周一到周五")) {
            textView_custom.setText(" ");
            for (int i = 1; i <6 ; i++) {
                selectedDays[i]=true;
            }
        }
        else if (item.equals("只响一次")) {
            textView_custom.setText(" ");
            for (int i = 0; i < 7; i++) {
                selectedDays[i]=false;
            }
        }
        else if (item.equals("自定义")) {
            showCustomRepeatDialog();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

