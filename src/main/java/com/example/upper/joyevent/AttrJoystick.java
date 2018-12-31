package com.example.upper.joyevent;

import android.widget.EditText;


/**
 * Created by 10126090 on 2018/10/24.
 */
public class AttrJoystick {
    private EditText et_name;
    private EditText et_radius;
    private EditText et_original;
    private EditText et_type;
    private EditText et_response;
    private EditText et_description;

    AttrJoystick(EditText et_name,EditText et_radius,EditText et_original,EditText et_type,EditText et_description){
        this.et_name = et_name;
        this.et_radius = et_radius;
        this.et_original = et_original;
        this.et_type = et_type;
        this.et_description = et_description;
    }

    public void setName(String name){
        et_name.setText(name);
    }

    public String getName(){
        return et_name.getText().toString();
    }

    public void setOriginal(String strPoint){
        et_original.setText(strPoint);
    }

    public String getOriginal(){
        return et_original.getText().toString();
    }

    public void setType(String strPoint){
        et_type.setText(strPoint);
    }

    public String getType(){
        return et_type.getText().toString();
    }

    public void setRadius(int radius){
        et_radius.setText(""+radius);
    }

    public String getRadius(){
        return et_radius.getText().toString();
    }

    public void setDescription(String description){
        et_description.setText(description);
    }

    public String getDescription(){
        return et_description.getText().toString();
    }
}
