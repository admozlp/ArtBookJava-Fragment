package com.ademozalp.artfragmentjava.roomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.ademozalp.artfragmentjava.model.modelArt;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ArtDao {
    @Query("Select name, id from modelArt")
    Flowable<List<modelArt>> getArtWithNameAndId();

    @Query("Select * from modelArt where id = :id")
    Flowable<modelArt> getArtById(int id);

    @Insert
    Completable insert(modelArt modelArt);

    @Delete
    Completable delete(modelArt modelArt);
}
