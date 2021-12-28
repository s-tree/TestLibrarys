package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class DoorAccessLauncherActivity extends AppCompatActivity {
    public static final String class1 = "com.jingxi.smartlife.pad.sdk.demo.dooraccess.DoorAccessMainActivity";
    public static final String class2 = "com.jingxi.smartlife.pad.sdk.demo.dooraccess.DoorAccess2LauncherActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class _class = null;
        try{
            _class = Class.forName(class1);
        }catch (Exception e){
            try{
                _class = Class.forName(class2);
            }catch (Exception e2){

            }
        }
        if(_class == null){
            return;
        }
        startActivity(new Intent(this,_class));
        finish();
    }
}
