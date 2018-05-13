package com.example.biddut.recoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Ariful Islam on 5/13/2018.
 */

public class NetWorkStateChange extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("NetWorkStateChange","call");
    }
}
