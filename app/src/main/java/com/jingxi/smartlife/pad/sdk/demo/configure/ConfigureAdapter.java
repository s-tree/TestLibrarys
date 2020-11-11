package com.jingxi.smartlife.pad.sdk.demo.configure;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingxi.smartlife.pad.configure.adapter.BaseConfigureAdapter;
import com.jingxi.smartlife.pad.configure.bean.IRoomBean;
import com.jingxi.smartlife.pad.configure.bean.RoomBean;
import com.jingxi.smartlife.pad.sdk.demo.DemoApplication;
import com.jingxi.smartlife.pad.sdk.demo.R;

import java.util.List;

public class ConfigureAdapter extends BaseConfigureAdapter<ConfigureAdapter.RoomHolder> {
    View.OnClickListener onClickListener;

    public ConfigureAdapter(String rootName, List<? extends IRoomBean> iRoomBeanList, View.OnClickListener onClickListener) {
        super(rootName, iRoomBeanList);
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoomHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_configure_item,parent,false),onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomHolder holder, int position) {
        IRoomBean iRoomBean = showingList.get(position);
        holder.itemName.setText(iRoomBean.getName());
        if(selectBean == null || !iRoomBean.isSelf(selectBean)){
            holder.itemName.setTextColor(Color.BLACK);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.selectImage.setVisibility(View.GONE);
        }else{
            holder.itemName.setTextColor(ContextCompat.getColor(DemoApplication.application, R.color.btn_press_color));
            holder.itemView.setBackgroundResource(R.color.mp_deep_gray_4A4A4A);
            holder.selectImage.setVisibility(!(iRoomBean instanceof RoomBean) ? View.VISIBLE : View.GONE);
        }
        if(iRoomBean.hasChild() || iRoomBean instanceof RoomBean){
            holder.itemIcon.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(onClickListener);
        }else{
            holder.itemIcon.setVisibility(View.INVISIBLE);
            if(iRoomBean.hasDevice()){
                holder.itemView.setOnClickListener(onClickListener);
                holder.itemName.setEnabled(true);
            }else{
                holder.itemView.setOnClickListener(null);
                holder.itemName.setEnabled(false);
                holder.itemName.setTextColor(ContextCompat.getColor(DemoApplication.application, R.color.mp_text_color_white_alpha_p5_EAEAEA));
            }
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return showingList == null ? 0 : showingList.size();
    }

    public class RoomHolder extends RecyclerView.ViewHolder{
        public TextView itemName;
        public ImageView itemIcon,selectImage;

        public RoomHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemIcon = itemView.findViewById(R.id.itemIcon);
            selectImage = itemView.findViewById(R.id.selectImage);
            itemView.setOnClickListener(onClickListener);
        }
    }
}
