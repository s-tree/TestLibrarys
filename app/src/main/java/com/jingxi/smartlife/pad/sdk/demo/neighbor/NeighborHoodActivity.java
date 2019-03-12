package com.jingxi.smartlife.pad.sdk.demo.neighbor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.neighbor.NeighborManager;
import com.jingxi.smartlife.pad.sdk.network.BaseEntry;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 *
 *
 * {
 "code": "2000",
 "result": true,
 "msg": null,
 "content": {
 "ownerName": "小桔子",
 "ownerRealName": null,
 "ownerMobile": "18021651812",
 "ownerAccid": "y_m_18021651812",
 "ownerHeadImage": "http://image.house-keeper.cn/headImage/2018-11-12/B4BB2F25-FCE2-4D1F-AB3F-55164CDC28EA.jpg",
 "ownerId": 992,
 "familyInfoId": 1241,
 "securityEquipment": "门磁传感器;烟雾传感器;水浸传感器;震动传感器;可燃气体传感器;微波传感器;红外探测器;红外入侵感应器",
 "securityMask": null,
 "familyMemberId": 1947,
 "familyNickName": "bule",
 "familyPoint": 1,
 "familyHeadImg": "http://image.house-keeper.cn/headImage/base/family.png",
 "familyAddress": null,
 "padAccid": "y_p_1241_18021651812",
 "padToken": "7560892f40205332412b12cdff4c8585",
 "buildingId": 16,
 "buildingDetailId": 263,
 "communityId": 1,
 "communityName": "京希科技",
 "province": "江苏省",
 "city": "南通市",
 "area": "崇川区",
 "street": "南通市",
 "houseNO": "一期3号楼1单元6楼606室",
 "buildNo": "3",
 "roomString": "1,3,1,6,606",
 "isFree": false,
 "appKey": "userKey:d38bf3b32e09484b83673c90772442cc",
 "accessToken": "6a591fc521f347bfad171fd2932e60d6",
 "ssid": "",
 "password": "",
 "docksn": "",
 "buttonkey": null,
 "dockkey": "",
 "host": null,
 "latitude": "31.932312",
 "longitude": "120.962964",
 "infoVos": null
 },
 "totalSize": null
 }
 *
 */
public class NeighborHoodActivity extends AppCompatActivity {
    NeighborManager neighborManager;
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_neighbor_main);
        textView = (TextView) findViewById(R.id.textView);
        JXPadSdk.init(getApplication());
        JXPadSdk.setAccid("y_p_1241_18021651812");
        JXPadSdk.setAppKey("userKey:d38bf3b32e09484b83673c90772442cc","6a591fc521f347bfad171fd2932e60d6");
        JXPadSdk.setCommunityId("1");
        JXPadSdk.initNeighbor();
        neighborManager = JXPadSdk.getNeighborManager();
    }

    public void getTypeList(View v){
        neighborManager.queryNeighborBoardTypeList()
                .subscribe(new Observer<BaseEntry>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseEntry baseEntry) {
                        textView.setText(baseEntry.content);
                    }

                    @Override
                    public void onError(Throwable e) {
                        textView.setText(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getNeighborList(View v){
        neighborManager.getNeighborBoardList("","1","")
                .subscribe(new Observer<BaseEntry>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseEntry baseEntry) {
                        textView.setText(baseEntry.content);
                    }

                    @Override
                    public void onError(Throwable e) {
                        textView.setText(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void zh(View v){
        JXPadSdk.setLang("zh");
    }

    public void en(View v){
        JXPadSdk.setLang("en");
    }

}
