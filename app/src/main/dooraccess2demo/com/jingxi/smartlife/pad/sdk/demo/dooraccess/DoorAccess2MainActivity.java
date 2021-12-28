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
import android.util.Log;
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

import com.intercom.client.IntercomConstants;
import com.intercom.client.NetClient;
import com.intercom.service.MCUController;
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
public class DoorAccess2MainActivity extends AppCompatActivity implements
        DoorAccessListUI,DoorAccessListener,AdapterView.OnItemClickListener,MCUController.MCUControllerHandler{
    private final String TAG = "DoorAccess2MainActivity";

    ListView listView ;
    ListAdapter listAdapter;
    TextView textView;
    EditText numberEdit;
    List<DoorDevice> mDevices = new ArrayList<>();
    DoorAccessManager manager;
    /**
     * 需要连接的室内机的familyID,一般为室内机的序列号 末尾拼0 为16位字符串
     */
    public static String familyID = "";
    /**
     * 室内机标识主分机所用，移动端使用不重复字符串
     */
    public static String buttonKey = "";

    public static final String SERVER_WORK_DIR = "sdcard/data/doorkeeper/server/conf";

    public static String systemId = Build.SERIAL;
    public static String activeCode = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dooraccess_list);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        numberEdit = (EditText) findViewById(R.id.number);
        Log.w(TAG,"DoorAccessMainActivity onCreate");

        initKit();

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

    private void initKit(){

        /**
         * 多麦克风时
         */
//        WebRtcAudioRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
        /**
         * 麦克风对采样率有要求
         */
//        WebRtcAudioUtils.setDefaultSampleRateHz(32000);

        DoorKit.Options options = DoorKit.getOptions();
        options.platform = IntercomConstants.NetClientSubType.NetClientSubTypeMobile;

        /**
         * 调试,正式版关闭
         */
        options.logDebug = true;

        /**
         * 唯一id，不可重复
         */
        options.systemId = "";
        options.sn = Build.SERIAL;
        options.sslCertPath = "";
        options.alias = "测试机器";
        options.maxRecordCount = 1;
        options.max_audio_delay_us = 120000;
        options.audio_samplerate = 8000;

        initPhone(options);

        options.lan_broadcast_exclude_interface = "eth1";
        options.master_device = false;

        options.video_encode_codec_width = 640;
        options.video_encode_codec_height = 480;
        options.frame_rate = 30;
        options.video_encode_gopsize = 30;
        options.video_encode_avg_bps = 2456000;
        options.video_encode_max_bps = 2456000;

        options.capture_video_fps = 60;
        options.capture_video_width = 640;
        options.capture_video_height = 480;

        options.max_session_wait_time = 2592000;

        DoorKit.init(options);
    }

    private void init(){
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
//        IntercomManager.getInstance().active("123456","123456",activeCode);
        /**
         * 室内机通过此方法获取到familyId
         */
//        familyID = DoorAccessManager.getInstance().getLocalFamilyId(SERVER_WORK_DIR);
        Log.w(TAG,"after init");
        Log.w(TAG,"DoorAccessMainActivity onCreate init familyId = " + familyID );
        manager.startFamily(familyID,buttonKey);

        ((Button)findViewById(R.id.callP2PV)).setText(familyID + "\t" + buttonKey + "\tcallP2P");
        setData();
    }

    private void initPhone(DoorKit.Options options){
        options.useExternalEncoder = false;


        options.video_decode_engine = 0;
        options.video_encode_engine = 0;
        options.useExternalEncoder = false;
        options.audio_layer = IntercomConstants.AudioLayer.kAndroidJavaAudio;
        options.disable_aec = false;
        options.aec_latency = 80;
        options.audio_play_agc_level = 30;
        options.audio_send_denoise = true;
        options.audio_play_denoise = true;
    }


    @Override
    public void onCameraStateChanged(int a){

    }

    @Override
    public void onIntercomAppActivated(int errcode) {

    }

    @Override
    public void onMCUDeviceMessage(String message) {

    }

    @Override
    public void onAudioDeviceStateChanged(int state) {

    }

    @Override
    public void onIntercomAppHangup(int thread_id, String thread_name) {

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
        startActivity(new Intent(this,DoorAccess2PlayBackListActivity.class));
    }

    public void toSecurity(View v){
        startActivity(new Intent(this,DoorAccess2SecurityActivity.class));
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
        Intent intent = new Intent(this,DoorAccess2VideoActivity.class);
        intent.putExtra("sessionId",sessionId);
        startActivity(intent);
    }

    public void toExt(View v){
        if(!DoorAccessManager.getInstance().isSupportExt(familyID)){
            Toast.makeText(this,"不支持室内通",Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this,DoorAccess2ExtActivity.class));
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
        Intent intent = new Intent(this,DoorAccess2VideoActivity.class);
        intent.putExtra("sessionId",sessionId);
        startActivity(intent);
    }

    @Override
    public void onUnLock(String sessionID, String deviceName) {

    }

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
    public void onIntercomFamilyInitialized(String familyId, boolean result) {

    }

    @Override
    public void onCameraError(int err, String from, String reason) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DoorDevice doorDevice = mDevices.get(i);
        String sessionId = manager.monitor(familyID,doorDevice);
        Intent intent = new Intent(this,DoorAccess2VideoActivity.class);
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
                new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE},0x1122);
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
