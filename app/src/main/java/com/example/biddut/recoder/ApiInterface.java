package com.example.biddut.recoder;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by SunMoon Computer on 4/30/2018.
 */

public interface ApiInterface {

    @Multipart
    @POST("file_upload.php")
    Call<ResponseBody> uploaFileToServer(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("due") RequestBody duration);
}
