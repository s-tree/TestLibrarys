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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.intercom.sdk.IntercomConstants;
import com.intercom.sdk.NetClient;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.DoorDeviceManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.DoorKit;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorEvent;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorRecordBean;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.ExtDeviceBean;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.orm.DoorAccessOrmUtil;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.ui.DoorAccessConversationUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class DoorAccessExtActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        DoorDeviceManager.OnExtDeviceChanged,DoorAccessConversationUI{
    private ListView listview;
    private ListAdapter listAdapter;
    private List<ExtDeviceBean> extDeviceBeans;
    private EditText et_alias;
    private TextView ext_status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dooraccess_ext_list);
        listview = (ListView) findViewById(R.id.listview);
        extDeviceBeans = new ArrayList<>();
        List<ExtDeviceBean> temp = DoorAccessManager.getInstance().getExtDevices(DoorAccessMainActivity.familyID,DoorAccessMainActivity.buttonKey);
        extDeviceBeans.addAll( removeDuplicate(temp));
        listview.setAdapter(listAdapter = new ListAdapter(extDeviceBeans));
        listview.setOnItemClickListener(this);
        et_alias = (EditText) findViewById(R.id.et_alias);
        ext_status = (TextView) findViewById(R.id.ext_status);
        DoorAccessManager.getInstance().setExtDeviceChangedListener(this);
        DoorAccessManager.getInstance().addConversationUIListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DoorAccessManager.getInstance().removeConversationUIListener(this);
        DoorAccessManager.getInstance().setExtDeviceChangedListener(null);
    }

    public void setAlias(View v){
        String alias = et_alias.getText().toString();
        if(TextUtils.isEmpty(alias)){
            Toast.makeText(this,"请输入要设置的备注名",Toast.LENGTH_SHORT).show();
            return;
        }
        DoorKit.Options options = DoorKit.getOptions();
        options.alias = alias;
        DoorKit.init(options);
        /**
         * unInit 后直接就 init 会导致 init 不成功，建议延迟200 毫秒后再 init
         */
        DoorAccessManager.getInstance().unInit();
        Observable.timer(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        DoorAccessManager.getInstance().init();
                        DoorAccessManager.getInstance().startFamily(DoorAccessMainActivity.familyID,DoorAccessMainActivity.buttonKey);
                    }
                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ExtDeviceBean extDeviceBean = extDeviceBeans.get(i);
        String sessionID = DoorAccessManager.getInstance().callExt(DoorAccessMainActivity.familyID,extDeviceBean,true);
        Intent intent = new Intent(this,DoorAccessVideoActivity.class);
        intent.putExtra("sessionId",sessionID);
        startActivity(intent);
    }

    /**
     * 数据中会有多个重复数据
     * @param deviceBeans
     * @return
     */
    private List<ExtDeviceBean> removeDuplicate(List<ExtDeviceBean> deviceBeans){
        HashMap<String,ExtDeviceBean> map = new HashMap<>();
        for(ExtDeviceBean bean : deviceBeans){
            if(bean.clientBean == null){
                continue;
            }
            String buttonKey = bean.clientBean.getButton_key();
            int subType = bean.clientBean.getSub_type();
            String key = buttonKey + "-" + subType;
            if(map.containsKey(key)){
                continue;
            }
            // 暂时允许平板与底座同时存在
//            if(subType == IntercomConstants.NetClientSubType.NetClientSubTypeEmbedded){
//                String padKey = buttonKey + "-" + IntercomConstants.NetClientSubType.NetClientSubTypePad;
//                if(map.containsKey(padKey)){
//                    continue;
//                }
//            }
//            if(subType == IntercomConstants.NetClientSubType.NetClientSubTypePad){
//                String dockKey = buttonKey + "-" + IntercomConstants.NetClientSubType.NetClientSubTypeEmbedded;
//                if(map.containsKey(dockKey)){
//                    map.remove(dockKey);
//                }
//            }
            map.put(key,bean);
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public void onExtChanged(ExtDeviceBean extDeviceBean, boolean isOnline) {
        List<ExtDeviceBean> temp = DoorAccessManager.getInstance().getExtDevices(DoorAccessMainActivity.familyID,DoorAccessMainActivity.buttonKey);
        extDeviceBeans.clear();
        extDeviceBeans.addAll(removeDuplicate(temp));
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void startTransPort(String sessionID) {

    }

    @Override
    public void refreshEvent(DoorEvent event) {
        //----------- ext
        if(event.type != DoorEvent.TYPE_EXT){
            return;
        }
        NetClient netClient = event.netClient;
        if(TextUtils.equals(event.getCmd(),IntercomConstants.kIntercomCommandPickup)){
            ExtDeviceBean extDeviceBean = getDevice(netClient);

            String showName = extDeviceBean.getShowName() ;
            String type = extDeviceBean.clientBean.getSub_type() == IntercomConstants.NetClientSubType.NetClientSubTypeEmbedded ? "底座" :
                    extDeviceBean.clientBean.getSub_type() == IntercomConstants.NetClientSubType.NetClientSubTypePad ? "平板" : "手机";

            boolean isMonitor = DoorAccessOrmUtil.getChatEvent(event.sessionId) == DoorRecordBean.CALL_EVENT_EXT_MONITOR;
            if(isMonitor){
                ext_status.setText("正在监控 " + showName + " " + type);
                return;
            }
            boolean isBeMonitor = DoorAccessOrmUtil.getChatEvent(event.sessionId) == DoorRecordBean.CALL_EVENT_EXT_BE_MONITORED;
            if(isBeMonitor){
                ext_status.setText("正在被 " + showName + " " + type + " 监控");
                return;
            }
            ext_status.setText("正在与 " + showName + " " + type + " 通话");
        }
    }

    @Override
    public int inviteIntercept(DoorEvent inviteEvent) {
        return 0;
    }

    private ExtDeviceBean getDevice(NetClient netClient){
        for(ExtDeviceBean deviceBean : extDeviceBeans){
            if(TextUtils.equals(deviceBean.clientId,netClient.getClient_id())){
                return deviceBean;
            }
        }
        return null;
    }

    private static class ListAdapter extends BaseAdapter {
        private List<ExtDeviceBean> devices;
        public ListAdapter(List<ExtDeviceBean> devices){
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
            ExtDeviceBean extDeviceBean = devices.get(i);
            String showName = extDeviceBean.getShowName() ;
            String type = extDeviceBean.clientBean.getSub_type() == IntercomConstants.NetClientSubType.NetClientSubTypeEmbedded ? "底座" :
                    extDeviceBean.clientBean.getSub_type() == IntercomConstants.NetClientSubType.NetClientSubTypePad ? "平板" : "手机";
            textView.setText(showName + "\t" + type);
            return view;
        }
    }
}
