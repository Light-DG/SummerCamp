package com.example.garbagesort.voice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbagesort.Parse;
import com.example.garbagesort.R;
import com.example.garbagesort.show.Show;
import com.google.gson.JsonObject;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;

import org.json.JSONString;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceSearch extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnCancelListener , DialogInterface.OnDismissListener ,VoiceContract.voiceView{

    VoiceContract.voicePresenter presenter;

    ImageView Voice = null;
    int clickVoiceCount = 0;
    MyAudioRecord record =null;
    public static final int SHOW = 1;
    ProgressDialog progressDialog = null;
    public static final int AUDIO_RECORD_PERMISSION = 4;
    private static final int SHOW_OUTTIME_DIALOG = 6;
    private static final int BUTTON_CLICKABLE = 7;
    TextView notice;
    boolean isCanceled = false;
    //开始和结束录制的时间

    long startTime = 0;
    long endTime = 0;
    boolean isOverTime = false;
    Timer timer;

    String cityName;
    boolean alreadyClick = false;

    private static final String TAG = "VoiceSearch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_search);

        JSONString js;

        presenter = new VoicePresenter(this);

        Voice = (ImageView)findViewById(R.id.image_audio_record);
        notice = (TextView)findViewById(R.id.text_notice);
        notice.setVisibility(View.INVISIBLE);

        Voice.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("查询中");
        progressDialog.setMessage("请耐心等候");
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(this);
        progressDialog.setOnDismissListener(this);

        cityName = getIntent().getStringExtra("cityName");
        presenter.setCity(cityName);

        if (ContextCompat.checkSelfPermission(VoiceSearch.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VoiceSearch.this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_RECORD_PERMISSION);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_audio_record:
                isCanceled = false;
                clickVoiceCount++;
                if (clickVoiceCount%2==1){
                    notice.setVisibility(View.VISIBLE);
                    Voice.setImageResource(R.mipmap.luyin2);
                    presenter.SetFileNameAndPath();
                    record = new MyAudioRecord(presenter.getPcmFilePath(),presenter.getWavFielPath());
                    record.recordAndSaveFile();
                    startTime = System.currentTimeMillis();
                    timer = new Timer();
                    timer.schedule(new voiceLengthTask(),0,200);
                    Log.d(TAG, "调试：onClick 开始录制: ");
                }else{
                    timer.cancel();
                    notice.setVisibility(View.INVISIBLE);
                    Voice.setImageResource(R.mipmap.luyin1);
                    record.close();
                    progressDialog.show();
                    if (!isCanceled)
                    {
                        Log.d(TAG, "调试： 再次按下");
                        presenter.requireMessage();
                    }
                }

                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case SHOW:
                    if (!isCanceled&&presenter.getResponseList().get(0).equals("success")){
                        Intent intent = new Intent(VoiceSearch.this, Show.class);
                        intent.putStringArrayListExtra("garbage_info",presenter.getResponseList());
                        intent.putExtra("model","voice");
                        intent.putExtra("model02","NO");
                        intent.putExtra("cityName",cityName);
                        progressDialog.dismiss();
                        startActivity(intent);
                    }else if (!isCanceled&&presenter.getResponseList().get(0).equals("failure")){
                        progressDialog.dismiss();
                        showDialog();
                        timer.cancel();
                    }
                    break;

                case SHOW_OUTTIME_DIALOG:
                    timer.cancel();
                    timer = null;
                    Toast.makeText(VoiceSearch.this,"超时！录制时间最长为30s",Toast.LENGTH_SHORT).show();
                    isOverTime = false;
                    break;

                case BUTTON_CLICKABLE:
                    alreadyClick = false;
                    break;

                default:
                    break;

            }
        }
    };

    public void sendButtonRenewMessage(){
        Message message = new Message();
        message.what = BUTTON_CLICKABLE;
        handler.sendMessageDelayed(message,400);
    }



    public void showDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(VoiceSearch.this);
        dialog.setTitle("查询失败");
        dialog.setMessage("未能找到匹配物品");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        //若用户取消查询，则删除录音文件
        isCanceled = true;
        File file1 = new File(presenter.getPcmFilePath());
        File file2 = new File(presenter.getWavFielPath());
        if (file1.exists()){
            file1.delete();
        }
        if (file2.exists()){
            file2.delete();
        }
        Toast.makeText(VoiceSearch.this,"取消查询",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }


    public void showResult(){
        Message message = new Message();
        message.what=SHOW;
        handler.sendMessage(message);
    }


    class voiceLengthTask extends TimerTask{

        @Override
        public void run() {
            endTime = System.currentTimeMillis();
            if (endTime-startTime>30*1000){
                isOverTime = true;
                clickVoiceCount++;
                record.close();
                File file1 = new File(presenter.getPcmFilePath());
                File file2 = new File(presenter.getWavFielPath());
                if (file1.exists()){
                    file1.delete();
                }
                if (file2.exists()){
                    file2.delete();
                }
                notice.setVisibility(View.INVISIBLE);
                Voice.setImageResource(R.mipmap.luyin1);
                Message message = new Message();
                message.what = SHOW_OUTTIME_DIALOG;
                handler.sendMessage(message);
            }
        }
    }


}
