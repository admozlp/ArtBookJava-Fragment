package com.ademozalp.artfragmentjava.roomdb;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ademozalp.artfragmentjava.model.modelArt;

@Database(entities = {modelArt.class}, version= 1)
public abstract class roomDatabase extends RoomDatabase {
    public abstract ArtDao artDao();
}
