package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;

import java.util.Arrays;
import java.util.List;

public class DoorAccess2LauncherActivity extends AppCompatActivity implements View.OnClickListener {
    static List<String> familyIds = null;
    public static String localFamilyId = "";
    LinearLayout.LayoutParams params;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_dooraccess_launcher);
        linearLayout = findViewById(R.id.rootView);
        float density = getResources().getDisplayMetrics().density;
        int height = (int) (density * 50 + 0.5f);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
        DoorAccessManager.getInstance().globalInit(getApplication());
        DoorAccess2LauncherActivity.localFamilyId = DoorAccessManager.getInstance().getLocalFamilyId("/sdcard/data/doorkeeper/server/conf");

        familyIds = Arrays.asList(
                "GS40K36G04470000", "H0TR2WK70W000000",
                "GFKRLL3BXO000000", "GS40K36G11110000","GS4TM10G00660000",
                "GS40L24G10480000","1234567890123456",localFamilyId
        );
        linearLayout.removeAllViews();
        for(String familyId : familyIds){
            if(TextUtils.isEmpty(familyId)){
                continue;
            }
            Button button = new Button(this);
            button.setText(familyId);
            button.setTag(familyId);
            button.setOnClickListener(this);
            linearLayout.addView(button,params);
        }
    }

    @Override
    public void onClick(View v) {
        DoorAccess2MainActivity.familyID = (String) v.getTag();
        startActivity(new Intent(this, DoorAccess2MainActivity.class));
    }
}
