package com.example.garbagesort.DataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GarbageDao {
    @Query("DELETE FROM GARBAGE")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Garbage... Garbage);

    @Query("SELECT * FROM Garbage ORDER BY id")
    List<Garbage> queryAll();

    @Update
    int updateDatabase(Garbage... garbage);

}
