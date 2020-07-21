package com.example.garbagesort.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface LastCityDao {
    @Update
    int changeLastCity(LastCity... lastCities);

    @Query("SELECT * FROM LastCity")
    List<LastCity> queryAll();

    @Insert
    void insertOne(LastCity... lastCities);

}
