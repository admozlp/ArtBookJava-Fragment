package com.ademozalp.artfragmentjava.model;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class modelArt {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @Nullable
    @ColumnInfo(name="image")
    public byte[] image;

    @ColumnInfo(name="name")
    public String artname;

    @Nullable
    @ColumnInfo(name="artist")
    public String artistname;

    @Nullable
    @ColumnInfo(name="year")
    public String year;

    public modelArt(@Nullable byte[] image, String artname, @Nullable String artistname, @Nullable String year) {
        this.image = image;
        this.artname = artname;
        this.artistname = artistname;
        this.year = year;
    }
}
