package com.example.helloandroid.entiy;

import android.net.Uri;

public class song {
    private long ID;
    private   String Artist;
    private  String Title;
    private String Data;
    private  String Display_name;
    private  int Duration;
    private Uri uri;

    public song(long ID, String artist, String title, String data, String display_name, int duration,Uri uri) {
        this.ID = ID;
        Artist = artist;
        Title = title;
        Data = data;
        Display_name = display_name;
        Duration = duration;
        this.uri=uri;
    }
    public  song(long id, String title,int duration, Uri uri)
    {
        this(id,null,title,null,null,duration,uri);


    }

    public String getArtist() {
        return Artist;
    }

    public String getTitle() {
        return Title;
    }

    public String getData() {
        return Data;
    }

    public String getDisplay_name() {
        return Display_name;
    }

    public int getDuration() {
        return Duration;
    }

    public long getID() {
        return ID;
    }

    public Uri getUri() {
        return uri;
    }

    public  String getFormatTime()
    {
        return String.format("%02d:%02d",Duration/60000,Duration/1000%60);
    }
}
