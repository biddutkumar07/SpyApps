package com.example.biddut.recoder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SunMoon Computer on 4/30/2018.
 */

public class ApiClient {

    public static final String BASE_URL = "http://192.168.0.101/api/";
    public static Retrofit retrofit = null;

    public static Retrofit getRetrofit(){

        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}