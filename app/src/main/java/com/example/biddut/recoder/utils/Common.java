package com.example.biddut.recoder.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;

/**
 * Created by Dream71 on 19/06/2017.
 */
public class Common {
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
    public  static   void Scrool(final ScrollView scrollview )
    {
        ViewTreeObserver vto = scrollview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                scrollview.scrollTo(0, 0);
            }
        });
    }
    public static  void TokenExpired(Context context)
    {
        Intent intent=new Intent("logout");
        intent.putExtra("message","Token invalid");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isEmailValid(String email) {
        return !(email == null || TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static void hideSoftKeyBoard(View v, final Context context) {
        if (v != null && context != null) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return false;
                }
            });
        }
    }
    public static void hideKeyBoard(View v, Context context) {
        if (v != null && context != null) {

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public static void showSoftKeyBoard(View v, final Context context) {
        if (v != null && context != null) {
            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT);
                    return false;
                }
            });
        }
    }
    public static void showKeyBoard(View v, Context context) {
        if (v != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
