package com.jingxi.smartlife.pad.sdk.neighbor.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.fragments.NeighborMainFragment;

public class NeighborUIActivity extends AppCompatActivity {
    int frameLayoutId = R.id.frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JXPadSdk.init(getApplication());
        JXPadSdk.setAccid("y_p_1241_18021651812");
        JXPadSdk.setAppKey("userKey:d38bf3b32e09484b83673c90772442cc","6a591fc521f347bfad171fd2932e60d6");
        JXPadSdk.setCommunityId("1");
        JXPadSdk.setFamilyInfoId("1241");
        JXPadSdk.initNeighbor();

        setContentView(R.layout.layout_activity_neighbor_demo);
        getSupportFragmentManager().beginTransaction().add(frameLayoutId,new NeighborMainFragment()).commitAllowingStateLoss();
    }
}
