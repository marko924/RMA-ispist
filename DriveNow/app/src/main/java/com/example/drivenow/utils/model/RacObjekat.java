package com.example.drivenow.utils.model;

import android.net.Uri;

public class RacObjekat {
    private String slikaUrl;
    private int id;

    public RacObjekat(int id, String slikaUrl){
        this.slikaUrl = slikaUrl;
        this.id = id;
    }

    public int getId() { return id; }
    public String getSlikaUrl() { return slikaUrl; }
}
