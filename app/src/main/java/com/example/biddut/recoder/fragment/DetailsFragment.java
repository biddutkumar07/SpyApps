package com.example.biddut.recoder.fragment;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.biddut.recoder.MainActivity;
import com.example.biddut.recoder.R;

import java.io.IOException;


public class DetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
String text;
TextView stop;
    MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private String voiceStoragePath;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_details, container, false);
        stop=(TextView)view.findViewById(R.id.stop);
        MainActivity.titleBar.setText("Detail");
        Bundle args = getArguments();
        if (args != null)
            voiceStoragePath=   args.getString("Name");
        Log.e("voiceStoragePath",""+voiceStoragePath);
//        details.setText(text);
      //  details.setVisibility(View.GONE);
        MainActivity.titleBar.setText("Details");
        MainActivity.shouldShowBackButton(true);
        playLastStoredAudioMusic();
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("mediaPlayer","stop");
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });
        return view;
    }
    private void playLastStoredAudioMusic(){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(voiceStoragePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

    }


}
