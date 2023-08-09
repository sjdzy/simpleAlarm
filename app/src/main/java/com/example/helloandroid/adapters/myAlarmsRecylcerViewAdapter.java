package com.example.helloandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helloandroid.R;
import com.example.helloandroid.entiy.myAlarm;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public  class myAlarmsRecylcerViewAdapter extends RecyclerView.Adapter<myAlarmsRecylcerViewAdapter.myViewHolder> {
    Context context;
    private ArrayList<myAlarm>  myAlarms;
    private OnClickListener switchClickListener;

    public myAlarmsRecylcerViewAdapter(Context context, ArrayList<myAlarm> myAlarms, OnClickListener switchClickListener)
    {
        this.switchClickListener=switchClickListener;
        this.context=context;
        this.myAlarms=myAlarms;
    }
    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_alarm,parent,false);
        return new myViewHolder(view);
    }
public void addItem(myAlarm myAlarm)
{
    this.myAlarms.add(myAlarm);
    notifyDataSetChanged();
}
public  boolean removeItem(int position)
{
    if(myAlarms.get(position)!=null)
    {
        this.myAlarms.remove(myAlarms.get(position));
    notifyItemRemoved(position);
    return true;
    }
    return false;
}

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.timetext.setText(myAlarms.get(position).getHour()+":"+(myAlarms.get(position).getMinutes()<10?
                ("0"+myAlarms.get(position).getMinutes()):(myAlarms.get(position).getMinutes())));
//        for (int i = 0; i < myAlarms.get(position).getRingtoneNames().length; i++) {
//            holder.alarmNamestext.append(myAlarms.get(position).getRingtoneNames()[i]+" ,");
//        }
   holder.myswitch.setChecked(myAlarms.get(position).isIson());
        if(myAlarms.get(position).isIson())
        {
            holder.alarmNamestext.setText(switchClickListener.getRemainingtime(myAlarms.get(position)));
        holder.alarmNamestext.setAlpha(1f);
        holder.datetext.setAlpha(1f);
        holder.timetext.setAlpha(1f);
        }
        else {holder.alarmNamestext.setText("");
            holder.alarmNamestext.setAlpha(0.3f);
            holder.datetext.setAlpha(0.3f);
            holder.timetext.setAlpha(0.3f);}

        holder.myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchClickListener.onSwitchClick(holder.getAdapterPosition(), isChecked);
            }
        });
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
              String  uuid=String.valueOf( myAlarms.get(holder.getAdapterPosition()).getAlarmId());
                switchClickListener.onlinearLayoutLongClick(uuid,holder.getAdapterPosition());
                return  false;
            }
        });
       holder.linearLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               switchClickListener.onlinearLayoutClick(String.valueOf(myAlarms.get(holder.getAdapterPosition()).getAlarmId()));
           }
       });

        if(myAlarms.get(position).getRepeateddays()!=null)
        { StringBuilder stringBuilder=new StringBuilder();
        boolean[] repeaded=myAlarms.get(position).getRepeateddays();
        if(!myAlarms.get(position).isRepeated()){stringBuilder.append("只响一次");}
       else if (Arrays.equals(repeaded,new boolean[]{true,true,true,true,true,true,true})){stringBuilder.append("每天");} else if
        (Arrays.equals(repeaded,new boolean[]{false,true,true,true,true,true,false})){stringBuilder.append("周一到周五");}
        else
        {
            if(repeaded[1])stringBuilder.append("周一").append(" ");
            if(repeaded[2])stringBuilder.append("周二").append(" ");
            if(repeaded[3])stringBuilder.append("周三").append(" ");
            if(repeaded[4])stringBuilder.append("周四").append(" ");
            if(repeaded[5])stringBuilder.append("周五").append(" ");
            if(repeaded[6])stringBuilder.append("周六").append(" ");
            if(repeaded[0])stringBuilder.append("周日").append(" ");
        }
        String s=stringBuilder.toString();
        holder.datetext.setText(s);}
    }


    public interface OnClickListener {
        void onSwitchClick(int position, boolean isChecked);
        boolean onlinearLayoutLongClick(String uuid,int position);
        boolean onlinearLayoutClick(String uuid);

        String getRemainingtime(myAlarm myAlarm);
    }

    @Override
    public int getItemCount() {
        return this.myAlarms.size();
    }
    public  static class myViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView timetext;
        TextView datetext;
        TextView alarmNamestext;
        Switch myswitch;
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout=itemView.findViewById(R.id.alarm_linearlayout);
            myswitch=itemView.findViewById(R.id.switchView);
            timetext=itemView.findViewById(R.id.Time_textview);
            datetext=itemView.findViewById(R.id.date_textview);
            alarmNamestext=itemView.findViewById(R.id.Alarm_names);

        }
    }
}
