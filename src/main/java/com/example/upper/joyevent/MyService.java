package com.example.upper.joyevent;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;

public class MyService extends AccessibilityService {
    public static final String TAG = "MyAccessService";
    public static String currentPackageName = null;
    public static ArrayList<String> vSupportGamePackage = new ArrayList<>();//支持的游戏包名列表
    public GameSurfaceView mySurfaceView = null;
    public static MyService mMyAccessibilityService;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "OnServiceConnected");

        mMyAccessibilityService = this;

        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        //config.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16) {
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
            //config.flags |= AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS | AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON;
            config.flags |= AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON;
        }

        //更新支持游戏包名列表
        vSupportGamePackage.clear();
        HashMap<String,String> gamelayoutmap = GameLayout.getGameLayoutsMap();
        for(Map.Entry<String,String>entry:gamelayoutmap.entrySet()){
            String packageName = entry.getValue();
            Log.i(TAG,"Support game:"+packageName);
            vSupportGamePackage.add(packageName);
        }

        config.packageNames = vSupportGamePackage.toArray(new String[0]);

        setServiceInfo(config);
    }

    public static MyService getInstance(){
        return mMyAccessibilityService;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        Log.i(TAG, "onAccessibilityEvent enter..."+packageName);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
        //if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (currentPackageName == null) {
                Log.i(TAG, "current package:" + packageName);
                //currentPackageName = packageName;
                if (!vSupportGamePackage.isEmpty() && vSupportGamePackage.contains(packageName)) {
                    Log.i(TAG, "[1]switch to defined game package...");

                    currentPackageName = packageName;
                    //启动surfaceview
                    Log.i(TAG,"[1]进入悬浮窗!!!!!!!!");
                    params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                    | WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR
                                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
                                    //| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//确保surfaceview不会拦截activity的返回键处理

                    // 设置悬浮窗的长和宽
                    params.width = wm.getDefaultDisplay().getWidth();
                    params.height = wm.getDefaultDisplay().getHeight();
                    mySurfaceView = new GameSurfaceView(getApplicationContext(),packageName);
                    wm.addView(mySurfaceView, params);
                }
            } else if (!currentPackageName.equals(packageName)) {//检测到package变化
                Log.i(TAG, "detect package changed.");

                //WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                //WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                if (!vSupportGamePackage.isEmpty()) {
                    //currentPackageName = packageName;
                    if (vSupportGamePackage.contains(packageName)) {
                        Log.i(TAG, "[2]switch to defined game package...");

                        currentPackageName = packageName;
                        //启动surfaceview
                        Log.i(TAG,"[2]进入悬浮窗!!!!!!!!");
                        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                        | WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR
                                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
                        //| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//确保surfaceview不会拦截activity的返回键处理

                        // 设置悬浮窗的长和宽
                        params.width = wm.getDefaultDisplay().getWidth();
                        params.height = wm.getDefaultDisplay().getHeight();
                        mySurfaceView = new GameSurfaceView(getApplicationContext(),packageName);
                        wm.addView(mySurfaceView, params);
                    }else{//从支持游戏切换到其他界面
                        if(mySurfaceView != null) {
                            Log.i(TAG, "switch to unsupport package:" + packageName);
                            wm.removeView(mySurfaceView);
                        }
                    }
                }else{
                    Log.i(TAG,"vSupportGamePackage is empty");
                }
            } else {//同一package，不同的activity
                Log.i(TAG,"same package, different activity.");
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG,"onInterrupt enter...");
    }

}