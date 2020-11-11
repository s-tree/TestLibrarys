package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.content.Intent;
import android.os.Build;
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

import com.intercom.base.Log;
import com.intercom.sdk.IntercomConstants;
import com.intercom.sdk.IntercomManager;
import com.intercom.sdk.NetClient;
import com.intercom.sdk.peripheral.MCUController;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.DoorKit;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorDevice;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessListUI;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 门禁首页，展示设备列表
 */
public class DoorAccessMainActivity extends AppCompatActivity implements
        DoorAccessListUI,DoorAccessListener,AdapterView.OnItemClickListener,MCUController.MCUControllerHandler{
    ListView listView ;
    ListAdapter listAdapter;
    TextView textView;
    EditText numberEdit;
    List<DoorDevice> mDevices = new ArrayList<>();
    DoorAccessManager manager;
//    public static String familyID = "001904107CF50000";
//    public static String familyID = "A000000000150000";
//    public static String familyID = "GS40K36G09050000";
    public static String familyID = "GS40K36G11110000";
    public static String buttonKey = "01";
    public static final String SERVER_WORK_DIR = "sdcard/data/doorkeeper/server/conf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dooraccess_list);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        numberEdit = (EditText) findViewById(R.id.number);

        familyID = DoorAccessManager.getInstance().getLocalFamilyId(SERVER_WORK_DIR);

        DoorKit.Options options = DoorKit.getOptions();
//        options.sn = "GR8081707000162";

        options.systemId = Build.SERIAL;
        options.sn = Build.SERIAL;
        options.video_decode_engine = 1;
        options.video_encode_engine = 1;
//        options.audio_engine = IntercomConstants.AudioEngine.AudioEngine_OpenSLES;
        options.audio_engine = IntercomConstants.AudioEngine.AudioEngine_JAVA;
        options.max_audio_delay_us = 50000;
        options.intercomServerUrl = "";
        options.proxyServerUrl = "";
        options.proxykeepalive = 24 * 60 * 60;
        options.proxyRetryInterval = 24 * 60 * 60;
        options.disable_aec = false;
        options.aec_latency = 0;
        options.lan_broadcast_exclude_interface = "eth1";
        options.master_device = true;
        DoorKit.init(options);

        JXPadSdk.init(getApplication());
        JXPadSdk.initDoorAccess();
        manager = JXPadSdk.getDoorAccessManager();
        manager.setListUIListener(this);
        manager.setDoorAccessListener(this);
        manager.init();
        manager.startFamily(familyID,buttonKey);

        ((Button)findViewById(R.id.callP2PV)).setText(familyID + "\t" + buttonKey + "\tcallP2P");
        setData();

//        IntercomConfigure.MCUConf mcuConf = new IntercomConfigure.MCUConf("60000:10000:1",
//                "/dev/ttyS0:115200",
//                "0.0.0.0:10004");
//        String  buttonConf = "[\n" +
//                "        {\"key\":5,\"id\":\"pickup\",\"long_pressed_time\":5000,\"repeat_mini_time\":500,\"sound\":\"key\", \"action\":[{\"type\":2,\"event\":\"button:pickup\"},{\"type\":3,\"event\":\"button:service\"}]},\n" +
//                "        {\"key\":1,\"id\":\"unlock\",\"long_pressed_time\":30000,\"repeat_mini_time\":500,\"sound\":\"key\",\"action\":[{\"type\":2,\"event\":\"button:unlock\"},{\"type\":3,\"event\":\"button:resetdevice\"}]},\n" +
//                "        {\"key\":4,\"id\":\"sos\",\"long_pressed_time\":0,\"repeat_mini_time\":500, \"sound\":\"key\",\"action\":[{\"type\":2,\"event\":\"button:sos\"}]},\n" +
//                "        {\"key\":3,\"id\":\"monitor\",\"long_pressed_time\":0,\"repeat_mini_time\":500,\"sound\":\"key\", \"action\":[{\"type\":2,\"event\":\"button:monitor\"}]},\n" +
//                "        {\"key\":2,\"id\":\"user\",\"long_pressed_time\":5000,\"repeat_mini_time\":500,\"sound\":\"key\",\"action\":[{\"type\":2,\"event\":\"button:user\"},{\"type\":3,\"event\":\"button:reset\"}]},\n" +
//                "        {\"key\":6,\"id\":\"pad\",\"long_pressed_time\":0,\"repeat_mini_time\":0,\"sound\":\"\", \"action\":[{\"type\":1,\"event\":\"button:pad_near\"},{\"type\":2,\"event\":\"button:pad_leave\"}]},\n" +
//                "        {\"key\":8,\"id\":\"volume+\",\"long_pressed_time\":0,\"repeat_mini_time\":500,\"sound\":\"key\", \"action\":[{\"type\":2,\"event\":\"button:volume+\"}]},\n" +
//                "        {\"key\":7,\"id\":\"volume-\",\"long_pressed_time\":0,\"repeat_mini_time\":500,\"sound\":\"key\", \"action\":[{\"type\":2,\"event\":\"button:volume-\"}]},\n" +
//                "        {\"key\":9,\"id\":\"human\",\"long_pressed_time\":0,\"repeat_mini_time\":0,\"sound\":\"\", \"action\":[{\"type\":1,\"event\":\"button:human_near\"}]}\n" +
//                "]";
//        DoorAccessManager.getInstance().startMCU(this,mcuConf.toJson(),buttonConf);

//        MCUController mcuController = new MCUController(this);
//        Bundle bundle = new Bundle();
//        bundle.putString("serial", "/dev/ttyS0:115200");
//        bundle.putString("security_bus_name", "@/tmp/securitybus");
//        bundle.putString("watchdog", "60000:10000:1");
//        String muc_conf = BundleToJSON.toString(bundle);
//        String button_conf = "[{\"key\":5,\"id\":\"pickup\",\"long_pressed_time\":0,\"repeat_mini_time\":500,\"sound\":\"key\", \"action\":[{\"type\":2,\"event\":\"button:pickup\"}]},{\"key\":1,\"id\":\"unlock\",\"long_pressed_time\":0,\"repeat_mini_time\":500,\"sound\":\"key\",\"action\":[{\"type\":2,\"event\":\"button:unlock\"}]},{\"key\":4,\"id\":\"sos\",\"long_pressed_time\":0,\"repeat_mini_time\":500, \"sound\":\"key\",\"action\":[{\"type\":2,\"event\":\"button:sos\"}]},{\"key\":3,\"id\":\"monitor\",\"long_pressed_time\":0,\"repeat_mini_time\":500,\"sound\":\"key\", \"action\":[{\"type\":2,\"event\":\"button:monitor\"}]},{\"key\":2,\"id\":\"user\",\"long_pressed_time\":5000,\"repeat_mini_time\":500,\"sound\":\"key\",\"action\":[{\"type\":2,\"event\":\"button:user\"},{\"type\":3,\"event\":\"button:reset\"}]},{\"key\":6,\"id\":\"pad\",\"long_pressed_time\":0,\"repeat_mini_time\":0,\"sound\":\"\", \"action\":[{\"type\":1,\"event\":\"button:pad_near\"},{\"type\":2,\"event\":\"button:pad_leave\"}]},{\"key\":8,\"id\":\"volume+\",\"long_pressed_time\":0,\"repeat_mini_time\":500,\"sound\":\"key\", \"action\":[{\"type\":2,\"event\":\"button:volume+\"}]},{\"key\":7,\"id\":\"volume-\",\"long_pressed_time\":0,\"repeat_mini_time\":500,\"sound\":\"key\", \"action\":[{\"type\":2,\"event\":\"button:volume-\"}]}]";
//        mcuController.start(muc_conf, button_conf);
    }

    @Override
    public void onCameraStateChanged(int a){

    }

    @Override
    public void onMCUDeviceMessage(String message) {

    }

    private void setData(){
        DoorAccessManager.getInstance().getDevices(familyID);
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
        textView.setText("doorOnline " + isDoorDeviceOnLine + " unitOnline = " + isUnitDeviceOnline + " isPropertyDeviceOnLine = " + isPropertyDeviceOnLine + "\n");
    }

    @Override
    public void onBaseButtonClick(NetClient netClient, String cmd, String time) {
        textView.setText("onBaseButtonClick cmd = " + cmd + "\n");
    }

    @Override
    public void onIntercomAppIntialized(boolean result) {
        textView.setText("onIntercomAppIntialized\n");
    }

    @Override
    public void onProxyOnlineStateChanged(String familyID, String proxyId, int router, boolean online) {

    }

    @Override
    public void onProxyMessage(String familyId, NetClient netClient, String message) {

    }

    @Override
    public void onMediaStateChanged(int mediaType, int state) {

    }

    @Override
    public void onSnapshotReady(String familyID, String sessionID, String filePath) {

    }

    @Override
    public void onIntercomClientInitializeResult(IntercomManager.Intercom intercom, boolean result) {

    }

    @Override
    public void onCameraError(int err, String from, String reason) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DoorDevice doorDevice = mDevices.get(i);
        String sessionId = manager.monitor(familyID,doorDevice);
        Intent intent = new Intent(this,DoorAccessVideoActivity.class);
        intent.putExtra("sessionId",sessionId);
        startActivity(intent);
    }

    @Override
    public void onMCUStateChanged(boolean online) {

    }

    @Override
    public void onMCUButtonEvent(int event, String id, String sound) {
        Log.w("test_bug","onMCUButtonEvent event = " + event + " id = " + id + "\n");

    }

    @Override
    public void onMCUButtonClick(String id) {
        textView.setText("onMCUButtonClick id = " + id + "  " + System.currentTimeMillis() + "\n");

    }

    @Override
    public void onMCUMessageArrival(int type, boolean ack, int result, String message) {

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
