package com.example.upper.joyevent;

import android.widget.EditText;

/**
 * Created by 10126090 on 2018/10/24.
 */
public class AttrCombo {
    private EditText et_name;
    private EditText et_coordinate;
    private EditText et_description;

    AttrCombo(EditText et_name,EditText et_coordinate,EditText et_description){
        this.et_name = et_name;
        this.et_coordinate = et_coordinate;
        this.et_description = et_description;
    }

    public void setName(String name){
        et_name.setText(name);
    }

    public String getName(){
        return et_name.getText().toString();
    }

    public void setPoints(String strPoints){
        et_coordinate.setText(strPoints);
    }

    public String getPoints(){
        return et_coordinate.getText().toString();
    }

    public void setDescription(String description){
        et_description.setText(description);
    }

    public String getDescription(){
        return et_description.getText().toString();
    }
}
