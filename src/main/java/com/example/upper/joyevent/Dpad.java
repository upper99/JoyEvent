package com.example.upper.joyevent;

import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import static java.lang.Math.abs;

/**
 * Created by upper on 18-8-11.
 */

public class Dpad {
    final static int UP = 0;
    final static int LEFT = 1;
    final static int RIGHT = 2;
    final static int DOWN = 3;
    final static int LEFT_UP = 4;
    final static int LEFT_DOWN = 5;
    final static int RIGHT_UP = 6;
    final static int RIGHT_DOWN = 7;
    final static int UNKNOWN = 0xff;
    final static int JOYSTICK_L = 0x010;
    final static int JOYSTICK_R = 0x020;

    int directionPressed = -1;
    public static double validJoystickThresholdX = 0.15;
    public static double validJoystickThresholdY = 0.15;

    public void reset(){
        directionPressed = -1;
    }

    public static void updateThreshold(int thre){
        validJoystickThresholdX = (double)thre/100;
        validJoystickThresholdY = (double)thre/100;
    }

    public static void updateThreshold(int x,int y){
        validJoystickThresholdX = (double)x/100;
        validJoystickThresholdY = (double)y/100;
    }

    public int getAction(MotionEvent motionEvent){
        float axis_x = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
        float axis_y = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);

        if(Float.compare(axis_x,0.0f) == 0 && Float.compare(axis_y,0.0f) == 0){
            return MotionEvent.ACTION_UP;
        }else{
            return MotionEvent.ACTION_DOWN;
        }
    }

    //十字方向键
    public int getDirectionPressed(InputEvent event){
        if(!isDpadDevice(event)){
            return -1;
        }

        if(event instanceof MotionEvent){
            System.out.println("MotionEvent");
            MotionEvent motionEvent = (MotionEvent)event;
            float axis_x = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
            float axis_y = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);

            if(Float.compare(axis_x,-1.0f) == 0 && Float.compare(axis_y,-1.0f) == 0){
                directionPressed = Dpad.LEFT_UP;
            }else if(Float.compare(axis_x,-1.0f) == 0 && Float.compare(axis_y,1.0f) == 0){
                directionPressed = Dpad.LEFT_DOWN;
            }else if(Float.compare(axis_x,1.0f) == 0 && Float.compare(axis_y,-1.0f) == 0){
                directionPressed = Dpad.RIGHT_UP;
            }else if(Float.compare(axis_x,1.0f) == 0 && Float.compare(axis_y,1.0f) == 0){
                directionPressed = Dpad.RIGHT_DOWN;
            }else if(Float.compare(axis_x,-1.0f) == 0){
                directionPressed = Dpad.LEFT;
            }else if(Float.compare(axis_x,1.0f) == 0){
                directionPressed = Dpad.RIGHT;
            }else if(Float.compare(axis_y,-1.0f) == 0){
                directionPressed = Dpad.UP;
            }else if(Float.compare(axis_y,1.0f) == 0){
                directionPressed = Dpad.DOWN;
            }
        }
        else if(event instanceof KeyEvent){
            System.out.println("KeyEvent");
            KeyEvent keyEvent = (KeyEvent)event;
            if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT){
                directionPressed = Dpad.LEFT;
            }else if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT){
                directionPressed = Dpad.RIGHT;
            }else if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP){
                directionPressed = Dpad.UP;
            }else if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN){
                directionPressed = Dpad.DOWN;
            }
        }

        return directionPressed;
    }

    public static boolean isDpadDevice(InputEvent event){
        if((event.getSource() & InputDevice.SOURCE_DPAD) != InputDevice.SOURCE_DPAD){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isJoystickL(MotionEvent event){
        double x,y;
        if(KeycodeMap.getMode() == KeycodeMap.MODE_BETOP) {
            x = event.getAxisValue(MotionEvent.AXIS_X);
            y = event.getAxisValue(MotionEvent.AXIS_Y);
        }else {
            x = event.getAxisValue(MotionEvent.AXIS_Z);
            y = event.getAxisValue(MotionEvent.AXIS_RZ);
        }
        if(validJoystickThresholdX > abs(x) && validJoystickThresholdY > abs(y)){
            return false;
        }else{
            return true;
        }
    }

    public static boolean isJoystickR(MotionEvent event){
        double z,rz;
        if(KeycodeMap.getMode() == KeycodeMap.MODE_BETOP) {
            z = event.getAxisValue(MotionEvent.AXIS_Z);
            rz = event.getAxisValue(MotionEvent.AXIS_RZ);
        }else{
            z = event.getAxisValue(MotionEvent.AXIS_X);
            rz = event.getAxisValue(MotionEvent.AXIS_Y);
        }

        if(validJoystickThresholdX > abs(z) && validJoystickThresholdY > abs(rz)){
            return false;
        }else{
            return true;
        }
    }

    public int getJoystickType(MotionEvent event){
        if(isJoystickL(event) && !isJoystickR(event)){
            return JOYSTICK_L;
        }else if(isJoystickR(event) && !isJoystickL(event)){
            return JOYSTICK_R;
        }else if(isJoystickR(event) && isJoystickL(event)){
            return JOYSTICK_R+JOYSTICK_L;
        }else{//静置状态
            return UNKNOWN;
        }
    }

    //游戏摇杆方向判断
    public int getJoystickDirection(MotionEvent event){
        double x=0,y=0;
        int swipeType = Dpad.UNKNOWN;
        if(isJoystickL(event)){
            if(KeycodeMap.getMode() == KeycodeMap.MODE_BETOP) {
                x = event.getAxisValue(MotionEvent.AXIS_X);
                y = event.getAxisValue(MotionEvent.AXIS_Y);
            }else {
                x = event.getAxisValue(MotionEvent.AXIS_Z);
                y = event.getAxisValue(MotionEvent.AXIS_RZ);
            }
        }else if(isJoystickR(event)){
            if(KeycodeMap.getMode() == KeycodeMap.MODE_BETOP) {
                x = event.getAxisValue(MotionEvent.AXIS_Z);
                y = event.getAxisValue(MotionEvent.AXIS_RZ);
            }else {
                x = event.getAxisValue(MotionEvent.AXIS_X);
                y = event.getAxisValue(MotionEvent.AXIS_Y);
            }
        }else{
            return Dpad.UNKNOWN;
        }

        if(1 == x)
        {
            swipeType = Dpad.RIGHT;
        }else if(-1 == x){
            swipeType = Dpad.LEFT;
        }else if(1 == y)
        {
            swipeType = Dpad.DOWN;
        }else if(-1 == y){
            swipeType = Dpad.UP;
        }else if(validJoystickThresholdX < abs(x) && validJoystickThresholdY < abs(y)){
            if(x>0 && y>0){
                swipeType = Dpad.RIGHT_DOWN;
            }else if(x>0 && y<0){
                swipeType = Dpad.RIGHT_UP;
            }else if(x<0 && y>0){
                swipeType = Dpad.LEFT_DOWN;
            }else if(x<0 && y<0){
                swipeType = Dpad.LEFT_UP;
            }
        }

        return swipeType;
    }
}
