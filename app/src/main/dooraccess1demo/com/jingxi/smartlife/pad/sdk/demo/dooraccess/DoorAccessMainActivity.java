package com.jingxi.smartlife.pad.sdk.demo.dooraccess;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorRecordBean;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessListUI;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessListener;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 门禁首页，展示设备列表
 */
public class DoorAccessMainActivity extends AppCompatActivity implements
        DoorAccessListUI,DoorAccessListener,AdapterView.OnItemClickListener,MCUController.MCUControllerHandler{
    private final String TAG = "DoorAccessMainActivity_LOG";

    ListView listView ;
    ListAdapter listAdapter;
    TextView textView;
    EditText numberEdit;
    List<DoorDevice> mDevices = new ArrayList<>();
    DoorAccessManager manager;
//    public static String familyID = "GT40K36G00000000";
    public static String familyID = "1234567890123456";
//    public static String familyID = "GS40K36G07650000";
//    public static String familyID = "GR80817070001650";
//    public static String familyID = "GS40000000000000";
    //242_01010306
//    public static String familyID = "00200521C8DA0000";
//242_01010307
//    public static String familyID = "002012088A070000";
//    public static String buttonKey = "199";
    public static String buttonKey = "177";
    public static final String SERVER_WORK_DIR = "sdcard/data/doorkeeper/server/conf";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dooraccess_list);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        numberEdit = (EditText) findViewById(R.id.number);
        Log.w(TAG,"DoorAccessMainActivity onCreate");

        if(requestPermission()){
            init();
        }else{
            Log.w(TAG,"DoorAccessMainActivity onCreate requestPermission faild" );
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init(){
        /**
         * 多麦克风时
         */
//        WebRtcAudioRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
        /**
         * 麦克风对采样率有要求
         */
//        WebRtcAudioUtils.setDefaultSampleRateHz(32000);
        /**
         * 室内机通过此方法获取到familyId
         */
//        familyID = DoorAccessManager.getInstance().getLocalFamilyId(SERVER_WORK_DIR);

        DoorKit.Options options = DoorKit.getOptions();
//        options.proxyServerUrl = "http://transit.hongyanyun.cn:9989/keepalive";
//        options.proxyServerUrl = "http://transit.hongyanyun.cn:9989/keepalive";
//        options.intercomServerUrl = "http://106.13.190.88:9990/opensip/v2/register";
//        options.sn = "GR8081707000162";

//        options.platform = IntercomConstants.NetClientSubType.NetClientSubTypeMobile;
        options.platform = IntercomConstants.NetClientSubType.NetClientSubTypePad;

        options.logDebug = true;
//        options.systemId = Build.SERIAL;
//        options.systemId = "GS40K36G1111";
        options.systemId = "GR8081707000165";
//        options.systemId = "GR8081707000000";
        options.sn = Build.SERIAL;
        options.sslCertPath = "";
        options.alias = "测试机器";
//        options.video_decode_engine = 1;
//        options.video_encode_engine = 1;
        options.video_decode_engine = 0;
        options.video_encode_engine = 0;
        options.maxRecordCount = 1;
//        options.audio_engine = IntercomConstants.AudioEngine.AudioEngine_OpenSLES;
        options.audio_engine = IntercomConstants.AudioEngine.AudioEngine_JAVA;
        options.max_audio_delay_us = 120000;
        options.audio_samplerate = 8000;
//        options.intercomServerUrl = "";
//        options.proxyServerUrl = "";

//        options.disable_aec = false;
//        options.aec_latency = 0;
        options.disable_aec = true;
        options.audio_send_denoise = true;
        options.audio_play_denoise = true;

        options.lan_broadcast_exclude_interface = "";
        options.master_device = false;

        options.capture_video_fps = 15;
//        options.video_encode_codec_width = 320;
//        options.video_encode_codec_height = 240;
        options.frame_rate = 15;
//        options.capture_video_width = 1280;
//        options.capture_video_height = 960;

        options.screen_portrait = false;
//        options.media_codec_encoder_color_format = 0x13;

        options.max_session_wait_time = 2592000;

//        PathUtils.setPrivateDataDirectorySuffix("jx_indoor",this);
        DoorKit.init(options);

        JXContextWrapper.context = getApplication();
        manager = JXPadSdk.getDoorAccessManager();
        DoorAccessManager.getInstance().setListUIListener(this);
        DoorAccessManager.getInstance().setDoorAccessListener(this);
        Log.w(TAG,"will init");
        /**
         * 部分场景下有出现app退出后底层线程没有正确停止，导致下次初始化时参数不正确
         */
        manager.unInit();
        manager.init();
        /**
         ZxIJ-XtNN-Ptul-bFfg
         zy20-AVMe-WFtC-1y7e
         zz1d-3Y0B-0VVf-n8R1
         Zz2p-GlYK-Zt2D-Lqh6
         Zz3r-axB9-gm1I-qWZd
         zzA5-2a9x-BWYi-36rm
         ZZHV-9rvh-QY90-HAIF
         ZZRR-9Vai-7aY0-i6xr
         ZZXS-wc9Y-kewl-emtY
         */
//        IntercomManager.getInstance().active("123456","123456","ZZRR-9Vai-7aY0-i6xr");
        /**
         * GS40K36G1111
         */
//        IntercomManager.getInstance().active("123456","123456","ZZXS-wc9Y-kewl-emtY");
        //gr808***165
//        IntercomManager.getInstance().active("123456","123456","ZxIJ-XtNN-Ptul-bFfg");
        //gr808***000
//        IntercomManager.getInstance().active("123456","123456","0bew-Q4Th-sZ7U-XnzR");

        Log.w(TAG,"after init");
        Log.w(TAG,"DoorAccessMainActivity onCreate init familyId = " + familyID );
        manager.startFamily(familyID,buttonKey);

        ((Button)findViewById(R.id.callP2PV)).setText(familyID + "\t" + buttonKey + "\tcallP2P");
        setData();

    }

    @Override
    public void onCameraStateChanged(int a){

    }

//    @Override
    public void onIntercomAppActivated(int errcode, String type, String message) {

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
//        startActivity(new Intent(this,DoorAccessPlayBackListActivity.class));
        init();
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
//        String number = "912_01010102";
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

    public void clearAllRecord(View v){
        try {
            DoorAccessManager.getInstance().deleteAllByType(familyID, DoorRecordBean.RECORD_TYPE_DOOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deleteFile(new File(Environment.getExternalStorageDirectory(),"data/doorkeeper/access/record"));
    }

    private void deleteFile(File dir){
        if(!dir.exists()){
            return;
        }
        File[] listFile = dir.listFiles();
        if(listFile == null || listFile.length == 0){
            return;
        }
        for(File file : listFile){
            if(file.isDirectory()){
                deleteFile(file);
            }else{
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        dir.delete();
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
//        DoorAccessManager.getInstance().openDoor(familyID,doorDevice.name);
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

    private boolean requestPermission(){
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if(permission == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                "android.permission.CHANGE_WIFI_MULTICAST_STATE"},0x1122);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {
        if(i != 0x1122){
            return;
        }
        for(int k = 0 ; k < strings.length; k++){
            if(ints[k] != PackageManager.PERMISSION_GRANTED){
                android.util.Log.w("RequestPermission",strings[k] + " denied");
            }else{
                android.util.Log.w("RequestPermission",strings[k] + " granted");
            }
        }
        super.onRequestPermissionsResult(i, strings, ints);
        init();
    }
}
