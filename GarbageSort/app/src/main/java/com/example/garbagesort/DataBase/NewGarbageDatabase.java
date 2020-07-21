package com.example.garbagesort.DataBase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Garbage.class,LastCity.class},version = 2,exportSchema = false)
public abstract  class NewGarbageDatabase extends RoomDatabase {
   public abstract GarbageDao getGarbageDao();

   public abstract LastCityDao getLastCityDao();
}
