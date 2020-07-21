package com.example.garbagesort;

import android.util.Log;

import com.example.garbagesort.result.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Parse {
    private static final String TAG = "Parse";

    public ArrayList<String> linkWordParse(String jsonData) {
        ArrayList<String> wordList = new ArrayList<String>();
        wordList.clear();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String temp = jsonObject.getString("result");
            JSONArray array = new JSONArray(temp);
            for (int i = 0; i < array.length(); i++) {
                JSONArray array1 = array.getJSONArray(i);
                String s1 = array1.getString(0);
                wordList.add(s1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wordList;
    }

    public ArrayList<String> hotWordParse(String jsonData) {
        ArrayList<String> hotWordList = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            String temp = jsonObject.getString("newslist");
            JSONArray array = new JSONArray(temp);
            for (int i=0;i<array.length();i++){
                if (i>9) break;
                JSONObject object = array.getJSONObject(i);
                String s = ""+(i+1)+"  ";
                s+=object.getString("name");
                hotWordList.add(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hotWordList;
    }

    //Retrofit + GSON
    public ArrayList<String> textOrVoiceResponseParse(Result result){
        ArrayList<String> retList = new ArrayList<String>();
        int remainCount = result.getRemain(); //获得保留的次数
        if (remainCount>=0){
            String message = result.getResult().getMessage();
            if (message != null && message.equals("success")){
                retList.add("success");
                Result.ResultBean.GarbageInfoBean bean = result.getResult().getGarbage_info().get(0);
                retList.add(bean.getGarbage_name());
                retList.add(bean.getCate_name());
                retList.add(bean.getPs());
                retList.add("100%");
                return retList;
            }else{
                retList.add("failure");
                return retList;
            }
        }else{
            retList.add("remainCount=0");
            return retList;
        }
    }

    //Retrofit + GSON
    public ArrayList<String> ImageResponseParse(Result result){
        ArrayList<String> retList = new ArrayList<String>();
        int remainCount = result.getRemain();
        if (remainCount >= 0){
            String message = result.getResult().getMessage();
            if (message.equals("success")){
                retList.add("success");
                List<Result.ResultBean.GarbageInfoBean> list;
                list = result.getResult().getGarbage_info();
                double max = -999;
                int maxIndex = 0;
                for (int i = 0;i<list.size();i++){
                    double confidence = list.get(i).getConfidence();
                    BigDecimal a = new BigDecimal(confidence);
                    BigDecimal b = new BigDecimal(max);
                    if (a.compareTo(b) == 1){
                        max = confidence;
                        maxIndex = i;
                    }
                }
                Result.ResultBean.GarbageInfoBean bean = result.getResult().getGarbage_info().get(maxIndex);
                retList.add(bean.getGarbage_name());
                retList.add(bean.getCate_name());
                retList.add(bean.getPs());
                retList.add(String.valueOf(bean.getConfidence()*100)+"%");
                return retList;
            }else{
                retList.add("failure");
                return retList;
            }
        }
        return retList;
    }

    //用JSON解析
//    public ArrayList<String> textOrVoiceResponseParse(String jsonData){
//        ArrayList<String> retList = new ArrayList<String>();
//        try {
//            JSONObject jsonObject = new JSONObject(jsonData);
//            int remainCount = jsonObject.getInt("remain"); //获得保留的次数
//            String result = jsonObject.getString("result");
//            if (remainCount>=0){
//                JSONObject object = new JSONObject(result);
//                String message = object.getString("message");
//                if (message.equals("success")){
//                    retList.add("success");
//                    JSONArray array = object.getJSONArray("garbage_info");
//                    for (int i =0;i<array.length();i++){
//                        JSONObject minObject = array.getJSONObject(i);
//                        String name = minObject.getString("garbage_name");
//                        String kind = minObject.getString("cate_name");
//                        String ps = minObject.getString("ps");
//                        double confidence = minObject.getDouble("confidence");
//                        confidence*=100;
//                        retList.add(name);
//                        retList.add(kind);
//                        retList.add(ps);
//                        retList.add(String.valueOf(confidence)+"%");
//                    }
//                    return retList;
//                }else{
//                    retList.add("failure");
//                    return retList;
//                }
//            }else{
//                retList.add("remainCount=0");
//                return retList;
//            }
//
//            }catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return retList;
//    }

    //用JSON解析
//    public ArrayList<String> ImageResponseParse(String jsonData){
//        ArrayList<String> retList = new ArrayList<String>();
//        try {
//            JSONObject jsonObject = new JSONObject(jsonData);
//            int remainCount = jsonObject.getInt("remain"); //获得保留的次数
//            String result = jsonObject.getString("result");
//            if (remainCount>=0){
//                JSONObject object = new JSONObject(result);
//                String message = object.getString("message");
//                if (message.equals("success")){
//                    retList.add(message);
//                    JSONArray array = object.getJSONArray("garbage_info");
//                    double best = 0;
//                    int bestIndex = 0;
//                    Log.d(TAG, "调试： 数组长度： "+array.length());
//                    for (int i =0;i<array.length();i++){
//                        JSONObject minObject = array.getJSONObject(i);
//                        double confidence = minObject.getDouble("confidence");
//                        Log.d(TAG, "调试 confidence: "+confidence);
////                        if (confidence>0.5){
////                            bestIndex = i;
////                            break;
////                        }
//                        BigDecimal a = new BigDecimal(confidence);
//                        BigDecimal b = new BigDecimal(best);
//                        if (a.compareTo(b)==1)
//                        {
//                            best = confidence;
//                            bestIndex=i;
//                        }
//                    }
//                    JSONObject bestObject = array.getJSONObject(bestIndex);
//                    String name = bestObject.getString("garbage_name");
//                    String kind = bestObject.getString("cate_name");
//                    String ps = bestObject.getString("ps");
//                    double confidence = bestObject.getDouble("confidence");
//                    confidence*=100;
//                    retList.add(name);
//                    retList.add(kind);
//                    retList.add(ps);
//                    retList.add(String.valueOf(confidence)+"%");
//                    return retList;
//                }else{
//                    retList.add("failure");
//                    return retList;
//                }
//            }else{
//                retList.add("remainCount=0");
//                return retList;
//            }
//
//        }catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return retList;
//    }

}
