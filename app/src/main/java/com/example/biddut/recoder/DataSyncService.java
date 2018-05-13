package com.example.biddut.recoder;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;


import com.example.biddut.recoder.database.DBTablesInfo;
import com.example.biddut.recoder.database.DatabaseHelper;
import com.example.biddut.recoder.model.VoiceToText;
import com.example.biddut.recoder.utils.NetworkConnection;
import com.google.gson.Gson;

import org.chalup.microorm.MicroOrm;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dream71 on 03/07/2017.
 */

public class DataSyncService extends IntentService {
String Tag="DataSyncService";
DatabaseHelper db;
public static boolean flag=true;
public static boolean profile=true;
public static boolean report=true;
    public static final String BASE_URL = "http://192.168.0.101/api/";
public static boolean attendance=true;
    private ApiInterface apiInterface;
    Context context;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(Tag+"","onStartCommand call");
       // AccountInfo.initializeUserInfo(getApplicationContext());
        context=this;
        db = new DatabaseHelper(context);
        apiInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
      //  showForegroundNotification("Start Service");
       // EventBus.getDefault().register(this);
        SyncRecord();


        return START_NOT_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    public DataSyncService() {
        super("DataSyncService");

    }
    String attendanceId;
    private  void SyncRecord()
    {


        Cursor cursor2 = db.getAllItemsUnSyne(DBTablesInfo.MESSAGE_TABLE_NAME);
        MicroOrm uOrm = new MicroOrm();
        List<VoiceToText> someObjects = uOrm.listFromCursor(cursor2, VoiceToText.class);
        if(NetworkConnection.getConnectivityStatusBoolean(context)&&someObjects!=null&&someObjects.size()>0) {

            if (someObjects.size()>0) {

               // int delete=db.deleteItems(DBTables.ATTENDANCE_TABLE_NAME,DBTables.ID_KEY,someObjects.get(i).id);
                //uploadCustomerImage(someObjects.get(i));

                Send(someObjects.get(0));
               // Log.e("attendence data", delete+new Gson().toJson(someObjects.get(i)));

            }
            else
            {
                stopSelf();
            }
            Log.e("total size 2", "" + someObjects.size());

        }
        else {

            stopSelf();
            Log.e(Tag + "", "sent all ");
        }
    }
    private  void Send(  VoiceToText voiceToText)
    {

     final String id=voiceToText.id="";
        if(NetworkConnection.getConnectivityStatusBoolean(getApplicationContext())) {

            String callerName = voiceToText.name;
            String durationStr = voiceToText.datetime;
            //Original file
            final File file = new File(voiceToText.audio_file);

            RequestBody body = RequestBody.create(MediaType.parse("audio"), file);

            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), callerName);
            RequestBody duration = RequestBody.create(MediaType.parse("text/plain"), durationStr);

            MultipartBody.Part filePart = MultipartBody.Part.createFormData("audio", file.getName(), body);

            Call<ResponseBody> call = apiInterface.uploaFileToServer(filePart, nameBody, duration);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    db = new DatabaseHelper(getApplicationContext());
                    int   flag = db.UpdateValue(id ,DBTablesInfo.STATUS,DBTablesInfo.MESSAGE_TABLE_NAME);
                    Log.e("update",""+flag);
                    SyncRecord();
                    try {
                        //    Toast.makeText(getApplicationContext(), "Success"+response.body().string(), Toast.LENGTH_LONG).show();
                        //  file.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    //Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
                }
            });


        }
        else
        {
            stopSelf();
        }
    }



}
