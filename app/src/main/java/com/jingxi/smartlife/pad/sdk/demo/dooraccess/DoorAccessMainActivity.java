package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorDevice;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessListUI;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 门禁首页，展示设备列表
 */
public class DoorAccessMainActivity extends AppCompatActivity implements
        DoorAccessListUI,DoorAccessListener,AdapterView.OnItemClickListener{
    ListView listView ;
    ListAdapter listAdapter;
    TextView textView;
    EditText numberEdit;
    List<DoorDevice> mDevices = new ArrayList<>();
    DoorAccessManager manager;
    public static String familyID = "A000000000050000";
//    public static String familyID = "A000000000150000";
    public static String buttonKey = "02";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dooraccess_list);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        numberEdit = (EditText) findViewById(R.id.number);

        JXPadSdk.init(getApplication());
        JXPadSdk.initDoorAccess();
        manager = JXPadSdk.getDoorAccessManager();
        manager.startFamily(familyID,buttonKey);
        manager.setListUIListener(this);
        manager.setDoorAccessListener(this);

        ((Button)findViewById(R.id.callP2PV)).setText(familyID + "\t" + buttonKey + "\tcallP2P");
        setData();
    }

    private void setData(){
        mDevices.clear();
        List<DoorDevice> devices = manager.getDevices(familyID);
        if(devices != null ){
            mDevices.addAll(devices);
        }
        if(listAdapter == null){
            listAdapter = new ListAdapter(mDevices);
            listView.setAdapter(listAdapter);
        }else{
            listAdapter.notifyDataSetChanged();
        }
    }

    public void toPlayBack(View v){
        startActivity(new Intent(this,DoorAccessPlayBackListActivity.class));
    }

    public void toSecurity(View v){
        startActivity(new Intent(this,DoorAccessSecurityActivity.class));
    }

    public void callP2P(View v){
        if(!DoorAccessManager.getInstance().isSupportP2P(familyID)){
            Toast.makeText(this,"不支持户户通",Toast.LENGTH_SHORT).show();
            return;
        }
        String number = numberEdit.getText().toString();
        if(TextUtils.isEmpty(number)){
            Toast.makeText(this,"请输入房号",Toast.LENGTH_SHORT).show();
            return;
        }
        String sessionId = DoorAccessManager.getInstance().monitorP2P(familyID,number);
        Intent intent = new Intent(this,DoorAccessVideoActivity.class);
        intent.putExtra("sessionId",sessionId);
        startActivity(intent);
    }

    public void toExt(View v){
        if(!DoorAccessManager.getInstance().isSupportExt(familyID)){
            Toast.makeText(this,"不支持室内通",Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this,DoorAccessExtActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refreshList() {
        setData();
    }

    @Override
    public void onRinging(String sessionId) {
        Intent intent = new Intent(this,DoorAccessVideoActivity.class);
        intent.putExtra("sessionId",sessionId);
        startActivity(intent);
    }

    @Override
    public void onUnLock(String sessionID) {

    }

    @Override
    public void onDeviceChanged(String familyId,boolean isDoorDeviceOnLine, boolean isUnitDeviceOnline, boolean isPropertyDeviceOnLine) {
        textView.setText("doorOnline " + isDoorDeviceOnLine + " unitOnline = " + isUnitDeviceOnline + " isPropertyDeviceOnLine = " + isPropertyDeviceOnLine );
    }

    @Override
    public void onBaseButtonClick(String buttonKey, String cmd, String time) {

    }

    @Override
    public void onProxyOnlineStateChanged(String familyID, String proxyId, int router, boolean online) {

    }

    @Override
    public void onSnapshotReady(String familyID, String sessionID, String filePath) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DoorDevice doorDevice = mDevices.get(i);
        String sessionId = manager.monitor(familyID,doorDevice);
        Intent intent = new Intent(this,DoorAccessVideoActivity.class);
        intent.putExtra("sessionId",sessionId);
        startActivity(intent);
    }

    private static class ListAdapter extends BaseAdapter{
        private List<DoorDevice> devices;
        public ListAdapter(List<DoorDevice> devices){
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int i) {
            return devices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView textView = null;
            if(view == null){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_dooraccess_item,null);
                textView = view.findViewById(R.id.textView);
                view.setTag(textView);
            }else{
                textView = (TextView) view.getTag();
            }
            DoorDevice doorDevice = devices.get(i);
            textView.setText(doorDevice.alias + " , " + doorDevice.name );
            textView.setTag(doorDevice);
            return view;
        }
    }

}
