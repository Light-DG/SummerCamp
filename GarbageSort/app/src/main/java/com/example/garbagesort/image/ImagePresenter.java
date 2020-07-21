package com.example.garbagesort.image;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.example.garbagesort.API;
import com.example.garbagesort.MyApplication;
import com.example.garbagesort.Parse;
import com.example.garbagesort.result.Result;
import com.google.gson.Gson;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImagePresenter implements ImageContract.imagePresent {
    private static final String TAG = "ImagePresenter";

    ImageContract.imageView mview;
    Parse myParse;
    ArrayList<String> responseList;
    String baseUrl;
    long timeStamp;
    String sign;
    String cityId;
    String appkey = "f6f588dd5b849c2dee78d9f52671fa79";

    public ImagePresenter(ImageContract.imageView view) {
        mview = view;
        myParse = new Parse();
        responseList = new ArrayList<String>();
    }

     public void handleAndSendImageRequest (){
         //设置接口需要的参数 sign + timeStamp
         setSignAndTimeStamp();
         //获得最新一张图片的Base64字符串
         String base64 = getBase64(mview.getPhotoPath());
         Map<String,String> bodyMessage = new HashMap<String,String>();
         bodyMessage.put("imgBase64",base64);
         bodyMessage.put("cityId",cityId);
         Retrofit retrofit = new Retrofit.Builder()
                 .baseUrl(baseUrl)
                 .addConverterFactory(GsonConverterFactory.create(new Gson()))
                 .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                 .build();
         API api = retrofit.create(API.class);
         api.getImageSearchResult02(bodyMessage,timeStamp,sign,appkey)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(new Consumer<Result>() {
                     @Override
                     public void accept(Result result) throws Exception {
                         Log.d(TAG, "调试： message--> "+ result.getResult().getMessage());
                         responseList = myParse.ImageResponseParse(result);
                         responseList.add(mview.getPhotoPath());     //放入最新拍的图片的路径
                         mview.resultShow();
                     }
                 });
     }




    public String getBase64(String path){
        File file = new File(path);
        Log.d(TAG, "调试：path "+path);
        Log.d(TAG, "调试： 存在？ "+file.exists());
        InputStream is = null;
        byte[] temp = null;
        try {
            is = new FileInputStream(file);
            temp = new byte[is.available()];
            is.read(temp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is!=null)
                {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String ans = Base64.encodeToString(temp,Base64.DEFAULT);
        return ans;
    }

    public ArrayList<String> getResponseList() {
        return responseList;
    }

    public void handlePhoto(){
          handleAndSendImageRequest();
//        new Thread(new handleImageThread02()).start();
    }

    public String setSignAndTimeStamp(){
        baseUrl = "https://aiapi.jd.com/jdai/garbageImageSearch/";
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

    //借用京东sdk的写法
    //转换成Base64,网络请求,请求展示
//    class handleImageThread implements Runnable{
//        @Override
//        public void run() {
//            //获得最新一张图片的Base64字符串
//            String base64 = getBase64(mview.getPhotoPath());
//            RequestModel model = new RequestModel();
//            model.setGwUrl("https://aiapi.jd.com/jdai/garbageImageSearch");
//            model.setAppkey("f6f588dd5b849c2dee78d9f52671fa79");
//            model.setSecretKey("2e54975929ff45af3f36cc8200af9efc");
//            model.setBodyStr("{ \"cityId\":\"310000\",\"imgBase64\":\""+base64+"\"}");	//body参数
//            Map queryMap = new HashMap();
//            model.setQueryParams(queryMap);
//            WxApiCall call = new WxApiCall();
//            call.setModel(model);
//            Log.d(TAG, "调试： 申请前");
//            String ret = call.request();
//            Log.d(TAG, "调试： ret "+ret);
//            responseList = myParse.ImageResponseParse(ret);
//            responseList.add(mview.getPhotoPath());     //放入最新拍的图片的路径
//            mview.resultShow();
//
//
//
//        }
//    }

    //转换成Base64,网络请求,请求展示,Retrofit的写法
//    class handleImageThread01 implements Runnable{
//        @Override
//        public void run() {
//            setSignAndTimeStamp();
//            //获得最新一张图片的Base64字符串
//            String base64 = getBase64(mview.getPhotoPath());
//            Map<String,String> bodyMessage = new HashMap<String,String>();
//            bodyMessage.put("imgBase64",base64);
//            bodyMessage.put("cityId",cityId);
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            API api = retrofit.create(API.class);
//            Call<ResponseBody> call = api.getImageSearachResult(bodyMessage,timeStamp,sign,appkey);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        String ret;
//                        ret = response.body().string();
//                        responseList = myParse.ImageResponseParse(ret);
//                        responseList.add(mview.getPhotoPath());     //放入最新拍的图片的路径
//                        mview.resultShow();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Log.d(TAG, "调试： onFailure"+t);
//                }
//            });
//
//        }
//    }

    //GSON 结合 retrofit
//    class handleImageThread02 implements Runnable{
//        @Override
//        public void run() {
//            setSignAndTimeStamp();
//            //获得最新一张图片的Base64字符串
//            String base64 = getBase64(mview.getPhotoPath());
//            Map<String,String> bodyMessage = new HashMap<String,String>();
//            bodyMessage.put("imgBase64",base64);
//            bodyMessage.put("cityId",cityId);
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
//                    .build();
//            API api = retrofit.create(API.class);
//            Call<Result> call = api.getImageSearchResult(bodyMessage,timeStamp,sign,appkey);
//            call.enqueue(new Callback<Result>() {
//                @Override
//                public void onResponse(Call<Result> call, Response<Result> response) {
//                    Result result = response.body();
//                    Log.d(TAG, "调试： 图像结果： "+result.getResult().getGarbage_info().get(0).getPs());
//                    responseList = myParse.ImageResponseParse(result);
//                    responseList.add(mview.getPhotoPath());     //放入最新拍的图片的路径
//                    mview.resultShow();
//                }
//
//                @Override
//                public void onFailure(Call<Result> call, Throwable t) {
//                    Log.d(TAG, "调试： onFailure --> "+t);
//                }
//            });
//
//        }
//    }

}
