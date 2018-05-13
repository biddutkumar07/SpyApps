package com.example.biddut.recoder;


import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.example.biddut.recoder.database.AppData;
import com.example.biddut.recoder.model.DataReceiveEvent;
import com.example.biddut.recoder.model.VoiceToText;
import com.example.biddut.recoder.utils.Validation;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReceiverNumber extends BroadcastReceiver {


	int lastState = TelephonyManager.CALL_STATE_IDLE;

	private String incommingName=null;
	private SharedPreferences myPrefs;
String TAG="ReceiverNumber";

	public  static  String PhoneMunber="";
public  static String Name="Unknown";
	public  static  String Text="";
	public  static  String StartTime="";
	public  static  String EndTime="";
	public  static  String TotalTime="";
	public  static  String CallType="";
	RecordingService mService;
	boolean recording = false;
	boolean isService;
	Intent in;
	//private BroadcastReceiver CallBlocker;
	 Context context;
public static 	 boolean isIncomming=true;
	@Override
	public void onReceive(final Context context, final Intent intent) {
		// TODO Auto-generated method stub
		 isService= AppData.getBool(AppData.service,context);
		Log.e(TAG,""+isService);
		this.context=context;
		Log.e(TAG, "start sevicek"+isService+"  "+intent.getAction());
		//isIncomming=true;
		if(!isService)
			return;

		 String blockingMode="";
		TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		telephony.listen(new PhoneStateListener(){
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				super.onCallStateChanged(state, incomingNumber);
				System.out.println("incomingNumber1 : "+incomingNumber);

//				if(!Validation.isServiceRunning(context,RecordingService.class))
//				{
//					Log.e(TAG, "start sevicek");
//					Intent in = new Intent(context, RecordingService.class);
//					context.startService(in);
//				}
				if(incomingNumber!=null&&incomingNumber.length()>0)
				PhoneMunber=incomingNumber;
				Log.e("incomingNumber1",state+intent.getAction()+ GetCurrentDateTime()+"   "+incomingNumber);

				if(lastState == state)
					return;

				switch (state) {
					case TelephonyManager.CALL_STATE_RINGING:
						isIncomming = true;
						in = new Intent(context, RecordingService.class);
						if(!Validation.isServiceRunning(context,RecordingService.class))
						{
							Log.e(TAG, "start sevicek");
							context.stopService(in);
							context.startService(in);
						}
						else
						{
							Log.e(TAG, "start sevice after stop");
							context.stopService(in);
							context.startService(in);
						}
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK:
						//Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
						if(lastState != TelephonyManager.CALL_STATE_RINGING){
							isIncomming = false;
							in = new Intent(context, RecordingService.class);
							if(!Validation.isServiceRunning(context,RecordingService.class))
							{
								Log.e(TAG, "start sevicek");
								context.stopService(in);
								context.startService(in);
							}
							else
							{
								Log.e(TAG, "start sevice after stop");
								context.stopService(in);
								context.startService(in);
							}
						}
						break;
					case TelephonyManager.CALL_STATE_IDLE:
						//Went to idle-  this is the end of a call.  What type depends on previous state(s)
						if(lastState == TelephonyManager.CALL_STATE_RINGING){
							//Ring but no pickup-  a miss
							context.stopService(in);

							Log.e("onMissedCall","call");
							//onMissedCall(context, savedNumber, callStartTime);
						}
						else if(isIncomming){
							Log.e("onMissedCall","call");
							EndTime=	GetCurrentDateTime();
							Log.e(TAG, GetCurrentDateTime()+"  "+lastState+"   "+"IDLE"+isIncomming);

							CallType="Incomming";

							SetDutyTime();
						}
						else{
							EndTime=	GetCurrentDateTime();
							CallType="Outgoing";
							Log.e(TAG, GetCurrentDateTime()+"  "+lastState+"   "+"IDLE"+isIncomming);

							SetDutyTime();
							}


						break;
				}
				lastState = state;


			}
		},PhoneStateListener.LISTEN_CALL_STATE);

		 {
			 Bundle b = intent.getExtras();

			 String outGoingNumber = b.getString(Intent.EXTRA_PHONE_NUMBER);
			 outGoingNumber=getContactDisplayNameByNumber(outGoingNumber, context);
			 Log.e("outGoingNumber33",""+outGoingNumber);
			 if(outGoingNumber!=null&&outGoingNumber.length()>0)
				 PhoneMunber=outGoingNumber;
			 //blockCall(context, b);

		 }


       }

	public void SetDutyTime()
	{


		Date date1,date2;
		long difference=0,hour=0,min=0,sec=0,h=3600,m=60,s=60;
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
			date1 = format.parse(StartTime);
			date2 = format.parse(EndTime);
			difference = (date2.getTime() - date1.getTime())/(1000);
			Log.e("difference d 11---",""+difference+" - "+StartTime+"  "+EndTime);
			long hours = difference/ 3600;
			min = (difference / 60)%60;
			sec = (difference) % 60;



			Log.e("difference d---",""+difference+" - ");
		}catch (Exception e)
		{
			Log.e("Exception",""+e.getMessage());
		}
		TotalTime=""+hour+":"+min+":"+sec;
		VoiceToText voiceToText=new VoiceToText();
		voiceToText.text="hello";
		voiceToText.datetime=StartTime;
		voiceToText.time=TotalTime;
		voiceToText.name=Name;
		voiceToText.phone=PhoneMunber;
		voiceToText.callType=CallType;
		//  Deauty_time.setText(""+difference);
		Log.e("sumary of call","--s"+StartTime+" \n e"+ EndTime+"  \n"+TotalTime+"--\n"+PhoneMunber+"--\n"+Name);

		EventBus.getDefault().post(new DataReceiveEvent("data_received", voiceToText));

	}
	private String  GetCurrentDateTime()
	{

		DateFormat df1=new SimpleDateFormat("dd MMM yyyy HH:mm:ss");//foramt date
		String date=df1.format(Calendar.getInstance().getTime());
		return date;//mYear+"-"+(mMonth+1)+"-"+mDay;
	}

	public String getContactDisplayNameByNumber(String number, Context c) {
		String name = "?";
		String data=null;
try{
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        ContentResolver contentResolver =c.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                data = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                if(data==null||data.equals(""))
				{
					Name="Unknown";
				}else
				Name=data;
                Log.e("name",""+data+"    "+GetCurrentDateTime()+"   ");
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
		} finally {
            if (contactLookup != null) {
                contactLookup.close();
               
            }
        }}
        catch (Exception e)
		{

		}
        
        return data;
    }



}
