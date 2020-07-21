package com.example.garbagesort.text;

import android.util.Log;

import com.example.garbagesort.API;
import com.example.garbagesort.Parse;
import com.example.garbagesort.result.Result;
import com.google.gson.Gson;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.security.AlgorithmConstraints;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;

public class TextPresenter implements TextContract.textPresenter{
    private static final String TAG = "TextPresenter";

    TextContract.textView mView;
    Parse mParse;
    ArrayList<String> responseList = null;
    String cityId;
    String nowText="";
    ArrayList<String> datalist;
    String baseUrl = "https://aiapi.jd.com/jdai/garbageTextSearch/";
    String sign;
    long timeStamp;

    public TextPresenter(TextContract.textView view){
        mView = view;
        mParse = new Parse();
        responseList = new ArrayList<String>();
        datalist = new ArrayList<String>();
    }

    //Retrofit + Gson + RxJava
    public void sendTextRequestAndShow(){
        setSignAndTimeStamp();
        Map<String, String> map = new HashMap<>();
        map.put("text", nowText);
        map.put("cityId", cityId);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        API api = retrofit.create(API.class);
        api.getTextSearchResult02(map, sign, timeStamp)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Result>() {
                    @Override
                    public void accept(Result result) throws Exception {
                        responseList = mParse.textOrVoiceResponseParse(result);
                        mView.show(responseList.get(0));
                    }
                });
    }


    class getWordThread implements Runnable{

        @Override
        public void run() {
            OkHttpClient client = new OkHttpClient();
            String url = "https://api.zhetaoke.com:10001/api/api_suggest.ashx?appkey=36adb8e28c394906b3e01fba444b7121&content=";
            String content = mView.getNowText();
            url+=content;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String res = response.body().string();
                datalist.clear();
                datalist=mParse.linkWordParse(res);
                mView.updateAdapter(datalist);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public ArrayList<String> getDataList() {
        return datalist;
    }

    public void requireLinkWord(){
        new Thread(new getWordThread()).start();
    }


    public void setCityId(String cityId){
        this.cityId = cityId;
    }

    public void setNowText(String nowText){
        this.nowText = nowText;
    }

    public void sendRequest(){
        sendTextRequestAndShow();
//        new Thread(new sendRequestThread02()).start();
    }

    public ArrayList<String> getResponseList() {
        return responseList;
    }

    public void setSignAndTimeStamp(){
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

    }

    //京东sdk的写法
    //    class sendRequestThread implements Runnable{
//
//        @Override
//        public void run() {
//            Log.d(TAG, "调试： 线程中");
//            RequestModel model = new RequestModel();
//            model.setGwUrl("https://aiapi.jd.com/jdai/garbageTextSearch");
//            model.setAppkey("f6f588dd5b849c2dee78d9f52671fa79");
//            model.setSecretKey("2e54975929ff45af3f36cc8200af9efc");
//            model.setBodyStr("{  \"cityId\":"+cityId+",  \"text\":\""+nowText+"\" }");
//            Map queryMap = new HashMap();
//            model.setQueryParams(queryMap);
//            WxApiCall call = new WxApiCall();
//            call.setModel(model);
//            String response = call.request();
//            Log.d(TAG, "调试： 文本查询返回： "+response);
//            responseList.clear();
//            responseList = mParse.textOrVoiceResponseParse(response);
//
//            Log.d(TAG, "调试： run: 展示前 "+responseList.get(0));
//            mView.show(responseList.get(0));
//
//
//        }
//    }

    //Retrofit + Gson
//    class sendRequestThread02 implements Runnable{
//
//        @Override
//        public void run() {
//            setSignAndTimeStamp();
//            Map<String,String> map = new HashMap<>();
//            map.put("text",nowText);
//            map.put("cityId",cityId);
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
//                    .build();
//            API api = retrofit.create(API.class);
//            Call<Result> call = api.getTextSearchResult(map,sign,timeStamp);
//            call.enqueue(new Callback<Result>() {
//                @Override
//                public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
//                    if (response.body() != null){
//                        Result result = response.body();
//                        responseList.clear();
//                        responseList = mParse.textOrVoiceResponseParse(result);
//                        mView.show(responseList.get(0));
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<Result> call, Throwable t) {
//                    Log.d(TAG, "调试：文本 onFailure --->" + t.toString());
//                }
//            });
//
//
//        }
//    }


}
