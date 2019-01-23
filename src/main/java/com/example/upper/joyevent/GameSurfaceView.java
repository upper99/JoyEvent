package com.example.upper.joyevent;

/**
 * Created by upper on 18-9-1.
 */

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
//import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.input.InputManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.InputDevice;
import android.view.SurfaceView;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Math.abs;

public class GameSurfaceView extends SurfaceView implements Callback,Runnable {
    private final static String TAG = "GameSurfaceView";
    private SurfaceHolder mHolder; // 用于控制SurfaceView
    private Context mContext;
    private String mPackageName;

    private Thread t; // 声明一条线程
    private boolean isRunning; // 线程运行的标识，用于控制线程
    private Canvas mCanvas; // 声明一张画布
    private Paint p; // 声明一支画笔

    //调试用
    private int circleX = 0xffff, circleY = 0xffff, circleR = 10; // 圆的坐标和半径
    private int circleX_JoystickL;
    private int circleY_JoystickL;
    private int circleX_JoystickR;
    private int circleY_JoystickR;

    private boolean bSelfTestMode = false;

    private GameLayout game = null;
    private int originalX_L = 317;
    private int originalY_L = 811;
    private int originalX_R = 1489;
    private int originalY_R = 341;
    private int response_L = 10;
    private int response_R = 10;
    private String type_L;
    private String type_R;
    private int radius_L;//左摇杆半径
    private int radius_R;//右摇杆半径

    public final static int KEYCODE_JOYSTICK_L = 0x10001;
    public final static int KEYCODE_JOYSTICK_R = 0x10002;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            final MotionEvent event = (MotionEvent)msg.obj;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (event != null) mInst.sendPointerSync(event);
                }
            }).start();
        }
    };
    List<GamePadEvent> usedList = new ArrayList<GamePadEvent>();
    Instrumentation mInst = new Instrumentation();
    ReentrantLock lock = new ReentrantLock();
    static int usbDeviceIdOfViewCtrl = -1;//default
    static boolean bMaxDeviceIdAsViewCtrl = true;

    public GameSurfaceView(Context context,String packageName,boolean bTestMode) {
        super(context);
        Log.i(TAG,"GameSurfaceView constructor enter..."+packageName);

        mContext = context;
        mPackageName = packageName;
        bSelfTestMode = bTestMode;

        mHolder = getHolder(); // 获得SurfaceHolder对象
        mHolder.addCallback(this); // 为SurfaceView添加状态监听
    }

    public GameSurfaceView(Context context, String packageName) {
        super(context);
        Log.i(TAG,"GameSurfaceView constructor enter...");

        mContext = context;
        mPackageName = packageName;
        bSelfTestMode = false;

        mHolder = getHolder(); // 获得SurfaceHolder对象
        mHolder.addCallback(this); // 为SurfaceView添加状态监听
    }

    public int getViewCtrlDeviceId(){
        InputManager inputmanager = (InputManager)getContext().getSystemService(Context.INPUT_SERVICE);
        int[] inputDeviceIds = inputmanager.getInputDeviceIds();

        Log.i(TAG,"inputdeviceIds length:"+inputDeviceIds.length);
        int deviceCnt = 0;
        for(int i=0;i<inputDeviceIds.length;i++){
            InputDevice inputDevice = inputmanager.getInputDevice(inputDeviceIds[i]);
            int currDeviceId = inputDevice.getId();
            Log.i(TAG,String.format("detect vid:0x%x,pid:0x%x,device id:%d,record:%d",inputDevice.getVendorId(),inputDevice.getProductId(),inputDevice.getId(),usbDeviceIdOfViewCtrl));
            if(inputDevice.getVendorId() == 0x11b9 && inputDevice.getProductId() == 0x0b00){
                deviceCnt++;
                if(usbDeviceIdOfViewCtrl == -1) {
                    usbDeviceIdOfViewCtrl = currDeviceId;
                }else if(bMaxDeviceIdAsViewCtrl){//第二个设备作为视角设备（设备号大）
                    if(currDeviceId > usbDeviceIdOfViewCtrl){
                        usbDeviceIdOfViewCtrl = currDeviceId;
                    }
                }else if(!bMaxDeviceIdAsViewCtrl){//第一个设备作为视角设备（设备号小）
                    if(currDeviceId < usbDeviceIdOfViewCtrl){
                        usbDeviceIdOfViewCtrl = currDeviceId;
                    }
                }
            }
        }

        if(deviceCnt > 1){
            Log.i(TAG,"detect view ctrl device id:"+usbDeviceIdOfViewCtrl);
            return usbDeviceIdOfViewCtrl;
        }else{
            return -1;
        }
    }

    /**
     * 当SurfaceView创建的时候，调用此函数
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG,"surfaceCreated enter...");

        //根据packageName解析gamelayout布局文件
        String xmlpath = GameLayout.DEFAULT_GAMELAYOUT_PATH+mPackageName +GameLayout.XML_SUFFIX;
        File f = new File(xmlpath);
        if(f.exists()){
            Log.i(TAG,"游戏对应xml布局文件存在");
            game = new GameLayout(xmlpath);
            game.parse();

            Joystick joystick_L = game.getJoystick("JOYSTICK_L");
            Joystick joystick_R = game.getJoystick("JOYSTICK_R");
            originalX_L = joystick_L.getOriginal().getX();
            originalY_L = joystick_L.getOriginal().getY();
            originalX_R = joystick_R.getOriginal().getX();
            originalY_R = joystick_R.getOriginal().getY();
            radius_L = joystick_L.getRadius();
            radius_R = joystick_R.getRadius();
            type_L = joystick_L.getType();
            type_R = joystick_R.getType();
            response_L = joystick_L.getResponse();
            response_R = joystick_R.getResponse();

            System.out.println("type_L:"+type_L+",type_R:"+type_R);
        }else{
            Log.e(TAG,"游戏对应xml布局文件不存在!!!");
        }

        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        if(bSelfTestMode) {
            //setFocusable(true); // 设置焦点
            //setFocusableInTouchMode(true);
            //requestFocus();
            t = new Thread(this); // 创建一个线程对象
            isRunning = true; // 把线程运行的标识设置成true
            t.start(); // 启动线程
        }
    }

    /**
     * 当SurfaceView的视图发生改变的时候，调用此函数
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.i(TAG,"surfaceChanged enter...");
    }

    /**
     * 当SurfaceView销毁的时候，调用此函数
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG,"surfaceDestroyed enter...");
        isRunning = false; // 把线程运行的标识设置成false
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event){
        Log.i(TAG,"SurfaceView dispatchGenericMotionEvent...\n");

        double x,y,z,rz;
        int ix,iy,iz,irz;

        //左右摇杆
        if(KeycodeMap.getMode() == KeycodeMap.MODE_BETOP) {
            x = event.getAxisValue(MotionEvent.AXIS_X);
            y = event.getAxisValue(MotionEvent.AXIS_Y);
            z = event.getAxisValue(MotionEvent.AXIS_Z);
            rz = event.getAxisValue(MotionEvent.AXIS_RZ);
        }else {
            x = event.getAxisValue(MotionEvent.AXIS_Z);
            y = event.getAxisValue(MotionEvent.AXIS_RZ);
            z = event.getAxisValue(MotionEvent.AXIS_X);
            rz = event.getAxisValue(MotionEvent.AXIS_Y);
        }

        Log.i("AXIS",String.format("x:%f,y:%f,z:%f,rz:%f",event.getAxisValue(MotionEvent.AXIS_X),event.getAxisValue(MotionEvent.AXIS_Y),event.getAxisValue(MotionEvent.AXIS_Z),event.getAxisValue(MotionEvent.AXIS_RZ)));

        if(initFlag){
            initMotionEventBuff();
            initFlag = false;
        }

        //输入摇杆坐标处理
        if(Math.abs(x) < Dpad.validJoystickThresholdX && Math.abs(y) < Dpad.validJoystickThresholdY){
            x = 0;y = 0;
        }

        if(Math.abs(z) < Dpad.validJoystickThresholdX && Math.abs(rz) < Dpad.validJoystickThresholdY) {
            z = 0;
            rz = 0;
        }

        ix = (int)(radius_L*x);iy=(int)(radius_L*y);
        iz = (int)(radius_R*z);irz=(int)(radius_R*rz);

        //计算摇杆实时坐标
        circleX_JoystickL = originalX_L + ix;
        circleY_JoystickL = originalY_L + iy;
        circleX_JoystickR = originalX_R + iz;
        circleY_JoystickR = originalY_R + irz;

        /*
        //if(type_L.equals(GameLayout.TYPE_JOYSTICK_STANDARD)) {
            if (isJoystickLStill()) {
                Log.i("Joystick", "detect JoystickL still");
                sync_up(KEYCODE_JOYSTICK_L, circleX_JoystickL, circleY_JoystickL);
            } else {
                sync_down(KEYCODE_JOYSTICK_L, circleX_JoystickL, circleY_JoystickL);
            }
        //}
        */

        //if(type_R.equals(GameLayout.TYPE_JOYSTICK_STANDARD)) {
            if (isJoystickRStill()) {
                Log.i("Joystick", "detect JoystickR still");
                sync_up(KEYCODE_JOYSTICK_R, circleX_JoystickR, circleY_JoystickR);
            } else {
                sync_down(KEYCODE_JOYSTICK_R, circleX_JoystickR, circleY_JoystickR);
            }
        //}

        return false;//(x,y分量控制视角，子view不处理，交给父view处理;而z,rz分量确保父view不处理)
    }

    public static final int MOTITONEVENT_BUFFSIZE = 10;
    private boolean initFlag = true;
    private double[] buffer_x = new double[MOTITONEVENT_BUFFSIZE];
    private double[] buffer_y = new double[MOTITONEVENT_BUFFSIZE];
    private double[] buffer_z = new double[MOTITONEVENT_BUFFSIZE];
    private double[] buffer_rz = new double[MOTITONEVENT_BUFFSIZE];
    private int loc = 0;
    private int loc_prev = 0;
    private long recordKeyDownTime = 0;
    private int recordKeyCode = 0;

    public void initMotionEventBuff(){
        for(int i=0;i<MOTITONEVENT_BUFFSIZE;i++){
            buffer_x[i] = 0;
            buffer_y[i] = 0;
            buffer_z[i] = 0;
            buffer_rz[i] = 0;
        }
    }

    /*
    @Override
    public boolean onGenericMotionEvent(MotionEvent event){
        double x,y,z,rz;
        int ix,iy,iz,irz;

        Log.i(TAG,"surfaceview onGenericMotionEvent...\n");

        Log.i(TAG,"processing...");

        //左右摇杆
        if(KeycodeMap.getMode() == KeycodeMap.MODE_BETOP) {
            x = event.getAxisValue(MotionEvent.AXIS_X);
            y = event.getAxisValue(MotionEvent.AXIS_Y);
            z = event.getAxisValue(MotionEvent.AXIS_Z);
            rz = event.getAxisValue(MotionEvent.AXIS_RZ);
        }else {
            x = event.getAxisValue(MotionEvent.AXIS_Z);
            y = event.getAxisValue(MotionEvent.AXIS_RZ);
            z = event.getAxisValue(MotionEvent.AXIS_X);
            rz = event.getAxisValue(MotionEvent.AXIS_Y);
        }

        Log.i("AXIS",String.format("GenericMotionEvent:\t%f,\t%f,\t%f,\t%f",(double)Math.round(x*100)/100,(double)Math.round(y*100)/100,(double)Math.round(z*100)/100,(double)Math.round(rz*100)/100));

        if(initFlag){
            initMotionEventBuff();
            initFlag = false;
        }

        //输入摇杆坐标处理
        if(Math.abs(x) < Dpad.validJoystickThresholdX && Math.abs(y) < Dpad.validJoystickThresholdY){
            x = 0;y = 0;
        }

        if(Math.abs(z) < Dpad.validJoystickThresholdX && Math.abs(rz) < Dpad.validJoystickThresholdY){
            z = 0;rz = 0;
        }

        buffer_x[loc] = x;
        buffer_y[loc] = y;
        buffer_z[loc] = z;
        buffer_rz[loc] = rz;

        ix = (int)(radius_L*x);iy=(int)(radius_L*y);
        iz = (int)(radius_R*z);irz=(int)(radius_R*rz);

        //计算摇杆实时坐标
        circleX_JoystickL = originalX_L + ix;
        circleY_JoystickL = originalY_L + iy;
        circleX_JoystickR = originalX_R + iz;
        circleY_JoystickR = originalY_R + irz;

        if(type_L.equals(GameLayout.TYPE_JOYSTICK_STANDARD)) {
            if (isJoystickLStill()) {
                Log.i("Joystick", "detect JoystickL still");
                sync_up(KEYCODE_JOYSTICK_L, circleX_JoystickL, circleY_JoystickL);
            } else {
                sync_down(KEYCODE_JOYSTICK_L, circleX_JoystickL, circleY_JoystickL);
            }
        }else{
            if(isJoystickLBack()){
                Log.i("turnback","joystickL turnback");
                sync_up(KEYCODE_JOYSTICK_L, circleX_JoystickL, circleY_JoystickL);
            }else{
                sync_down(KEYCODE_JOYSTICK_L, circleX_JoystickL, circleY_JoystickL);
            }
        }


        if(type_R.equals(GameLayout.TYPE_JOYSTICK_STANDARD)) {
            if (isJoystickRStill()) {
                Log.i("Joystick", "detect JoystickR still");
                sync_up(KEYCODE_JOYSTICK_R, circleX_JoystickR, circleY_JoystickR);
            } else {
                sync_down(KEYCODE_JOYSTICK_R, circleX_JoystickR, circleY_JoystickR);
            }
        }else{
            if(isJoystickRBack()){
                Log.i("turnback","joystickR turnback");
                sync_up(KEYCODE_JOYSTICK_R, circleX_JoystickR, circleY_JoystickR);
            }else{
                sync_down(KEYCODE_JOYSTICK_R, circleX_JoystickR, circleY_JoystickR);
            }
        }

        loc = (loc+1)%MOTITONEVENT_BUFFSIZE;

        return false;
    }
    */

    public boolean isJoystickLBack(){
        double curr_x = buffer_x[loc];
        double curr_y = buffer_y[loc];
        double prev_x = buffer_x[loc_prev];
        double prev_y = buffer_y[loc_prev];

        //测略1：半径减少法
        double curr_powsum = (Math.pow(curr_x,2)+Math.pow(curr_y,2));
        double prev_powsum = (Math.pow(prev_x,2)+Math.pow(prev_y,2));
        double thre_powsum = (Math.pow(Dpad.validJoystickThresholdX,2)+Math.pow(Dpad.validJoystickThresholdY,2));
        double curr_ratio = curr_y/curr_x+0.000000001;
        double prev_ratio = prev_y/prev_x+0.000000001;
        //Log.i("turnback",String.format("prev_ratio:%s,curr_ratio:%s",prev_ratio,curr_ratio));
        if(curr_powsum > prev_powsum && curr_powsum > thre_powsum){
            return false;
        }else{
            return true;
        }

        //测略2：检测向心方向
    }

    public boolean isJoystickRBack(){
        double curr_z = buffer_z[loc];
        double curr_rz = buffer_rz[loc];
        double prev_z = buffer_z[loc_prev];
        double prev_rz = buffer_rz[loc_prev];

        double curr_powsum = (Math.pow(curr_z,2)+Math.pow(curr_rz,2));
        double prev_powsum = (Math.pow(prev_z,2)+Math.pow(prev_rz,2));
        double thre_powsum = (Math.pow(Dpad.validJoystickThresholdX,2)+Math.pow(Dpad.validJoystickThresholdY,2));
        if(curr_powsum > prev_powsum && curr_powsum > thre_powsum){
            return false;
        }else{
            return true;
        }
    }

    public boolean isJoystickLStill(){
        if(buffer_x[loc] != 0 || buffer_y[loc] != 0){
            return false;
        }else {
            return true;
        }
    }

    public boolean isJoystickRStill(){
        if(buffer_z[loc] != 0 || buffer_rz[loc] != 0){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        Log.i(TAG,"dispatchKeyEvent enter...");
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            this.setVisibility(View.GONE);
            return false;
        }

        Log.i(TAG,"dispatchKeyEvent done.");
        return super.dispatchKeyEvent(event);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event){
        doActionForKeyUp(keyCode, event.getAction());

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long currTime = System.currentTimeMillis();
        if(currTime - recordKeyDownTime > 50){//50ms内判断是否同时按下多个键
            recordKeyDownTime = currTime;
            recordKeyCode = keyCode;
        }else{
            if(recordKeyCode != keyCode) {
                System.out.println("检测连续按不同键："+recordKeyCode+","+keyCode);
                //按组合键处理

                //doActionForKeyUp(recordKeyCode,keyCode,event.getAction());
                //return true;
            }else{
                System.out.println("检测连续按相同键");
                //不处理
            }
        }

        doActionForKeyUp(keyCode, event.getAction());

        return true;
    }

    //处理组合键
    public boolean doActionForKeyUp(int keyCode1,int keyCode2,int action){
        if(game != null){
            String comboLabel = String.format("%s+%s",KeycodeMap.getKey(keyCode1),KeycodeMap.getKey(keyCode2));
            ComboBtn comboBtn = game.getCombosMap().get(comboLabel);
            if(comboBtn != null){
                if (comboBtn.getAction().equals(GameLayout.ACTION_CLICK)) {
                    if(action == MotionEvent.ACTION_DOWN) {
                        if (comboBtn.getType().equals(GameLayout.TYPE_MULTI)) {//该按钮为multi多点复用，需要处理index
                            Log.i(TAG, "当前index：" + comboBtn.getPointIndex());
                            comboBtn.increasePointIndex();
                            Log.i(TAG, "下个index：" + comboBtn.getPointIndex());
                        }
                    }

                    Point point = comboBtn.getPoint();
                    if(point == null) return false;
                    Log.i(TAG,"x:"+point.getX()+",:"+point.getY());
                    if(action == MotionEvent.ACTION_UP){
                        circleX = 0xffff;
                        circleY = 0xffff;
                        sync_up(keyCode1+keyCode2,point.getX(), point.getY());
                    }else if(action == MotionEvent.ACTION_DOWN){
                        circleX = point.getX();
                        circleY = point.getY();
                        sync_down(keyCode1+keyCode2,point.getX(), point.getY());
                    }
                } else if (comboBtn.getAction().equals(GameLayout.ACTION_SWIPE)) {

                }

                return true;
            }else{
                Log.e(TAG,"Btn NOT defined");
                return false;
            }
        }else{
            Log.i(TAG,"[KeyUp]game is null");

            return false;
        }
    }

    public boolean doActionForKeyUp(int keyCode,int action){
        String keycodelabel = KeycodeMap.getKey(keyCode);
        if(game != null){
            Btn btn = game.getBtnsMap().get(keycodelabel);
            if(btn != null){
                if (btn.getAction().equals(GameLayout.ACTION_CLICK)) {
                    if(action == MotionEvent.ACTION_DOWN) {
                        if (btn.getType().equals(GameLayout.TYPE_MULTI)) {//该按钮为multi多点复用，需要处理index
                            Log.i(TAG, "当前index：" + btn.getPointIndex());
                            btn.increasePointIndex();
                            Log.i(TAG, "下个index：" + btn.getPointIndex());
                        }
                    }

                    Point point = btn.getPoint();
                    if(point == null) return false;
                    Log.i(TAG,"x:"+point.getX()+",:"+point.getY());
                    if(action == MotionEvent.ACTION_UP){
                        circleX = 0xffff;
                        circleY = 0xffff;
                        sync_up(keyCode,point.getX(), point.getY());
                    }else if(action == MotionEvent.ACTION_DOWN){
                        circleX = point.getX();
                        circleY = point.getY();
                        sync_down(keyCode,point.getX(), point.getY());
                    }
                } else if (btn.getAction().equals(GameLayout.ACTION_SWIPE)) {

                }

                return true;
            }else{
                Log.e(TAG,"Btn NOT defined");
                return false;
            }
        }else{
            Log.i(TAG,"[KeyUp]game is null");

            return false;
        }
    }

    @Override
    public void run() {
        p = new Paint(); // 创建一个画笔对象
        while (isRunning == true) {
            if(MainActivity.bWhetherShowMotionTrack) Draw(); // 调用自定义画画方法
            try {
                Thread.sleep(50); // 让线程休息50毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void Draw() {
        mCanvas = mHolder.lockCanvas(); // 获得画布对象，开始对画布画画
        if(mCanvas != null) {
            if(usedList.size() != 0) {
                Log.d("Draw","usedList size="+usedList.size());
                Log.d("Draw","keycode="+usedList.get(0).keycode);
                if (circleX != 0xffff && circleY != 0xffff) {
                    p.setColor(Color.BLUE); // 设置画笔的颜色为白色
                    p.setTextSize(50);
                    mCanvas.drawCircle(circleX, circleY, circleR, p); // 画一个圆
                }

                if(circleX_JoystickL != originalX_L || circleY_JoystickL != originalY_L) {
                    p.setColor(Color.GREEN);
                    mCanvas.drawCircle(circleX_JoystickL, circleY_JoystickL, 20, p);
                }

                if(circleX_JoystickR != originalX_R || circleY_JoystickR != originalY_R) {
                    p.setColor(Color.GRAY);
                    mCanvas.drawCircle(circleX_JoystickR, circleY_JoystickR, 20, p);
                }
            }else{
                Log.d("Draw","usedList size="+usedList.size());
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                mCanvas.drawPaint(p);
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            }

            mHolder.unlockCanvasAndPost(mCanvas); // 完成画画，把画布显示在屏幕上
        }
    }

    public void sync_down(int keycode, int x, int y) {
        MotionEvent event = null;
        lock();
        try {
            GamePadEvent target = new GamePadEvent(keycode, x, y, MotionEvent.ACTION_DOWN);
            if (usedList.size() > 0) {
                int i;
                boolean found = false;
                for (i = 0; i < usedList.size(); i++) {//写了10就是支持10个点
                    GamePadEvent gpe = usedList.get(i);
                    if (gpe.keycode == keycode) {
                        //Log.i("updown","move:"+keycode+",x:"+x+",y:"+y);
                        target.sid = i;
                        if(keycode == KEYCODE_JOYSTICK_L){
                            target.downtime = gpe.downtime + response_L;
                            //target.downtime = gpe.downtime;
                            target.eventtime = SystemClock.uptimeMillis() + response_L;
                        }else if(keycode == KEYCODE_JOYSTICK_R){
                            target.downtime = gpe.downtime + response_R;
                            //target.downtime = gpe.downtime;
                            target.eventtime = SystemClock.uptimeMillis() + response_R;
                        }else {
                            target.downtime = gpe.downtime;
                            target.eventtime = SystemClock.uptimeMillis();
                        }
                        target.action = MotionEvent.ACTION_MOVE;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Log.i("updown","[new]down:"+keycode+",x:"+x+",y:"+y);
                    target.sid = usedList.size() + 1;
                    target.downtime = SystemClock.uptimeMillis();
                    target.eventtime = target.downtime;
                    //约定摇杆down起点为原点
                    if(keycode == KEYCODE_JOYSTICK_L){
                        target.x = originalX_L;
                        target.y = originalY_L;
                    }else if(keycode == KEYCODE_JOYSTICK_R){
                        target.x = originalX_R;
                        target.y = originalY_R;
                    }
                    usedList.add(target);
                }

            } else {
                Log.i("updown","[first]down:"+keycode+",x:"+x+",y:"+y);
                target.sid = 0;
                target.downtime = SystemClock.uptimeMillis();
                target.eventtime = target.downtime;
                //约定摇杆down起点为原点
                if(keycode == KEYCODE_JOYSTICK_L){
                    target.x = originalX_L;
                    target.y = originalY_L;
                }else if(keycode == KEYCODE_JOYSTICK_R){
                    target.x = originalX_R;
                    target.y = originalY_R;
                }
                usedList.add(0, target);
            }

            event = getMultiEvent(target);
            Message msg = Message.obtain();
            msg.obj = event;
            handler.sendMessage(msg);
        }finally{
            Log.i("sync","down:"+usedList.size());
            unlock();
        }

    }

    public void sync_up(int keycode, int x, int y) {
        MotionEvent event = null;
        lock();
        try {
            GamePadEvent target = new GamePadEvent(keycode, x, y, MotionEvent.ACTION_UP);
            //把新的target 添加到usedlist里面去，并且设置sid
            for (int i = 0; i < usedList.size(); i++) {
                GamePadEvent gpe = usedList.get(i);
                if (target.keycode == gpe.keycode) {
                    Log.i("updown","up:"+keycode+",x:"+x+",y:"+y);
                    target.sid = gpe.sid;
                    target.downtime = gpe.downtime;
                    target.eventtime = SystemClock.uptimeMillis();
                    target.action = MotionEvent.ACTION_UP;
                    event = getMultiEvent(target);
                    Log.i("sync","before:"+usedList.size());
                    usedList.remove(i);
                    Log.i("sync","after:"+usedList.size());

                    Message msg = Message.obtain();
                    msg.obj = event;
                    handler.sendMessage(msg);

                    break;
                }
            }
        }finally {
            unlock();
        }

    }

    public void lock(){
        lock.lock();
    }

    public void unlock(){
        lock.unlock();
    }

    private MotionEvent getMultiEvent(GamePadEvent target) {
        int action;
        int listsize;

        listsize = usedList.size();
        if (listsize == 0) {
            System.out.println("NO event found!");
            return null;
        }

        //记录当前按下的点的action是move，up，down，判断是1个点还是多个点
        if (target.action == MotionEvent.ACTION_DOWN) {
            action = listsize > 1 ? MotionEvent.ACTION_POINTER_DOWN : MotionEvent.ACTION_DOWN;
        }else if (target.action == MotionEvent.ACTION_UP) {
            action = listsize > 1 ? MotionEvent.ACTION_POINTER_UP : MotionEvent.ACTION_UP;
        }else{
            action = MotionEvent.ACTION_MOVE;
        }

        MotionEvent.PointerProperties[] pointerProperties = new MotionEvent.PointerProperties[listsize];
        MotionEvent.PointerCoords[] pointerCoords = new MotionEvent.PointerCoords[listsize];
        int index = 0;
        int targetIndex = 0;
        for (GamePadEvent gamePadEvent : usedList) {
            if (gamePadEvent.keycode == target.keycode) {//存在表明是ACTION_MOVE
                targetIndex = index;//记录当前的按下的点的位置，用作计算最后的action

                MotionEvent.PointerCoords pointerCoord = new MotionEvent.PointerCoords();
                pointerCoord.pressure = 1;
                pointerCoord.x = target.x;
                pointerCoord.y = target.y;
                pointerCoords[index] = pointerCoord;

                MotionEvent.PointerProperties pointerPropertie = new MotionEvent.PointerProperties();
                pointerPropertie.id = index;//保存的id
                pointerPropertie.toolType = MotionEvent.TOOL_TYPE_FINGER;
                pointerProperties[index] = pointerPropertie;
            }else{
                MotionEvent.PointerCoords pointerCoord = new MotionEvent.PointerCoords();
                pointerCoord.pressure = 1;
                if(gamePadEvent.keycode == KEYCODE_JOYSTICK_L) {//摇杆坐标是实时更新的(而不是固定down时的坐标)
                    pointerCoord.x = circleX_JoystickL;
                    pointerCoord.y = circleY_JoystickL;
                }else if(gamePadEvent.keycode == KEYCODE_JOYSTICK_R){//摇杆坐标是实时更新的(而不是固定down时的坐标)
                    pointerCoord.x = circleX_JoystickR;
                    pointerCoord.y = circleY_JoystickR;
                }else{
                    pointerCoord.x = gamePadEvent.x;
                    pointerCoord.y = gamePadEvent.y;
                }
                pointerCoords[index] = pointerCoord;

                MotionEvent.PointerProperties pointerPropertie = new MotionEvent.PointerProperties();
                pointerPropertie.id = index;//保存的id
                pointerPropertie.toolType = MotionEvent.TOOL_TYPE_FINGER;
                pointerProperties[index] = pointerPropertie;
            }

            ++index;
        }

        int actionPoint = (action != MotionEvent.ACTION_MOVE) ? (action + (targetIndex << MotionEvent.ACTION_POINTER_INDEX_SHIFT)) : MotionEvent.ACTION_MOVE;
        long downTime = target.downtime;
        long eventTime = target.eventtime;

        //Log.i("multievent",String.format("action=0x%x",actionPoint));
        return MotionEvent.obtain( downTime,
                eventTime,
                actionPoint,
                usedList.size(),
                pointerProperties,
                pointerCoords,
                0, //metaState
                0, //buttonState
                1, //xPrecision
                1, //yPrecision
                0, //deviceId
                0, //edgeFlags
                InputDevice.SOURCE_TOUCHSCREEN,
                0);
    }
}
