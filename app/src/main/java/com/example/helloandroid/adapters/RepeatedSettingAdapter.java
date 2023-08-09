package com.example.helloandroid.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.helloandroid.R;

import java.util.List;
public class RepeatedSettingAdapter extends ArrayAdapter<String> {


    public RepeatedSettingAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_select, parent, false);
            TextView textView = convertView.findViewById(R.id.text_view);
            textView.setTextSize(15);
            textView.setGravity(Gravity.END| Gravity.CENTER_VERTICAL);
            textView.setText(getItem(position));
        }
        return convertView;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_select, parent, false);
            TextView textView = convertView.findViewById(R.id.text_view);
            textView.setTextSize(15);
            textView.setText(getItem(position));
        }
        return convertView;
    }
}
