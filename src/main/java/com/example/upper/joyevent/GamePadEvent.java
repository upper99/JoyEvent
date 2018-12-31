package com.example.upper.joyevent;

/**
 * Created by 10126090 on 2018/10/29.
 */
public class GamePadEvent {
    public int sid;
    public int x;
    public int y;
    public int keycode;
    public int action;
    public long downtime;
    public long eventtime;

    GamePadEvent(int keycode){
        this.keycode = keycode;
    }

    GamePadEvent(int keycode,int x,int y,int action){
        this.keycode = keycode;
        this.x = x;
        this.y = y;
        this.action = action;
    }

}
