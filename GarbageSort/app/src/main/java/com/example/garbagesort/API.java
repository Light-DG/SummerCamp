package com.example.garbagesort;

import com.example.garbagesort.result.Result;

import java.util.Map;


import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

        //下三个用了Gson + Retrofit
//    @POST("?appkey=f6f588dd5b849c2dee78d9f52671fa79")
//    Call<Result> getVoiceSearchResult01(@HeaderMap Map<String,String> headerMessage, @Body RequestBody file, @Query("timestamp") long timeStamp, @Query("sign") String sign);

//    @Headers("Content-Type:application/json;charset=UTF-8")
//    @POST(".")
//    Call<Result> getImageSearchResult01(@Body Map bodyMessage, @Query("timestamp") long timeStamp, @Query("sign") String sign, @Query("appkey") String appkey);


//    @Headers("Content-Type:application/json;charset=UTF-8")
//    @POST("?appkey=f6f588dd5b849c2dee78d9f52671fa79")
//    Call<Result> getTextSearchResult01(@Body Map bodyMessage , @Query("sign") String sign,@Query("timestamp") long timeStamp);

    //下面这三个用了Gson + Retrofit + RxJava
    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST("?appkey=f6f588dd5b849c2dee78d9f52671fa79")
    Observable<Result> getTextSearchResult02(@Body Map bodyMessage , @Query("sign") String sign, @Query("timestamp") long timeStamp);

    @POST("?appkey=f6f588dd5b849c2dee78d9f52671fa79")
    Observable<Result> getVoiceSearchResult02(@HeaderMap Map<String,String> headerMessage, @Body RequestBody file, @Query("timestamp") long timeStamp, @Query("sign") String sign);

    @Headers("Content-Type:application/json;charset=UTF-8")
    @POST(".")
    Observable<Result> getImageSearchResult02(@Body Map bodyMessage, @Query("timestamp") long timeStamp, @Query("sign") String sign, @Query("appkey") String appkey);

}
