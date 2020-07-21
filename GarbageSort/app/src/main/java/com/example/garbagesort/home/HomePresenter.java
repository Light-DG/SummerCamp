package com.example.garbagesort.home;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.garbagesort.DataBase.GarbageDao;
import com.example.garbagesort.DataBase.LastCity;
import com.example.garbagesort.DataBase.LastCityDao;
import com.example.garbagesort.DataBase.NewGarbageDatabase;
import com.example.garbagesort.MyApplication;
import com.example.garbagesort.Parse;
import com.example.garbagesort.DataBase.dbHelper;
import com.example.garbagesort.history.History;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomePresenter implements HomeContract.homePresenter {

    private static final String TAG = "HomePresenter";

    HomeContract.homeView mView;

    Parse myParse = null;
    ArrayList<String> hotWordList = null;

    private int nowChecked = 3;
    public String cityId = null;
    String cityName = "上海市";



    public HomePresenter(HomeContract.homeView view){
        //此构造方法在 MainActivity 的OnCreate() 方法里 一开始就调用。
        mView = view;

    }






    class getHotWordThread implements Runnable {
        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            String url = "https://api.tianapi.com/txapi/hotlajifenlei/index?key=7b0abb09d3f8790699dd5893dd117cd3";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                String ret = response.body().string();
                hotWordList = myParse.hotWordParse(ret);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void requireHotWord(){
        myParse = new Parse();
        new Thread(new getHotWordThread()).start();  //一打开程序，就启动 获得当天热词的线程,早点开启，保证有数据
    }

    public ArrayList<String> getHotWordList(){
        return hotWordList;
    }


    @Override
    public void setCityId(String s) {
        if (s.equals("北京市")) {
                nowChecked = 1;
                cityId = "110000";
                cityName="北京市";
                mView.setCityName("当前城市： 北京市");
        } else if (s.equals("深圳市")) {
            nowChecked = 2;
            cityId = "440300";
            cityName="深圳市";
            mView.setCityName("当前城市： 深圳市");
        } else if (s.equals("上海市")) {
            nowChecked = 3;
            cityId = "310000";
            cityName="上海市";
            mView.setCityName("当前城市： 上海市");
        } else if (s.equals("西安市")) {
            nowChecked = 4;
            cityId = "610100";
            cityName="西安市";
            mView.setCityName("当前城市： 西安市");
        } else if (s.equals("宁波市")) {
            nowChecked = 5;
            cityId = "330200";
            cityName="宁波市";
            mView.setCityName("当前城市： 宁波市");
        }
    }

    @Override
    public int getNowChecked() {
        return nowChecked;
    }


    public String getCityName() {
        return cityName;
    }

    @Override
    public String getCityId() {
        return cityId;
    }

    public void setNowChecked (int nowChecked){
        this.nowChecked = nowChecked;
    }

    public int SetLastCity(){
        Migration migration_1_2 = new Migration(1,2) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `LastCity`(`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,`lastCity` TEXT DEFAULT '上海市')");
            }
        };
        NewGarbageDatabase database = Room.databaseBuilder(MyApplication.getContext(),NewGarbageDatabase.class,"NewDatabase04.db")
                .allowMainThreadQueries()
                .addMigrations(migration_1_2)
                .build();


        LastCityDao lastCityDao = database.getLastCityDao();

        if (lastCityDao.queryAll().size() == 0)
        {
            LastCity firstCity = new LastCity("上海市");
            lastCityDao.insertOne(firstCity);
        }else {
            LastCity lastCity = lastCityDao.queryAll().get(0);
            setCityId(lastCity.getLastCity());
            mView.uncheckOthers();
        }
        return nowChecked;
    }

    public void clearFiles() {
        String path = "";
        String model = "";
        new Thread(){
            @Override
            public void run() {
                super.run();
                NewGarbageDatabase database = Room.databaseBuilder(MyApplication.getContext(),NewGarbageDatabase.class,"NewDatabase04.db").build();
                GarbageDao garbageDao = database.getGarbageDao();
                garbageDao.deleteAll();
            }
        }.start();


        File catchDir = new File(MyApplication.getContext().getExternalCacheDir().toString());
        File pcmDir = new File(MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/MyRecord03");
        File wavDir = new File(MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/MyRecord04");
        if (catchDir.isDirectory()) {
            File[] catchFiles = catchDir.listFiles();
            for (File file : catchFiles) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        if (pcmDir.isDirectory()) {
            File[] pcmFiles = pcmDir.listFiles();
            for (File file : pcmFiles) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        if (wavDir.isDirectory()) {
            File[] wavFiles = wavDir.listFiles();
            for (File file : wavFiles) {
                if (file.exists()) {
                    file.delete();
                }
            }
        }

//        int len = writeDb.delete("Garbage03", null, null);
//        Log.d(TAG, "调试 删除数量： " + len);
        Toast.makeText(MyApplication.getContext(), "清空成功！", Toast.LENGTH_SHORT).show();
    }

    // 清除录音，所拍照片，截屏
//    public void clearFiles() {
//        String path = "";
//        String model = "";
//        dbHelper helper = new dbHelper(MyApplication.getContext(), "SearchHistory.db", null, 5);
//        SQLiteDatabase readDb = helper.getReadableDatabase();
//        SQLiteDatabase writeDb = helper.getWritableDatabase();
//        File catchDir = new File(MyApplication.getContext().getExternalCacheDir().toString());
//        File pcmDir = new File(MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/MyRecord03");
//        File wavDir = new File(MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/MyRecord04");
//        if (catchDir.isDirectory()) {
//            File[] catchFiles = catchDir.listFiles();
//            for (File file : catchFiles) {
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//        }
//        if (pcmDir.isDirectory()) {
//            File[] pcmFiles = pcmDir.listFiles();
//            for (File file : pcmFiles) {
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//        }
//        if (wavDir.isDirectory()) {
//            File[] wavFiles = wavDir.listFiles();
//            for (File file : wavFiles) {
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//        }
//
//        int len = writeDb.delete("Garbage03", null, null);
//        Log.d(TAG, "调试 删除数量： " + len);
//        Toast.makeText(MyApplication.getContext(), "清空成功！", Toast.LENGTH_SHORT).show();
//    }




}
