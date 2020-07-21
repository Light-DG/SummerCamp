package com.example.garbagesort.show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.room.Room;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbagesort.DataBase.Garbage;
import com.example.garbagesort.DataBase.GarbageDao;
import com.example.garbagesort.DataBase.NewGarbageDatabase;
import com.example.garbagesort.R;
import com.example.garbagesort.DataBase.dbHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Show extends AppCompatActivity implements View.OnClickListener {

    boolean isText = false;
    boolean isImage = false;
    boolean isVoice = false;
    boolean isHistory = false;

    ImageView voiceIcon = null;
    TextView garbageName = null;
    TextView garbageKind = null;
    TextView garbagePs = null;
    TextView garbageConfidence = null;
    TextView Tip = null;
    ImageView garbagePicture = null;
    LinearLayout shareChoice = null;
    ArrayList<String> responseList = null;
    String photoPath = null;
    String wavFilePath = null;
    String confidence = null;
    MediaPlayer mediaPlayer = null;
    int count=0;
    AudioManager audioManager =null;
    String searchTime =null;
    File screenShotFile = null;
    ImageView weChatFrientIcon = null;
    ImageView weChatMomentsIcon = null;
    ImageView QQFriendsIcon = null;
    int clickShareCount = 0;
    private static final String TAG = "Show";
    String cityName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show);
        isImage = false;
        isVoice = false;
        isText = false;
        isHistory = false;
        mediaPlayer = new MediaPlayer();
        voiceIcon = (ImageView)findViewById(R.id.icon_voice);
        garbageName = (TextView) findViewById(R.id.text_garbage_name);
        garbageKind = (TextView) findViewById(R.id.text_garbage_kind);
        garbagePs = (TextView)findViewById(R.id.text_garbage_ps);
        garbagePicture = (ImageView) findViewById(R.id.image_garbage);
        garbageConfidence = (TextView) findViewById(R.id.text_confidence);
        shareChoice = (LinearLayout)findViewById(R.id.share_choice);
        weChatFrientIcon = (ImageView)findViewById(R.id.weixin);
        weChatMomentsIcon = (ImageView)findViewById(R.id.pengyouquan);
        QQFriendsIcon = (ImageView)findViewById(R.id.qq);
        weChatFrientIcon.setOnClickListener(this);
        QQFriendsIcon.setOnClickListener(this);
        weChatMomentsIcon.setOnClickListener(this);

        shareChoice.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_toolbar);
        setSupportActionBar(toolbar);


        voiceIcon.setOnClickListener(this);
        Tip = (TextView)findViewById(R.id.text_tip);


        String model = getIntent().getStringExtra("model");
        String model02 = getIntent().getStringExtra("model02");
        setModel(model);                  //非历史详情只需要设置一次，历史详情设置2次。
        analysisModel02(model02);         //历史详情具体是哪一种。
        Log.d(TAG, "调试： model: "+model);
        Log.d(TAG, "调试： model02: "+model02);
        responseList = getIntent().getStringArrayListExtra("garbage_info");
        cityName = getIntent().getStringExtra("cityName");
        audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);              //用于语音播放中


        //准备好要保留到数据库的变量
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        searchTime = formatter.format(date);
        if (isImage){
            //如果是图片，还要获得可信度, 路径。
            confidence = responseList.get(4);
            photoPath = responseList.get(5);
        }else if (isVoice){
            wavFilePath = responseList.get(5);
            Log.d(TAG, "调试： onCreate: wav: "+wavFilePath);
        }
        Log.d(TAG, "调试： show里面wav： "+wavFilePath);

//        File temp = new File(photoPath);
//        Log.d(TAG, "调试： 存在？"+temp.exists());
        setContent();                   //设置界面内容


    }


    public void setModel(String model){
        switch (model){
            case "text":
                isText = true;
                voiceIcon.setVisibility(View.INVISIBLE);
                garbagePicture.setVisibility(View.INVISIBLE);
                garbageConfidence.setVisibility(View.INVISIBLE);
                Tip.setVisibility(View.VISIBLE);
                break;

            case "image":
                isImage = true;
                voiceIcon.setVisibility(View.INVISIBLE);
                garbagePicture.setVisibility(View.VISIBLE);
                Tip.setVisibility(View.INVISIBLE);
                garbageConfidence.setVisibility(View.VISIBLE);
                break;

            case "voice":
                isVoice = true;
                voiceIcon.setVisibility(View.VISIBLE);
                garbagePicture.setVisibility(View.INVISIBLE);
                Tip.setVisibility(View.VISIBLE);
                garbageConfidence.setVisibility(View.INVISIBLE);
                break;

            case "history":
                isHistory = true;
            default:
                break;

        }
    }

    //分析历史详情是文本，图片，还是语音。
    public void analysisModel02(String model02){
        if (!model02.equals("NO")){
            setModel(model02);  //再次调用获得模式
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        menu.findItem(R.id.share).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                clickShareCount++;


                //展示或隐藏选项
                if (clickShareCount%2==1)
                {
                    View dView = getWindow().getDecorView();
                    dView.setDrawingCacheEnabled(true);
                    dView.buildDrawingCache();
                    Bitmap bitmap = Bitmap.createBitmap(dView.getDrawingCache());
                    if (bitmap!=null){
                        String screenShotName = String.valueOf(System.currentTimeMillis())+".png";
                        String screenShotPath = getExternalCacheDir()+"/"+screenShotName;
                        screenShotFile = new File(screenShotPath);
                        if (!screenShotFile.exists()){
                            try {
                                screenShotFile.createNewFile();
                                FileOutputStream fos = new FileOutputStream(screenShotFile);
                                bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
                                fos.flush();
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    shareChoice.setVisibility(View.VISIBLE);
                }else{
                    shareChoice.setVisibility(View.INVISIBLE);
                }
                break;

            default:
                break;
        }
        return true;
    }

    public void shareToWechatFriend()
    {
        File file = new File("/storage/emulated/0/Android/data/com.tencent.mm");
        if (file.exists())
        {
            ComponentName comp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI");
            Uri imageUri = FileProvider.getUriForFile(this,
                    "com.example.garbagesort.fileprovider",screenShotFile);
            if (screenShotFile.exists()){
                Intent intent = new Intent();
                intent.setComponent(comp);
                intent.putExtra(Intent.EXTRA_STREAM,imageUri);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_SEND);
                startActivity(intent);
            }
        }else {
            Toast.makeText(this,"请在手机上安装微信",Toast.LENGTH_SHORT).show();
        }
    }

    public void shareToWeChatMoments()
    {
        File file = new File("/storage/emulated/0/Android/data/com.tencent.mm");
        if (file.exists()){
            ComponentName comp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareToTimeLineUI");
            Uri imageUri = FileProvider.getUriForFile(this,
                    "com.example.wordtest.fileprovider",screenShotFile);
            if (screenShotFile.exists()){
                Intent intent = new Intent();
                intent.setComponent(comp);
                intent.putExtra(Intent.EXTRA_STREAM,imageUri);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_SEND);
                startActivity(intent);
            }
        }else{
            Toast.makeText(this,"请在手机上安装微信",Toast.LENGTH_SHORT).show();
        }

    }

    public void shareToQQFriend(){
        File file = new File("/storage/emulated/0/Android/data/com.tencent.mobileqq");
        if (file.exists())
        {
            ComponentName comp = new ComponentName("com.tencent.mobileqq","com.tencent.mobileqq.activity.JumpActivity");
            Uri imageUri = FileProvider.getUriForFile(this,
                    "com.example.wordtest.fileprovider",screenShotFile);
            if (screenShotFile.exists()){
                Intent intent = new Intent();
                intent.setComponent(comp);
                intent.putExtra(Intent.EXTRA_STREAM,imageUri);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_SEND);
                startActivity(intent);
            }
        }else{
            Toast.makeText(this,"请在手机上安装QQ",Toast.LENGTH_SHORT).show();
        }
    }



    public void setContent(){
        garbageName.setText("垃圾名称："+responseList.get(1));
        garbageKind.setText("类型："+responseList.get(2));
        garbagePs.setText("投放建议："+responseList.get(3));
        if (isImage){
            displayImage();
            garbageConfidence.setText("可信度： "+responseList.get(4));
        }
    }

    public void displayImage(){
        if (photoPath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            garbagePicture.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {

        Log.d(TAG, "调试： onDestroy:");
        //非历史详情情况才保存到数据库中。
        if (!isHistory){
            NewGarbageDatabase database = Room.databaseBuilder(this,NewGarbageDatabase.class,"NewDatabase04.db")
                    .allowMainThreadQueries()
                    .build();
            GarbageDao garbageDao = database.getGarbageDao();

            Log.d(TAG, "调试 isText: "+isText);
            Log.d(TAG, "调试 isImage: "+isImage);
            Log.d(TAG, "调试 isVoice: "+isVoice);
            super.onDestroy();
            Garbage garbage = new Garbage(null,null,null,null,null,null,null,null);
            garbage.setName(responseList.get(1));
            garbage.setKind(responseList.get(2));
            garbage.setPs(responseList.get(3));
            garbage.setTime(searchTime);
            garbage.setCity(cityName);
            if (isText) garbage.setModel("text");
            if (isImage){
                garbage.setModel("image");
                garbage.setPath(photoPath);
                garbage.setConfidence(responseList.get(4));
            }else if (isVoice){
                garbage.setModel("voice");
                garbage.setPath(wavFilePath);
            }
            garbageDao.insert(garbage);
        }else{
            super.onDestroy();
        }
        Log.e(TAG, "调试： 11111111111111111111111111111" );
    }

//    @Override
//    protected void onDestroy() {
//        //若不调用,历史详情下回返回失败
//         Log.d(TAG, "调试： onDestroy:");
//        //非历史详情情况才保存到数据库中。
//        if (!isHistory){
//            Log.d(TAG, "调试 isText: "+isText);
//            Log.d(TAG, "调试 isImage: "+isImage);
//            Log.d(TAG, "调试 isVoice: "+isVoice);
//            super.onDestroy();
//            dbHelper helper = new dbHelper(this,"SearchHistory.db",null,5);
//            SQLiteDatabase db = helper.getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put("name",responseList.get(1));
//            values.put("kind",responseList.get(2));
//            values.put("ps",responseList.get(3));
//            values.put("time",searchTime);
//            values.put("city",cityName);
//            if (isText) values.put("model","text");
//            if (isImage){
//                values.put("model","image");
//                values.put("path",photoPath);
//                values.put("confidence",responseList.get(4));
//            }else if (isVoice){
//                values.put("model","voice");
//                values.put("path",wavFilePath);
//            }
//            db.insert("Garbage03",null,values);
//            values.clear();
//            helper.close();
//        }else{
//            super.onDestroy();
//        }
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icon_voice:
//                Log.d(TAG, "调试： 进入播放");
//                Log.d(TAG, "调试： 有音乐播放： "+audioManager.isMusicActive());
                if (wavFilePath!=null&&!audioManager.isMusicActive()){
                    //如果文件存在,且没有在播放音乐（防止多次重复播放）
                            initMediaPlayer();
                            mediaPlayer.start();
                            try {
                                TimeUnit.MILLISECONDS.sleep(mediaPlayer.getDuration());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.stop();
                            mediaPlayer.release();
                    Log.d(TAG, "调试： 结束播放");
                }

                break;

            case R.id.weixin:
                shareToWechatFriend();
                shareChoice.setVisibility(View.INVISIBLE);
                break;

            case R.id.pengyouquan:
                shareToWeChatMoments();
                shareChoice.setVisibility(View.INVISIBLE);
                break;

            case R.id.qq:
                shareToQQFriend();
                shareChoice.setVisibility(View.INVISIBLE);
                break;

            default:
                break;

        }
    }


    public void initMediaPlayer(){
        try {
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(wavFilePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
