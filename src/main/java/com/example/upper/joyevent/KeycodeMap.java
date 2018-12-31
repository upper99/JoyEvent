package com.example.upper.joyevent;

import java.util.HashMap;

/**
 * Created by upper on 18-8-29.
 * 不同的手柄,对应不同的kl和kcm文件;同时,不同的android系统,input.h和keycodes.h也是不同的
 */

public class KeycodeMap{
    public static HashMap<String,Integer> map = new HashMap<String,Integer>();
    public static final int MODE_BETOP = 1;
    public static final int MODE_LANMAO = 2;
    public static int mode = MODE_LANMAO;

    public static void setMode(int mode){
        KeycodeMap.mode = mode;
    }

    public static int getMode(){
        return mode;
    }

    public static void updateKeycodeMap(int mode){
        setMode(mode);
        switch(mode){
            case MODE_BETOP://betop
                //20bc 5500(betop)
                map.clear();
                map.put("BUTTON_A",98);
                map.put("BUTTON_B",97);
                map.put("BUTTON_X",99);
                map.put("BUTTON_Y",96);
                map.put("BUTTON_RB",101);
                map.put("BUTTON_RT",103);
                map.put("BUTTON_LB",100);
                map.put("BUTTON_LT",102);
                map.put("BUTTON_LEFT",21);
                map.put("BUTTON_RIGHT",22);
                map.put("BUTTON_UP",19);
                map.put("BUTTON_DOWN",20);
                map.put("BUTTON_LPRESS",109);//左摇杆压力按键
                map.put("BUTTON_RPRESS",108);//右摇杆压力按键
                break;
            case MODE_LANMAO://lanmao
                //11b9 0b00(lanmao)
                map.clear();
                map.put("BUTTON_B",194);
                map.put("BUTTON_X",192);
                map.put("BUTTON_Y",196);
                map.put("BUTTON_RT",190);
                map.put("BUTTON_LT",191);
                map.put("BUTTON_LEFT",195);
                map.put("BUTTON_RIGHT",193);
                map.put("BUTTON_UP",191);
                map.put("BUTTON_LPRESS",189);//左摇杆压力按键
                map.put("BUTTON_RPRESS",188);//右摇杆压力按键
                break;
        }
    }

    static{
        /* //20bc 1263
        map.put("BUTTON_A",98);
        map.put("BUTTON_B",97);
        map.put("BUTTON_X",99);
        map.put("BUTTON_Y",96);
        map.put("BUTTON_RB",101);
        map.put("BUTTON_RT",103);
        map.put("BUTTON_LB",100);
        map.put("BUTTON_LT",102);
        map.put("BUTTON_LEFT",21);
        map.put("BUTTON_RIGHT",22);
        map.put("BUTTON_UP",19);
        map.put("BUTTON_DOWN",20);
        map.put("BUTTON_LPRESS",109);//左摇杆压力按键
        map.put("BUTTON_RPRESS",108);//右摇杆压力按键
        */

        //20bc 5500(betop)
        /*
        map.put("BUTTON_A",96);
        map.put("BUTTON_B",97);
        map.put("BUTTON_X",99);
        map.put("BUTTON_Y",100);
        map.put("BUTTON_RB",103);
        map.put("BUTTON_RT",105);
        map.put("BUTTON_LB",102);
        map.put("BUTTON_LT",104);
        map.put("BUTTON_LEFT",21);
        map.put("BUTTON_RIGHT",22);
        map.put("BUTTON_UP",19);
        map.put("BUTTON_DOWN",20);
        map.put("BUTTON_LPRESS",106);//左摇杆压力按键
        map.put("BUTTON_RPRESS",107);//右摇杆压力按键
        */

        //11b9 0b00(lanmao)
        map.put("BUTTON_B",195);
        map.put("BUTTON_X",193);
        map.put("BUTTON_Y",197);
        map.put("BUTTON_RT",191);
        map.put("BUTTON_LT",190);
        map.put("BUTTON_LEFT",194);
        map.put("BUTTON_RIGHT",192);
        map.put("BUTTON_UP",196);
        map.put("BUTTON_LPRESS",188);//左摇杆压力按键
        map.put("BUTTON_RPRESS",189);//右摇杆压力按键

    }

    public static String getKey(Integer value){
        String key = null;
        for(String tmpKey: map.keySet()){
            if(map.get(tmpKey).equals(value)){
                key = tmpKey;
            }
        }

        return key;
    }
}
