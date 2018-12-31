package com.example.upper.joyevent;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameLayoutPreviewActivity extends AppCompatActivity {
    EditText et_appname;
    EditText et_packagename;

    //按键&组合键属性：名称
    EditText et_name_btn_b;
    EditText et_name_btn_x;
    EditText et_name_btn_y;
    EditText et_name_btn_up;
    EditText et_name_btn_left;
    EditText et_name_btn_right;
    EditText et_name_btn_lt;
    EditText et_name_btn_rt;
    EditText et_name_btn_lpress;
    EditText et_name_btn_rpress;
    EditText et_name_combo1;
    EditText et_name_combo2;
    EditText et_name_combo3;

    //按键&组合键属性：坐标
    EditText et_coordinate_btn_b;
    EditText et_coordinate_btn_x;
    EditText et_coordinate_btn_y;
    EditText et_coordinate_btn_up;
    EditText et_coordinate_btn_lt;
    EditText et_coordinate_btn_rt;
    EditText et_coordinate_btn_left;
    EditText et_coordinate_btn_right;
    EditText et_coordinate_btn_lpress;
    EditText et_coordinate_btn_rpress;
    EditText et_coordinate_combo1;
    EditText et_coordinate_combo2;
    EditText et_coordinate_combo3;

    //按键&组合键属性：功能
    EditText et_description_btn_b;
    EditText et_description_btn_x;
    EditText et_description_btn_y;
    EditText et_description_btn_up;
    EditText et_description_btn_lt;
    EditText et_description_btn_rt;
    EditText et_description_btn_left;
    EditText et_description_btn_right;
    EditText et_description_btn_lpress;
    EditText et_description_btn_rpress;
    EditText et_description_combo1;
    EditText et_description_combo2;
    EditText et_description_combo3;


    //摇杆
    EditText et_name_joystickL;
    EditText et_radius_joystickL;
    EditText et_coordinate_original_joystickL;
    EditText et_type_joystickL;
    EditText et_description_joystickL;

    EditText et_name_joystickR;
    EditText et_radius_joystickR;
    EditText et_coordinate_original_joystickR;
    EditText et_type_joystickR;
    EditText et_description_joystickR;

    GameLayout gameLayout;
    Button btn_exit_xml;
    HashMap<String,AttrBtn> mapAttrBtn = new HashMap<String,AttrBtn>();
    HashMap<String,AttrJoystick> mapAttrJoystick = new HashMap<String,AttrJoystick>();
    ArrayList<AttrCombo> arrayAttrCombo = new ArrayList<AttrCombo>();//Combo的名称是不确定的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout_preview);

        Intent intent = getIntent();
        String packageName = intent.getStringExtra("packageName");
        System.out.println("package:"+packageName);
        gameLayout = new GameLayout(packageName,true);
        gameLayout.parse();

        et_appname = (EditText)findViewById(R.id.et_appname);
        et_packagename = (EditText)findViewById(R.id.et_packagename);

        et_name_btn_b = (EditText)findViewById(R.id.btn_b);
        et_name_btn_x = (EditText)findViewById(R.id.btn_x);
        et_name_btn_y = (EditText)findViewById(R.id.btn_y);
        et_name_btn_up = (EditText)findViewById(R.id.btn_up);
        et_name_btn_left = (EditText)findViewById(R.id.btn_left);
        et_name_btn_right = (EditText)findViewById(R.id.btn_right);
        et_name_btn_lt = (EditText)findViewById(R.id.btn_lt);
        et_name_btn_rt = (EditText)findViewById(R.id.btn_rt);
        et_name_btn_lpress = (EditText)findViewById(R.id.btn_lpress);
        et_name_btn_rpress = (EditText)findViewById(R.id.btn_rpress);
        et_name_combo1 = (EditText)findViewById(R.id.combo1);
        et_name_combo2 = (EditText)findViewById(R.id.combo2);
        et_name_combo3 = (EditText)findViewById(R.id.combo3);

        et_coordinate_btn_b = (EditText)findViewById(R.id.et_btn_b);
        et_coordinate_btn_x = (EditText)findViewById(R.id.et_btn_x);
        et_coordinate_btn_y = (EditText)findViewById(R.id.et_btn_y);
        et_coordinate_btn_up = (EditText)findViewById(R.id.et_btn_up);
        et_coordinate_btn_left = (EditText)findViewById(R.id.et_btn_left);
        et_coordinate_btn_right = (EditText)findViewById(R.id.et_btn_right);
        et_coordinate_btn_lt = (EditText)findViewById(R.id.et_btn_lt);
        et_coordinate_btn_rt = (EditText)findViewById(R.id.et_btn_rt);
        et_coordinate_btn_lpress = (EditText)findViewById(R.id.et_btn_lpress);
        et_coordinate_btn_rpress = (EditText)findViewById(R.id.et_btn_rpress);

        et_description_btn_b = (EditText)findViewById(R.id.et_btn_b_desc);
        et_description_btn_x = (EditText)findViewById(R.id.et_btn_x_desc);
        et_description_btn_y = (EditText)findViewById(R.id.et_btn_y_desc);
        et_description_btn_up = (EditText)findViewById(R.id.et_btn_up_desc);
        et_description_btn_left = (EditText)findViewById(R.id.et_btn_left_desc);
        et_description_btn_right = (EditText)findViewById(R.id.et_btn_right_desc);
        et_description_btn_lt = (EditText)findViewById(R.id.et_btn_lt_desc);
        et_description_btn_rt = (EditText)findViewById(R.id.et_btn_rt_desc);
        et_description_btn_lpress = (EditText)findViewById(R.id.et_btn_lpress_desc);
        et_description_btn_rpress = (EditText)findViewById(R.id.et_btn_rpress_desc);

        et_name_joystickL = (EditText)findViewById(R.id.joystick_l);
        et_name_joystickR = (EditText)findViewById(R.id.joystick_r);
        et_radius_joystickL = (EditText)findViewById(R.id.et_joystickL_radius);
        et_radius_joystickR = (EditText)findViewById(R.id.et_joystickR_radius);
        et_coordinate_original_joystickL = (EditText)findViewById(R.id.et_joystickL_original);
        et_coordinate_original_joystickR = (EditText)findViewById(R.id.et_joystickR_original);
        et_type_joystickL =(EditText)findViewById(R.id.et_joystickL_type);
        et_type_joystickR =(EditText)findViewById(R.id.et_joystickR_type);
        et_description_joystickL =(EditText)findViewById(R.id.et_joystickL_desc);
        et_description_joystickR =(EditText)findViewById(R.id.et_joystickR_desc);

        et_name_combo1 = (EditText)findViewById(R.id.combo1);
        et_coordinate_combo1 = (EditText)findViewById(R.id.et_coordinate_combo1);
        et_description_combo1 = (EditText)findViewById(R.id.et_description_combo1);
        et_name_combo2 = (EditText)findViewById(R.id.combo2);
        et_coordinate_combo2 = (EditText)findViewById(R.id.et_coordinate_combo2);
        et_description_combo2 = (EditText)findViewById(R.id.et_description_combo2);
        et_name_combo3 = (EditText)findViewById(R.id.combo3);
        et_coordinate_combo3 = (EditText)findViewById(R.id.et_coordinate_combo3);
        et_description_combo3 = (EditText)findViewById(R.id.et_description_combo3);

        mapAttrBtn.put(et_name_btn_b.getText().toString(),new AttrBtn(et_name_btn_b,et_coordinate_btn_b,et_description_btn_b));
        mapAttrBtn.put(et_name_btn_x.getText().toString(),new AttrBtn(et_name_btn_x,et_coordinate_btn_x,et_description_btn_x));
        mapAttrBtn.put(et_name_btn_y.getText().toString(),new AttrBtn(et_name_btn_y,et_coordinate_btn_y,et_description_btn_y));
        mapAttrBtn.put(et_name_btn_up.getText().toString(),new AttrBtn(et_name_btn_up,et_coordinate_btn_up,et_description_btn_up));
        mapAttrBtn.put(et_name_btn_left.getText().toString(),new AttrBtn(et_name_btn_left,et_coordinate_btn_left,et_description_btn_left));
        mapAttrBtn.put(et_name_btn_right.getText().toString(),new AttrBtn(et_name_btn_right,et_coordinate_btn_right,et_description_btn_right));
        mapAttrBtn.put(et_name_btn_lt.getText().toString(),new AttrBtn(et_name_btn_lt,et_coordinate_btn_lt,et_description_btn_lt));
        mapAttrBtn.put(et_name_btn_rt.getText().toString(),new AttrBtn(et_name_btn_rt,et_coordinate_btn_rt,et_description_btn_rt));
        mapAttrBtn.put(et_name_btn_lpress.getText().toString(),new AttrBtn(et_name_btn_lpress,et_coordinate_btn_lpress,et_description_btn_lpress));
        mapAttrBtn.put(et_name_btn_rpress.getText().toString(),new AttrBtn(et_name_btn_rpress,et_coordinate_btn_rpress,et_description_btn_rpress));

        mapAttrJoystick.put(et_name_joystickL.getText().toString(),new AttrJoystick(et_name_joystickL,et_radius_joystickL,et_coordinate_original_joystickL,et_type_joystickL,et_description_joystickL));
        mapAttrJoystick.put(et_name_joystickR.getText().toString(),new AttrJoystick(et_name_joystickR,et_radius_joystickR,et_coordinate_original_joystickR,et_type_joystickR,et_description_joystickR));

        arrayAttrCombo.add(new AttrCombo(et_name_combo1,et_coordinate_combo1,et_description_combo1));
        arrayAttrCombo.add(new AttrCombo(et_name_combo2,et_coordinate_combo2,et_description_combo2));
        arrayAttrCombo.add(new AttrCombo(et_name_combo3,et_coordinate_combo3,et_description_combo3));

        btn_exit_xml = (Button)findViewById(R.id.btn_exit_xml);
        btn_exit_xml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameLayoutPreviewActivity.this.finish();
            }
        });

        initData();
    }

    public void initData(){
        gameLayout.dump();
        et_appname.setText(gameLayout.getGameNameCh());
        et_packagename.setText(gameLayout.getGamePackage());

        HashMap<String,Btn> mapBtns = gameLayout.getBtnsMap();
        for(String name:mapBtns.keySet()){
            System.out.println("mapBtn:"+name);
            Btn btn = mapBtns.get(name);
            AttrBtn attrBtn = mapAttrBtn.get(name);
            attrBtn.setName(btn.getName());
            attrBtn.setPoints(GameLayoutUtils.formatPoints(btn.getPointList()));
            attrBtn.setDescription(btn.getDescription());
        }

        HashMap<String,Joystick> mapJoys = gameLayout.getJoysMap();
        for(String name:mapJoys.keySet()){
            Joystick joystick = mapJoys.get(name);
            AttrJoystick attrJoystick = mapAttrJoystick.get(name);
            attrJoystick.setName(joystick.getName());
            attrJoystick.setOriginal(GameLayoutUtils.formatPoint(joystick.getOriginal()));
            attrJoystick.setRadius(joystick.getRadius());
            attrJoystick.setType(joystick.getType());
            attrJoystick.setDescription(joystick.getDescription());
        }

        HashMap<String,ComboBtn> mapCombos = gameLayout.getCombosMap();
        if(mapCombos != null && mapCombos.size() != 0){
            int comboIndex = 0;
            for(String comboName:mapCombos.keySet()){
                ComboBtn combo = mapCombos.get(comboName);
                if(comboIndex >= 3){
                    break;
                }

                arrayAttrCombo.get(comboIndex).setName(comboName);
                arrayAttrCombo.get(comboIndex).setPoints(GameLayoutUtils.formatPoints(combo.getPointList()));
                arrayAttrCombo.get(comboIndex).setDescription(combo.getDescription());

                comboIndex++;
            }
        }
    }

}

