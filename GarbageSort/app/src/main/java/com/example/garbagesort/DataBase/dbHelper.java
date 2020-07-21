package com.example.garbagesort.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class dbHelper extends SQLiteOpenHelper {



    public static final String CREATE_INFO ="create table Garbage03("
            +"name text,"
            +"kind text,"
            +"ps text,"
            +"confidence text,"
            +"model text,"
            +"time text,"
            +"city text,"
            +"path text)";

    

    public static final String LAST_CITY ="create table LastCity("
            +"lastcity integer)";


    private Context mContext;

    public dbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INFO);
        db.execSQL(LAST_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Garbage03");
        db.execSQL("drop table if exists LastCity");
        onCreate(db);
    }
}
