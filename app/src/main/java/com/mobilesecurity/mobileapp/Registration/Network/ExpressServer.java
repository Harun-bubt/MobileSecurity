package com.mobilesecurity.mobileapp.Registration.Network;


import com.mobilesecurity.mobileapp.Registration.Domain.OtpResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ExpressServer {




    //------------------ Base URL ------------------
    String BASE_URL = "https://api.bestsocialdiary.com/otp/";
    //----------------------------------------------

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();



    //For timeout calculation in calls
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .callTimeout(6, TimeUnit.SECONDS)
            .connectTimeout(6, TimeUnit.SECONDS)
            .readTimeout(6, TimeUnit.SECONDS)
            .writeTimeout(6, TimeUnit.SECONDS)
            .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();



    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    //...............Otp Send.......................

    @GET("mobile.php")
    Call<OtpResponse> getOtpResponse(
            @Query("type") String type,
            @Query("appid") String appid,
            @Query("no") String no,
            @Query("mobile") String mobile

    );
    @GET("mobile.php")
    Call<OtpResponse> getOtpVerification(
            @Query("type") String type,
            @Query("appid") String appid,
            @Query("no") String no,
            @Query("mobile") String mobile
    );



/*    //.........................Get post data From API....................
    @GET("ad/view/{id}")
    Call<ViewMyPostModel> getMyPostData(
            @Path("id") String id
    );

    //......................Get All Ads Markers for map...................

    @GET("ads/map")
    Call<List<UserAdsMapModel>> getAdsMapMarkers(
            @Query("number") String number,
            @Query("mainCategory") String mainCategory,
            @Query("subCategory") String subCategory,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("date") String date
    );

    @GET("ads/map")
    Call<List<UserAdsMapModel>> getAdsMapMarkers(
            @Query("number") String number,
            @Query("mainCategory") String mainCategory,
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("date") String date
    );*/




}