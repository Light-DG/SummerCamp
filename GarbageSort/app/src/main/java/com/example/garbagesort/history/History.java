package com.example.garbagesort.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.garbagesort.DataBase.Garbage;
import com.example.garbagesort.DataBase.GarbageDao;
import com.example.garbagesort.DataBase.NewGarbageDatabase;
import com.example.garbagesort.R;
import com.example.garbagesort.DataBase.dbHelper;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    List<BaseInfo> mylist = null;
    private static final String TAG = "History";
    RecyclerView recyclerView =null;
    GarbageInfoAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        mylist = new ArrayList<BaseInfo>();
        adapter = new GarbageInfoAdapter(mylist);
        loadDB();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }



    public void loadDB()
    {
        new Thread(){
            @Override
            public void run() {
                mylist.clear();
                BaseInfo baseInfo = null;
                NewGarbageDatabase database = Room.databaseBuilder(History.this,NewGarbageDatabase.class,"NewDatabase04.db").build();
                GarbageDao garbageDao = database.getGarbageDao();
                List<Garbage> tempList = garbageDao.queryAll();
                for (int i = 0;i<tempList.size();i++){
                    baseInfo = new BaseInfo();
                    baseInfo.setName(tempList.get(i).getName());
                    baseInfo.setKind(tempList.get(i).getKind());
                    baseInfo.setTime(tempList.get(i).getTime());
                    String s = tempList.get(i).getModel();
                    String cityName = tempList.get(i).getName();
                    if (s.equals("text")){
                        baseInfo.setModel("文字识别\n"+cityName);
                    }else if (s.equals("image")){
                        baseInfo.setModel("图像识别\n"+cityName);
                    }else if (s.equals("voice")){
                         baseInfo.setModel("语音识别\n"+cityName);
                     }
                    mylist.add(baseInfo);
                }

                adapter.notifyDataSetChanged();
            }
        }.start();

    }

//    public void loadDB()
//    {
//        mylist.clear();
//        BaseInfo baseInfo = null;
//        dbHelper helper = new dbHelper(this,"SearchHistory.db",null,5);
//        SQLiteDatabase db = helper.getReadableDatabase();
//        Cursor cursor = db.query("Garbage03",null,null,null,
//                null,null,null);
//        if (cursor.moveToFirst()){
//            do{
//                baseInfo = new BaseInfo();
//                baseInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
//                baseInfo.setKind(cursor.getString(cursor.getColumnIndex("kind")));
//                baseInfo.setTime(cursor.getString(cursor.getColumnIndex("time")));
////                baseInfo.setModel(cursor.getString(cursor.getColumnIndex("model")));
//                String s =cursor.getString(cursor.getColumnIndex("model"));
//                String cityName = cursor.getString(cursor.getColumnIndex("city"));
//                Log.d(TAG, "调试： s: "+s);
//                if (s.equals("text")){
//                    baseInfo.setModel("文字识别\n"+cityName);
//                }else if (s.equals("image")){
//                    baseInfo.setModel("图像识别\n"+cityName);
//                }else if (s.equals("voice")){
//                    baseInfo.setModel("语音识别\n"+cityName);
//                }
//                Log.d(TAG, "调试: name: "+baseInfo.getName());
//                mylist.add(baseInfo);
//            }while (cursor.moveToNext());
//
//        }
//        cursor.close();
//        adapter.notifyDataSetChanged();
//
//    }




}
