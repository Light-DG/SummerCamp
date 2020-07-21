package com.example.garbagesort.voice;

import android.os.Environment;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.garbagesort.API;
import com.example.garbagesort.MyApplication;
import com.example.garbagesort.Parse;
import com.example.garbagesort.R;
import com.example.garbagesort.image.ImagePresenter;
import com.example.garbagesort.result.Result;
import com.google.gson.Gson;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class VoicePresenter implements VoiceContract.voicePresenter {
    private static final String TAG = "VoicePresenter";

    VoiceContract.voiceView mView;
    ArrayList<String> responseList = null;
    Parse mParse;
    String platform = null;
    String pcmFileName = null;
    String pcmFilePath = null;
    String wavFileName = null;
    String wavFielPath = null;
    String baseUrl = "https://aiapi.jd.com/jdai/garbageVoiceSearch/";
    String cityId;
    String sign;
    long timeStamp;



    public VoicePresenter(VoiceContract.voiceView view){
        mView = view;
        responseList = new ArrayList<String>();
        mParse = new Parse();

    }

    public void sendVoiceRequestAndShow(){
        setSignAndTimeStamp();
        File file = new File(wavFielPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);    //创建requestBody对象
        Map<String,String> headerMap = new HashMap<String, String>();
        headerMap.put("cityId","310000"); //访问参数
        headerMap.put("property","{\"autoend\":false,\"encode\":{\"channel\":1,\"format\":\"wav\",\"sample_rate\":16000,\"post_process\":0},\"platform\":\""+platform+"\",\"version\":\"0.0.0.1\"}"); //访问参数
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        API api = retrofit.create(API.class);
       api.getVoiceSearchResult02(headerMap,requestBody,timeStamp,sign)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Consumer<Result>() {
                   @Override
                   public void accept(Result result) throws Exception {
                       Log.d(TAG, "调试 message-->"+ result.getResult().getMessage());
                       responseList = mParse.textOrVoiceResponseParse(result);
                       responseList.add(wavFielPath);
                       mView.showResult();
                   }
               });

    }





    public ArrayList<String> getResponseList() {
        return responseList;
    }

    public void requireMessage(){
          sendVoiceRequestAndShow();
//        new Thread(new handleVoiceThread02()).start();
    }

    public void SetFileNameAndPath() {
        pcmFileName = String.valueOf(System.currentTimeMillis())+".pcm";
        pcmFilePath = MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString();
        wavFileName = String.valueOf(System.currentTimeMillis()+1)+".wav";
        wavFielPath= MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString();
        pcmFilePath+="/MyRecord03";
        wavFielPath+="/MyRecord04";
        File file1 = new File(pcmFilePath);
        File file3 = new File(wavFielPath);
        if (!file1.isDirectory()){
            file1.mkdir();
        }
        if (!file3.isDirectory()){
            file3.mkdir();
        }
        pcmFilePath+="/"+pcmFileName;
        wavFielPath+="/"+wavFileName;
        File file2 = new File(pcmFilePath);
        File file4 = new File(wavFielPath);
        try {
            file2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            file4.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "调试： wavFilePath "+wavFielPath);
        Log.d(TAG, "调试： wav存在？ "+file4.exists());

    }

    public String getPcmFilePath() {
        return pcmFilePath;
    }

    public String getWavFielPath() {
        return wavFielPath;
    }

    public String setSignAndTimeStamp(){
        String SecretKey = "2e54975929ff45af3f36cc8200af9efc";
        timeStamp = System.currentTimeMillis();
        String temp = SecretKey+timeStamp;
        sign = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(temp.getBytes());
            byte[] digest = md.digest();
            sign = new BigInteger(1, digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "调试： sign: "+sign);
        return sign;

    }

    public void setCity(String s){
        if (s.equals("上海市")){
            cityId = s;
        }else if (s.equals("宁波市")){
            cityId = "330200";
        }else if (s.equals("西安市")){
            cityId = "610100";
        }else if (s.equals("深圳市")){
            cityId = "440300";
        }else if (s.equals("北京市")){
            cityId = "110000 ";
        }
    }

    //用京东的sdk
//    class handleVoiceThread implements Runnable{
//        @Override
//        public void run() {
//            RequestModel model = new RequestModel();
//            model.setGwUrl("https://aiapi.jd.com/jdai/garbageVoiceSearch");
//            model.setAppkey("f6f588dd5b849c2dee78d9f52671fa79");
//            model.setSecretKey("2e54975929ff45af3f36cc8200af9efc");
//            model.setFilePath(wavFielPath);
//            Map queryMap = new HashMap();
//            queryMap.put("cityId","310000"); //访问参数
//            queryMap.put("property","{\"autoend\":false,\"encode\":{\"channel\":1,\"format\":\"wav\",\"sample_rate\":16000,\"post_process\":0},\"platform\":\""+platform+"\",\"version\":\"0.0.0.1\"}"); //访问参数
//            model.setQueryParams(queryMap);
//            WxApiCall call = new WxApiCall();
//            call.setModel(model);
//            String ret = call.request();
//            Log.d(TAG, "调试 ret: "+ret);
//            responseList = mParse.textOrVoiceResponseParse(ret);
//            responseList.add(wavFielPath);
//            Log.d(TAG, "调试： run: 线程里： wav"+wavFielPath);
//
//            mView.showResult();
//
//        }
//
//    }

    //     Retrofit + Gson
//    class handleVoiceThread02 implements Runnable{
//        @Override
//        public void run() {
//            setSignAndTimeStamp();
//            File file = new File(wavFielPath);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);    //创建requestBody对象
//            Map<String,String> headerMap = new HashMap<String, String>();
//            headerMap.put("cityId","310000"); //访问参数
//            headerMap.put("property","{\"autoend\":false,\"encode\":{\"channel\":1,\"format\":\"wav\",\"sample_rate\":16000,\"post_process\":0},\"platform\":\""+platform+"\",\"version\":\"0.0.0.1\"}"); //访问参数
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            API api = retrofit.create(API.class);
//            Call<Result> call = api.getVoiceSearchResult(headerMap,requestBody,timeStamp,sign);
//            call.enqueue(new Callback<Result>() {
//                @Override
//                public void onResponse(Call<Result> call, Response<Result> response) {
//                    Result result = response.body();
//                    responseList = mParse.textOrVoiceResponseParse(result);
//                    responseList.add(wavFielPath);
//                    mView.showResult();
//                }
//
//                @Override
//                public void onFailure(Call<Result> call, Throwable t) {
//                    Log.d(TAG, "调试： onFailure: voice -->" + t);
//                }
//            });
//
//        }
//
//    }


}
