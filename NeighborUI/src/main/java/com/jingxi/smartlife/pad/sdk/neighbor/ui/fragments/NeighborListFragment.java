package com.jingxi.smartlife.pad.sdk.neighbor.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.activity.ReleaseActivity;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.GoDetailBusBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborBoardTypeBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborhoodDo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.SetTitleBusBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.UpdateData;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.LibTipDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.ReleaseTypeDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters.INeighborhoodListPresernter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews.INeighborhoodListView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.presenters.NeighborHoodListPresenter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.transformers.ZoomOutTransformer;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.InputMethodUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.FloatingActionButton;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.RoundImageView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.widget.NeighborhoodView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.annotation.BusReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NeighborListFragment extends Fragment implements INeighborhoodListView,
        View.OnClickListener {
    private List<NeighborBoardTypeBean> types;
    private INeighborhoodListPresernter presenter;
    private ViewPager viewPager;
    private FloatingActionButton floatingActionButton;
    private NeighborhoodAdapter neighborhoodAdapter;
    private ReleaseTypeDialog releaseTypeDialog;
    private LibTipDialog deleteNeighborDialog;
    private TextView myMsg;
    private TextView myPraise;
    private TextView myActivity;
    public RoundImageView head;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_neighbor_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(4);
        /**
         * 默认选中第一个
         */
        viewPager.setCurrentItem(0, true);
        viewPager.setPageTransformer(true, new ZoomOutTransformer());
        myMsg = (TextView) view.findViewById(R.id.tv_neighbor_msg);
        myPraise = (TextView) view.findViewById(R.id.tv_neighbor_mypraise);
        myActivity = (TextView) view.findViewById(R.id.tv_neighbor_activity);
        head = (RoundImageView) view.findViewById(R.id.iv_neighbor_head);
        head.setOnClickListener(this);
        myMsg.setOnClickListener(this);
        myPraise.setOnClickListener(this);
        myActivity.setOnClickListener(this);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_addlinliq);
        floatingActionButton.setOnClickListener(this);
        presenter = new NeighborHoodListPresenter(this);
//        String cache = SpUtil.getInstance().getNeighborhoodTypes();
//        if (!TextUtils.isEmpty(cache)) {
//            types = JSON.parseArray(cache, NeighborBoardTypeBean.class);
//            setTypes((ArrayList<NeighborBoardTypeBean>) types);
//        } else {
//            try {
//                types = JSON.parseArray(LibAppUtils.loadAss("queryNeighborBoardTypeList_default"), NeighborBoardTypeBean.class);
//            } catch (Exception e) {
//                e.printStackTrace();
//                types = new ArrayList<>();
//                NeighborBoardTypeBean typeBean = new NeighborBoardTypeBean();
//                typeBean.neighborBoardTypeId = "all";
//                typeBean.name = StringUtils.getString(R.string.default_neighbor_type_name);
//                types.add(typeBean);
//            }
//            setTypes((ArrayList<NeighborBoardTypeBean>) types);
//        }
//        newNeighborhoodPresernter.getHeadImg();
        presenter.getNeighborType();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Bus.getDefault().unregister(this);
        presenter.destroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_addlinliq) {
            if (getActivity() != null) {
                if (releaseTypeDialog == null) {
                    releaseTypeDialog = new ReleaseTypeDialog(getContext(), this);
                }
                List<NeighborBoardTypeBean> tempBean = new ArrayList<>(types);
                releaseTypeDialog.setTypes(tempBean);
                releaseTypeDialog.show();
            }
        } else if (v.getId() == R.id.iv_boardType) {
            NeighborBoardTypeBean bean = (NeighborBoardTypeBean) v.getTag();
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), ReleaseActivity.class);
                intent.putExtra("releaseType", bean.neighborBoardTypeId);
                intent.putExtra("releaseName", bean.name);
                intent.putExtra("type", bean.type);
                startActivity(intent);
            }
            if (releaseTypeDialog != null) {
                releaseTypeDialog.dismiss();
            }
        } else if (v.getId() == R.id.iv_closeType) {
            releaseTypeDialog.dismiss();
        } else if (v.getId() == R.id.tv_neighbor_msg) {
            willGoDetail(null, 1);
        } else if (v.getId() == R.id.tv_neighbor_mypraise) {
            willGoDetail(null, 2);
        } else if (v.getId() == R.id.tv_neighbor_activity) {
            willGoDetail(null, 3);
        } else if (v.getId() == R.id.iv_neighbor_head) {
            willGoDetail(null, 0);
        }
        InputMethodUtils.hideSoftInput(getActivity());
    }

    @BusReceiver
    public void onEvent(UpdateData data) {
        if (data.getId() == UpdateData.TYPE_ADD_NEIGHBOR) {
            ToastUtil.showToast(StringUtils.getString(R.string.releaseOK));
            presenter.published(data.getJsonObject());
            viewPager.setCurrentItem(0);
        } else if (data.getId() == UpdateData.TYPE_NEIGHBOR_DEL) {
            presenter.updateData(data.getJsonObject(), 3);
        } else if (data.getId() == UpdateData.TYPE_NEIGHBOR_UPDATE_COMMENT
                || data.getId() == UpdateData.TYPE_NEIGHBOR_UPDATE_FAV
                || data.getId() == UpdateData.TYPE_UPDATE_NEIGHBOR_COMMENTS_FAV) {
            presenter.updateData(data.getJsonObject(), 2);
        }
    }

    @BusReceiver
    public void onEvent(NeighborhoodDo neighborhoodDo) {
        if (neighborhoodDo.type == NeighborhoodDo.TYPE_DETAIL) {
            presenter.goDetail(neighborhoodDo.jsonObject);
        } else if (neighborhoodDo.type == NeighborhoodDo.TYPE_FAVOUR) {
            presenter.favourite(neighborhoodDo.jsonObject);
        } else if (neighborhoodDo.type == NeighborhoodDo.TYPE_DELETE) {
            presenter.delete(neighborhoodDo.jsonObject);
        } else if (neighborhoodDo.type == NeighborhoodDo.TYPE_REPORT) {
//            startActivity(new Intent(getActivity(), PublishActivity.class));
        }
    }

    //--------------------------------------------------------------------------------------------------
    @Override
    public void setTypes(ArrayList<NeighborBoardTypeBean> beans) {
        this.types = beans;
        neighborhoodAdapter = new NeighborhoodAdapter(beans);
        viewPager.setAdapter(neighborhoodAdapter);
        Bus.getDefault().post(new SetTitleBusBean());
    }

    public ViewPager getTitlePager(){
        return viewPager;
    }

    @Override
    public void showDeleteDialog(boolean isShow) {
        if (isShow) {
            if (deleteNeighborDialog == null) {
                deleteNeighborDialog = presenter.createDeleteDialog();
            }
            if (!deleteNeighborDialog.isShowing()) {
                deleteNeighborDialog.show();
            }
        } else if (deleteNeighborDialog != null && deleteNeighborDialog.isShowing()) {
            deleteNeighborDialog.dismiss();
        }
    }

    @Override
    public boolean showLoadingDialog(boolean isShow) {
//        if (isShow) {
//            ((BaseLibActivity) getActivity()).showLoadingDialog(true);
//        } else {
//            ((BaseLibActivity) getActivity()).cancelLoadingDialog();
//        }
        return true;
    }

    @Override
    public Context getTheContext() {
        return getActivity();
    }

    @Override
    public void willGoDetail(NeighborInfoBean infoBean, int detailType) {
        GoDetailBusBean busBean = new GoDetailBusBean(infoBean,detailType);
        Bus.getDefault().post(busBean);
    }

    @Override
    public HashMap<String, View> getPagerViews() {
        return neighborhoodAdapter.neighborhoodViews;
    }

    //----------------------------------------------------------------------------------------------

    static class NeighborhoodAdapter extends PagerAdapter {
        private List<NeighborBoardTypeBean> neighborhoodTypeBeens;
        private HashMap<String, View> neighborhoodViews;

        public NeighborhoodAdapter(List<NeighborBoardTypeBean> neighborhoodTypeBeen) {
            this.neighborhoodTypeBeens = neighborhoodTypeBeen;
            neighborhoodViews = new HashMap<>();
        }

        @Override
        public int getCount() {
            return neighborhoodTypeBeens.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return neighborhoodTypeBeens.get(position).name;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            NeighborBoardTypeBean bean = neighborhoodTypeBeens.get(position);
            if (neighborhoodViews.get(bean.neighborBoardTypeId) == null) {
                View v = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_view_neighbor_item, null);
                NeighborhoodView neighborhoodView = (NeighborhoodView) v.findViewById(R.id.neighborhood);
                neighborhoodView.setType(bean.name, bean.neighborBoardTypeId);
                neighborhoodView.init();
                neighborhoodViews.put(bean.neighborBoardTypeId, v);
                container.addView(v);
                return v;
            } else {
                return neighborhoodViews.get(bean.neighborBoardTypeId);
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            neighborhoodViews.remove(object);
        }
//
//        /* 释放所有控件资源，防止内存溢出 */
//        private void unbindDrawables(View view){
//            if (view.getBackground() != null){
//                view.getBackground().setCallback(null);
//            }
//            if (view instanceof ViewGroup && !(view instanceof AdapterView)){
//                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++){
//                    unbindDrawables(((ViewGroup) view).getChildAt(i));
//                }
//                ((ViewGroup) view).removeAllViews();
//            }
//        }
    }

    @Override
    public void setHeadImg(String familyHeadImg) {
//        Picasso.with(baseApplication)
//                .load(LibAppUtils.getImg(familyHeadImg))
//                .placeholder(R.mipmap.mrtx)
//                .error(R.mipmap.mrtx)
//                .into(head);
    }
}
