package com.example.helloandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.helloandroid.R;
import com.example.helloandroid.entity.song;

import java.util.List;

public class SongSpinnerAdapter extends ArrayAdapter<song> {
    public SongSpinnerAdapter(Context context, List<song> songs) {
        super(context, 0, songs);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        song song = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_select, parent, false);

            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.text_view);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(song.getTitle()+"----"+song.getFormatTime());

        return convertView;
    }

    // ViewHolder 类用于缓存视图中的控件对象，以便重复利用
    private static class ViewHolder {
        TextView textView;
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

    ViewHolder holder;
    song song = getItem(position);

        if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_select, parent, false);
        holder = new ViewHolder();
        holder.textView = convertView.findViewById(R.id.text_view);

        convertView.setTag(holder);
    } else {
        holder = (ViewHolder) convertView.getTag();
    }

        holder.textView.setText(song.getTitle()+"  "+song.getFormatTime()+"  "+"作者："+(song.getArtist().equals("unknown")?song.getArtist():"佚名"));

        return convertView;
}}
