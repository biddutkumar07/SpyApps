package com.example.biddut.recoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.biddut.recoder.utils.NetworkConnection;
import com.example.biddut.recoder.utils.Validation;




public class NetworkChangeReceiver extends BroadcastReceiver {
    Context context;
boolean flag=true;

    public NetworkChangeReceiver() {
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        String status = NetworkConnection.getConnectivityStatusBoolean(context)+"";
        //NetworkConnection.getConnectivityStatusBoolean(getActivity())
      //  Toast.makeText(context, status, Toast.LENGTH_LONG).show();
        Log.e("status"," fdtf "+status+"  "+NetworkConnection.NETWORK_STATE);
       // Toast.makeText(context, status, Toast.LENGTH_LONG).show();
      //  if (AppData.getString(AppData.Acess_Toten, context) != null && !AppData.getData(AppData.Acess_Toten, context).equals("")) {

        Count count= new Count("arif");
        count.start();

    }

    class Count implements Runnable
    {
        Thread mythread ;
        String ThreadName;
        Count( String name) {
            ThreadName = name;
            System.out.println("Creating " +  ThreadName );
        }
        public void start () {
            System.out.println("Starting " +  ThreadName );
            if (mythread == null) {
                mythread = new Thread (this, ThreadName);
                mythread.start ();
            }
        }
        public void run()
        {
            try
            {
                for (int i=0 ;i<20;i++)
                {
                    System.out.println("Printing the count " + i);
                  //  Log.e("count ",""+i);

                    if(NetworkConnection.getConnectivityStatusBoolean(context))
                    {
                        Log.e("contect","connected"+i);
                        Intent in = new Intent(context, DataSyncService.class);
                        if(!Validation.isServiceRunning(context,DataSyncService.class)&&flag) {
                           // mythread. stop();
                            flag=false;
                            context.startService(in);
                          //  Thread.interrupt();
                           // StopTread();
                        }
                        else
                        {
                            if(flag) {
                                context.stopService(in);
                                context.startService(in);
                            }

                        }
                       // mythread.stop();
                        break;
                    }
                    else
                    {
                        Log.e("contect","not connection"+i);
                    }
                    Thread.sleep(200);
                }
            }
            catch(InterruptedException e)
            {
                System.out.println("my thread interrupted");
            }
            System.out.println("mythread run is over" );
        }
    }

}
