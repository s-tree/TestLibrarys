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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.demo.R;
import com.jingxi.smartlife.pad.sdk.doorAccess.DoorAccessManager;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.DoorRecordBean;
import com.jingxi.smartlife.pad.sdk.doorAccess.base.bean.RecordVideoBean;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 可回放的视频列表
 */
public class DoorAccess2PlayBackListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    ListView listView ;
    ListAdapter listAdapter;
    DoorAccessManager manager;
    private int pageSize = 50;

    List<RecordVideoBean> recordVideoBeans = new ArrayList<>();
    List<DoorRecordBean> doorRecordBeans = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dooraccess_playback_list);
        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(this);

        manager = JXPadSdk.getDoorAccessManager();
        getData();
    }

    /**
     * {@link DoorRecordBean} 是每次会话的记录
     * 单次会话可能会有多个视频记录，表现为  {@link DoorRecordBean#getRecordList()}
     */
    private void getData(){
        List<DoorRecordBean>  recordBeans = manager.getHistoryList(
                Arrays.asList("GFKRLL3BXO000000","GS40K36G04470000","GS40K36G11110000","GS4TM10G00660000"),
                0,pageSize);
//        List<DoorRecordBean>  recordBeans = manager.getHistoryListByType("GS40K36G05420000",DoorRecordBean.RECORD_TYPE_DOOR,0,pageSize);
//        List<DoorRecordBean>  recordBeans = manager.getHistoryList(DoorAccessMainActivity.familyID,DoorRecordBean.RECORD_TYPE_P2P,0,pageSize);
//        List<DoorRecordBean>  recordBeans = manager.getHistoryListByType("GS40K36G05420000",DoorRecordBean.RECORD_TYPE_EXT,0,pageSize);
        for(DoorRecordBean recordBean : recordBeans){
            List<RecordVideoBean> videoBeans = recordBean.getRecordList();
            if(videoBeans.size() == 0){
                continue;
            }
            recordVideoBeans.addAll(recordBean.getRecordList());
        }
        if(listAdapter == null){
            listAdapter = new ListAdapter(recordVideoBeans);
            listView.setAdapter(listAdapter);
        }else{
            listAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        RecordVideoBean recordBean = recordVideoBeans.get(i);
        if(!new File(recordBean.videoPath).exists()){
            Toast.makeText(this,"该记录没有录制视频",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this,DoorAccess2PlayBackActivity.class);
        intent.putExtra("sessionId",recordBean.videoSession);
        intent.putExtra("videoPath",recordBean.videoPath);
        startActivity(intent);
    }

    private static class ListAdapter extends BaseAdapter {
        private List<RecordVideoBean> historys;
        public ListAdapter(List<RecordVideoBean> devices){
            this.historys = devices;
        }

        @Override
        public int getCount() {
            return historys.size();
        }

        @Override
        public Object getItem(int i) {
            return historys.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            SimpleHolder holder = null;
            if(view == null){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_dooraccess_item,null);
                TextView textView = view.findViewById(R.id.textView);
                ImageView imageView = view.findViewById(R.id.imageView);
                TextView textView_status = view.findViewById(R.id.textView_status);
                TextView textView_date = view.findViewById(R.id.textView_date);
                TextView tvDevice = view.findViewById(R.id.textView_device);
                holder = new SimpleHolder(imageView,textView,textView_status,textView_date,tvDevice);
                view.setTag(holder);
            }else{
                holder = (SimpleHolder) view.getTag();
            }
            RecordVideoBean recordBean = historys.get(i);
            holder.textView.setText(recordBean.chatSession);
            if(!TextUtils.isEmpty(recordBean.picPath)){
                holder.imageView.setVisibility(View.VISIBLE);
                Picasso.with(viewGroup.getContext())
                        .load(new File(recordBean.picPath))
                        .resize(100,100)
                        .into(holder.imageView);
            }else{
                holder.imageView.setVisibility(View.GONE);
            }
//            holder.tvDevice.setText("");
//            holder.textView_status.setText(recordBean.getStatusByEvent());
            holder.textView_date.setText(formatNearDay(recordBean.startTime));
            return view;
        }
    }

    public static class SimpleHolder{
        public SimpleHolder(ImageView imageView, TextView textView,TextView textView_status,TextView textView_date,TextView tvDevice) {
            this.imageView = imageView;
            this.textView = textView;
            this.textView_status = textView_status;
            this.textView_date = textView_date;
            this.tvDevice = tvDevice;
        }

        public ImageView imageView;
        public TextView textView;
        public TextView textView_status;
        public TextView textView_date;
        public TextView tvDevice;
    }


    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
    private static SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("MM/dd", Locale.getDefault());
    private static SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    static int nowYear = Calendar.getInstance().get(Calendar.YEAR);
    static int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public static String formatNearDay(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        if(calendar.get(Calendar.YEAR) != nowYear){
            return YEAR_FORMAT.format(calendar.getTime());
        }
        else if(calendar.get(Calendar.DAY_OF_MONTH) != nowDay){
            calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) + 1);
            if(calendar.get(Calendar.DAY_OF_MONTH) == nowDay){
                return "昨天 " + HOUR_FORMAT.format(calendar.getTime());
            }else{
                calendar.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH) -1);
                return MONTH_FORMAT.format(calendar.getTime());
            }
        }
        else{
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            return hour < 12 ? "上午 " + HOUR_FORMAT.format(calendar.getTime()):
                    "下午 " + HOUR_FORMAT.format(calendar.getTime());
        }
    }
}
