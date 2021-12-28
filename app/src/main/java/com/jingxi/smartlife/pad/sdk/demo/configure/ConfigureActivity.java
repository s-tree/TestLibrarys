package com.jingxi.smartlife.pad.sdk.demo.configure;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jingxi.smartlife.pad.configure.ConfigureManager;
import com.jingxi.smartlife.pad.configure.bean.CommunityInfo;
import com.jingxi.smartlife.pad.configure.bean.IRoomBean;
import com.jingxi.smartlife.pad.configure.bean.RoomBean;
import com.jingxi.smartlife.pad.configure.network.BaseEntry;
import com.jingxi.smartlife.pad.sdk.demo.R;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class ConfigureActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView back,submit;
    private RecyclerView recyclerView;
    private ConfigureAdapter configureAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_configure);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(this);
        findViewById(R.id.initData).setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ConfigureManager.initConfigure(this);
//        ConfigureManager.setServerIp("192.168.125.243:8090");
//        ConfigureManager.setServerIp("192.168.125.243:8089");
        ConfigureManager.setServerIp("58.221.205.10:8089");
    }

    private void setData(List<RoomBean> roomBeans){
        if(configureAdapter == null){
            configureAdapter = new ConfigureAdapter("",roomBeans,this);
            recyclerView.setAdapter(configureAdapter);
        }else{
            configureAdapter.resetDatas(roomBeans);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.initData){
            initData();
        }
        else if(v.getId() == R.id.back){
            if(configureAdapter != null){
                configureAdapter.backList();
            }
        }
        else if(v.getId() == R.id.chooseRoomItem){
            int position = (int) v.getTag();
            boolean hasNext = configureAdapter.hasNext(position);
            configureAdapter.onListSelect((Integer) v.getTag());
            if (hasNext) {
                submit.setVisibility(View.GONE);
                submit.setTag(null);
            } else {
                /**
                 * 获取到选中的房间
                 */
                IRoomBean iRoomBean = configureAdapter.selectBean;
                submit.setVisibility(View.VISIBLE);
                submit.setTag(iRoomBean);
            }
        }
        else if(v.getId() == R.id.submit){
            IRoomBean iRoomBean = (IRoomBean) v.getTag();
            boolean isSuccess = ConfigureManager.saveConfig(configureAdapter);
            if(!isSuccess){
                Log.w("test_bug","saveConfig failed");
            }
        }
    }


    public void initData(){
        ConfigureManager.queryCommunityConfs()
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<BaseEntry, CommunityInfo>() {
                    @Override
                    public CommunityInfo apply(BaseEntry baseEntry) throws Exception {
                        if(baseEntry.code != BaseEntry.CODE_SUCCESS){
                            Log.w("test_bug",baseEntry.msg);
                            return null;
                        }
                        return ConfigureManager.parseCommunityInfo(baseEntry.data);
                    }
                })
                .filter(new Predicate<CommunityInfo>() {
                    @Override
                    public boolean test(CommunityInfo communityInfo) throws Exception {
                        if(communityInfo == null){
                            Log.w("test_bug","下载配置失败");
                        }
                        return communityInfo != null;
                    }
                })
                .flatMap(new Function<CommunityInfo, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(CommunityInfo communityInfo) throws Exception {
                        return ConfigureManager.downloadConf(communityInfo.getRoomFile());
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return !TextUtils.isEmpty(s);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                })
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        List<RoomBean> roomBeanList = ConfigureManager.decodeDownloadedConf(s);
                        setData(roomBeanList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
