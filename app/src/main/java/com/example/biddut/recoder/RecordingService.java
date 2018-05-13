package com.example.biddut.recoder;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.arasthel.asyncjob.AsyncJob;
import com.example.biddut.recoder.database.DBTablesInfo;
import com.example.biddut.recoder.database.DatabaseHelper;
import com.example.biddut.recoder.model.DataReceiveEvent;
import com.example.biddut.recoder.model.VoiceToText;
import com.example.biddut.recoder.utils.NetworkConnection;
import com.example.biddut.recoder.utils.Validation;
import com.google.gson.Gson;

import org.chalup.microorm.MicroOrm;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import java.util.ArrayList;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecordingService extends Service {

    private MediaRecorder mediaRecorder;
    private String voiceStoragePath;
    public static final String BASE_URL = "http://192.168.0.101/api/";
    static final String AB = "abcdefghijklmnopqrstuvwxyz";
    static Random rnd = new Random();

    MediaPlayer mediaPlayer;

    private static final String TAG="biddut";
   String LOG_TAG="RecordingService";
  public static SpeechRecognizer mSpeechRecognizer;
    public static   Intent mSpeechRecognizerIntent;
    private ApiInterface apiInterface;
    DatabaseHelper db;
    boolean isReceived=false;

boolean isSent=false;
    String TextMessage="";
    VoiceToText voiceToText=new VoiceToText();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand","");
        TextMessage="";
        isSent=false;
        apiInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface.class);
       showForegroundNotification("Start Service");
        EventBus.getDefault().register(this);
       // StartRecording();
        StartRecordVoice();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy","call");
        stopAudioPlay();
        EventBus.getDefault().unregister(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DataReceiveEvent event) throws ClassNotFoundException {
        Log.e("On data_received", "call--- "+new Gson().toJson(event));

        if (event.isTagMatchWith("data_received")) {
            voiceToText=event.getResponseMessage();
            Log.e("mSpeechRecognizer","stop");
            isReceived=true;
            stopAudioPlay();
         //   mSpeechRecognizer.stopListening();

          //  Save();

         //  data.setText(event.getResponseMessage().getArticles().get(0).getText());
        }

    }
    private void StartRecordVoice()
    {
        hasSDCard();
        voiceStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File audioVoice = new File(voiceStoragePath + File.separator + "voices");
        if(!audioVoice.exists()){
            audioVoice.mkdir();
        }
        voiceStoragePath = voiceStoragePath + File.separator + "voices/" + generateVoiceFilename(6) + ".3gpp";
        System.out.println("Audio path : " + voiceStoragePath);
        if(mediaRecorder == null){
            initializeMediaRecord();

        }

    }
    private void hasSDCard(){
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(isSDPresent)        {
            System.out.println("There is SDCard");
        }
        else{
            System.out.println("There is no SDCard");
        }
    }
    private void initializeMediaRecord(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(voiceStoragePath);
        startAudioRecording();
    }
    private void startAudioRecording(){
        try {
            //if(mediaRecorder.is)
//            mediaRecorder.stop();
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {

            Log.e("mediaRecorder","ex"+e.getMessage());
            e.printStackTrace();
        }

    }
    private void stopAudioPlay(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Save();
    }
    private String generateVoiceFilename( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
private void StartRecording()
{
    mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());


//    mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//    //mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn");
//    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en");
//    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplicationContext().getPackageName());
//   // mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
//    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

    mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
            "bn");

    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
Log.e("mSpeechRecognizer","start");
    mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.i(LOG_TAG, "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.i(LOG_TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.i(LOG_TAG, "onBufferReceived: " + bytes);
        }

        @Override
        public void onEndOfSpeech() {
            Log.i(LOG_TAG, "onEndOfSpeech");
            if(isReceived) {
                voiceToText.text = TextMessage;
                Save();
            }
            else
            {
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            }
        }

        @Override
        public void onError(int i) {
            Log.d(LOG_TAG, "FAILED " + i+"   "+TextMessage);
            if(isReceived) {
                voiceToText.text = TextMessage;
                Save();
            }
            else
            {
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);

            }

        }

        @Override
        public void onResults(Bundle bundle) {
            //getting all the matches
            ArrayList<String> matches = bundle
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            //displaying the first match
            if (matches != null)
            {
                TextMessage=TextMessage.concat("."+matches.get(0));
            if(isReceived) {
                voiceToText.text = TextMessage;
                Save();
            }
            else
            {
                mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
            }
            Log.e("TextMessage.",TextMessage+"");


        }

        @Override
        public void onPartialResults(Bundle bundle) {
            Log.i(LOG_TAG, "onPartialResults");
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.i(LOG_TAG, "onEvent");
        }
    });

}
    public void Save()
    {
        if(isSent)
            return;
        isSent=true;
        new AsyncJob.AsyncJobBuilder<Boolean>()
                .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                    @Override
                    public Boolean doAsync() {
                        ArrayList<ContentValues> contentValues = new ArrayList<>();
                        try {

                                Log.e("SurveyResponseInfo",""+new Gson().toJson(voiceToText));
                               voiceToText.audio_file=voiceStoragePath;
                               voiceToText.status=0;
                                MicroOrm uOrm = new MicroOrm();
                                ContentValues values = uOrm.toContentValues(voiceToText);
                                contentValues.add(values);

                            try {
                                boolean flag;
                                db = new DatabaseHelper(getApplicationContext());
                                flag = db.addItems(DBTablesInfo.MESSAGE_TABLE_NAME, contentValues);
                         Log.e("flag",""+flag);

                            }catch (Exception e)
                            {
                                Log.e("Exception2",""+e.getMessage()+"");
                            }
                            //questionInfos.clear();

                        } catch (Exception e) {

                            Log.e("Exception3",""+e.getMessage()+"");
                            //ToastyMsg.Error(getActivity(),"Something went wrong");
                        }
                        return true;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                        Intent in = new Intent(getApplicationContext(), DataSyncService.class);
                        if(!Validation.isServiceRunning(getApplicationContext(),DataSyncService.class)) {
                            // mythread. stop();

                            getApplicationContext().startService(in);
                            //  Thread.interrupt();
                            // StopTread();
                        }
Log.e("doWhenFinished","call");
                        stopSelf();
                     //   sendVoiceToServer(voiceToText);

                        // Toast.makeText(context, "Result was: " + result, Toast.LENGTH_SHORT).show();
                    }
                }).create().start();

    }



    private void showForegroundNotification(String contentText) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent();
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();

        startForeground(1, notification);
    }
    public void sendVoiceToServer(final  VoiceToText voiceToText) {


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
             int   flag = db.UpdateValue(voiceToText.id, DBTablesInfo.STATUS,DBTablesInfo.MESSAGE_TABLE_NAME);
            Log.e("update",""+flag);
                try {
                //    Toast.makeText(getApplicationContext(), "Success"+response.body().string(), Toast.LENGTH_LONG).show();
                  //  file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                stopSelf();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                stopSelf();
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
