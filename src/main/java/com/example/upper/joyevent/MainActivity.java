package com.example.upper.joyevent;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = "MainActivity";
    private static final Intent sSettingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private Button btnEnableService;
    private Button btnPreview;
    private Button btnCreate;
    private Button btnModify;
    private Button btnGameTest;
    private RadioGroup radioGroup;
    private TextView tv_keycode;
    private TextView tv_showresult;
    private RadioGroup rg_joystick;
    private EditText et_precision_joystick;
    private CheckBox cb_precision_joystick;
    private Button btn_confirm_precision;
    private CheckBox cb_showkeycode;
    private TextView tv_showkeycode;
    private CheckBox cb_showmotiontrack;
    private TextView tv_showmotiontrack;
    private Spinner gameslist;
    private List<String> data_list;
    private HashMap<String,String> mapGameLayoutNamePackage;
    private ArrayAdapter<String> arr_adapter;
    private boolean bWhetherShowKeycode = false;
    public static boolean bWhetherShowMotionTrack = false;
    private static boolean isExist = false;
    private boolean bTestMode = false;
    private String gamePackageName = "default";
    private GameSurfaceView mySurfaceView;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    public static final int REQUESTCODE = 0x001;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExist = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        btnEnableService = (Button) findViewById(R.id.btn_enableService);
        btnEnableService.setOnClickListener(this);

        btnCreate = (Button)findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(this);

        btnGameTest = (Button) findViewById(R.id.btn_gametest);
        btnGameTest.setOnClickListener(this);

        btnPreview = (Button) findViewById(R.id.btn_preview);
        btnPreview.setOnClickListener(this);

        btnModify = (Button)findViewById(R.id.btn_modify);
        btnModify.setOnClickListener(this);

        et_precision_joystick = (EditText)findViewById(R.id.et_precision_joystick);
        btn_confirm_precision = (Button)findViewById(R.id.btn_confirm_precision);
        btn_confirm_precision.setOnClickListener(this);

        cb_precision_joystick = (CheckBox)findViewById(R.id.cb_precision_joystick);
        cb_precision_joystick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                et_precision_joystick.setVisibility(View.VISIBLE);
                btn_confirm_precision.setVisibility(View.VISIBLE);
            }else{
                et_precision_joystick.setVisibility(View.INVISIBLE);
                btn_confirm_precision.setVisibility(View.INVISIBLE);
            }
            }
        });

        cb_showkeycode = (CheckBox)findViewById(R.id.cb_showkeycode);
        tv_showkeycode = (TextView)findViewById(R.id.tv_showkeycode);

        cb_showkeycode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bWhetherShowKeycode = true;
                }else{
                    bWhetherShowKeycode = false;
                }
            }
        });

        cb_showmotiontrack = (CheckBox)findViewById(R.id.cb_showmotion);
        tv_showmotiontrack = (TextView)findViewById(R.id.tv_showmotion);

        cb_showmotiontrack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bWhetherShowMotionTrack = true;
                    dispToast("显示手柄轨迹");
                }else{
                    bWhetherShowMotionTrack = false;
                    dispToast("不显示手柄轨迹");
                }
            }
        });

        gameslist = (Spinner) findViewById(R.id.spinner);

        //从/sdcard/gamelayout/中解析出“游戏名称-包名”映射关系

        KeycodeMap.updateKeycodeMap(KeycodeMap.MODE_LANMAO);//默认选择北通

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_ATTACHED_IN_DECOR

                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;//解决软键盘不显示问题
                //| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//确保surfaceview不会拦截activity的返回键处理

        // 设置悬浮窗的长和宽
        params.width = wm.getDefaultDisplay().getWidth();
        params.height = wm.getDefaultDisplay().getHeight();

        rg_joystick = (RadioGroup)findViewById(R.id.rg_joystick);
        rg_joystick.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                switch(checkedId){
                    case R.id.rb_betop:
                        System.out.println("选择betop");
                        KeycodeMap.updateKeycodeMap(KeycodeMap.MODE_BETOP);
                        break;
                    case R.id.rb_lanmao:
                        System.out.println("选择lanmao");
                        KeycodeMap.updateKeycodeMap(KeycodeMap.MODE_LANMAO);
                        break;
                }

                RadioButton rb = (RadioButton)findViewById(checkedId);
                dispToast("current joystick mode:"+rb.getText());
            }
        });

        SdcardPermission.verifyStoragePermissions(this);
        updateDisplayGameLayouts();
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(serviceIsRunning()){
            Log.d(TAG, "service is running");
            btnEnableService.setText("关闭辅助服务");
        }else{
            Log.d(TAG, "service is Not running");
            btnEnableService.setText("打开辅助服务");
        }
    }

    private boolean serviceIsRunning() {
        ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            Log.d(TAG, info.service.getClassName());
            if (info.service.getClassName().equals(getPackageName() + ".MyService")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        Log.i(TAG,"MainActivity:dispatchKeyEvent enter...");

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event){
        System.out.println("onKeyDown:"+keycode);
        if(bWhetherShowKeycode){
            tv_showkeycode.setText(""+keycode);
            return true;
        }

        if(keycode == event.KEYCODE_BACK) {
            exit();
            return false;
        }

        return false;
    }

    private void exit(){
        if(!isExist){
            isExist = true;
            Toast.makeText(getApplicationContext(),"再按一次退出", Toast.LENGTH_SHORT).show();

            mHandler.sendEmptyMessageDelayed(0,1000);
        }else{
            finish();
            System.exit(0);
        }
    }

    public void updateDisplayGameLayouts(){
        //数据
        mapGameLayoutNamePackage = getGameLayoutsMap();
        data_list = new ArrayList<String>();

        if(mapGameLayoutNamePackage == null){
            dispToast("未检测到游戏布局文件");
            return;
        }

        Iterator<Map.Entry<String,String>> it = mapGameLayoutNamePackage.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,String> me = it.next();
            String gamename = me.getKey();
            String packagename = me.getValue();
            System.out.println(""+gamename+"->"+packagename);
            data_list.add(gamename);
        }

        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        gameslist.setAdapter(arr_adapter);
        gameslist.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0,View arg1,int pos, long id) {
                // TODO Auto-generated method stub
                gamePackageName = mapGameLayoutNamePackage.get(data_list.get(pos));
                Log.i(TAG,"package changed:"+gamePackageName);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    public HashMap<String,String> getGameLayoutsMap(){
        File folder = new File(GameLayout.DEFAULT_GAMELAYOUT_PATH);
        if(!folder.exists()){
            folder.mkdir();
            folder.canRead();
            folder.canWrite();
            return null;
        }
        folder.canRead();
        folder.canWrite();
        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if(filename.endsWith(GameLayout.XML_SUFFIX)){
                    return true;
                }else {
                    return false;
                }
            }
        });

        HashMap<String,String> map = new HashMap<String,String>();
        for(File f:files){
            String filename = f.getName();
            System.out.println(filename);
            String packageName = GameLayoutUtils.xmlFileName2PackageName(filename);
            System.out.println(packageName);
            GameLayout gameLayout = new GameLayout(packageName,true);
            gameLayout.parse();
            String gameName_Ch = gameLayout.getGameNameCh();
            System.out.println(gameName_Ch);
            map.put(gameName_Ch,packageName);
        }

        return map;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_create:
                Intent it = new Intent(this,GameLayoutCreateActivity.class);
                startActivityForResult(it,REQUESTCODE);
                break;
            case R.id.btn_modify:
                Intent it1 = new Intent(this,GameLayoutModifyActivity.class);
                it1.putExtra("packageName",gamePackageName);
                startActivityForResult(it1,REQUESTCODE);
                break;
            case R.id.btn_preview:
                Intent it2 = new Intent(this,GameLayoutPreviewActivity.class);
                it2.putExtra("packageName",gamePackageName);
                startActivity(it2);
                break;
            case R.id.btn_gametest:
                if(bTestMode == false) {
                    btnGameTest.setText("停止自测");
                    bTestMode = true;
                    dispToast(gamePackageName);
                    mySurfaceView = new GameSurfaceView(getApplicationContext(),gamePackageName,true);
                    wm.addView(mySurfaceView, params);
                }else{
                    btnGameTest.setText("开始自测");
                    bTestMode = false;
                    wm.removeView(mySurfaceView);
                }
                break;
            case R.id.btn_enableService:
                startActivity(sSettingsIntent);
                break;
            case R.id.btn_confirm_precision:
                Dpad.updateThreshold(Integer.parseInt(et_precision_joystick.getText().toString()));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == REQUESTCODE){
            if(resultCode == 1) {
                updateDisplayGameLayouts();
            }else{
                System.out.println("do nothing");
            }
        }
    }

    public void dispToast(String str){
        Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = res.getConfiguration();
        config.fontScale = 1.5f;
        //1 设置正常字体大小的倍数
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

}