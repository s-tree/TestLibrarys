package com.jingxi.smartlife.pad.sdk.neighbor.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.GoDetailBusBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.SetTitleBusBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.InputMethodUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.widget.NeighborTitleHolder;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.annotation.BusReceiver;

public class NeighborMainFragment extends Fragment implements NeighborTitleHolder.OnTitleClickListener{
    public NeighborListFragment listFragment;
    private FragmentManager fragmentManager;
    private NeighborTitleHolder titleHolder;
    private NeighborDetialFragment detailFragment;
    private NeighborDetialFragment myDetailFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_fragment_neighbor_main,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        titleHolder = NeighborTitleHolder.init(view.findViewById(R.id.title));
        titleHolder.setOnTitleClickListener(this);
        fragmentManager = getChildFragmentManager();
        listFragment = new NeighborListFragment();
        fragmentManager.beginTransaction().add(R.id.neighbor_frameLayout, listFragment).commitAllowingStateLoss();
        Bus.getDefault().register(this);
    }

    @Override
    public void onTitleBack() {
        InputMethodUtils.hideSoftInput(getActivity());
        if (detailFragment != null && detailFragment.isAdded()) {
            if (myDetailFragment != null && myDetailFragment.isAdded()) {
                fragmentManager.beginTransaction().remove(detailFragment).commitAllowingStateLoss();
                detailFragment = null;
                fragmentManager.beginTransaction().show(myDetailFragment).commitAllowingStateLoss();
                titleHolder.setTitleText(myDetailFragment.getTitle());
            } else if (listFragment != null && listFragment.isAdded()) {
                fragmentManager.beginTransaction().remove(detailFragment).commitAllowingStateLoss();
                detailFragment = null;
                fragmentManager.beginTransaction().show(listFragment).commitAllowingStateLoss();
                titleHolder.showTab();
            } else if (myDetailFragment == null && listFragment == null) {
                getActivity().onBackPressed();
            }
        } else if (myDetailFragment != null && myDetailFragment.isAdded()) {
            if (listFragment == null) {
                getActivity().onBackPressed();
                listFragment = null;
            } else {
                fragmentManager.beginTransaction().remove(myDetailFragment).commitAllowingStateLoss();
                myDetailFragment = null;
                if (listFragment != null && listFragment.isAdded()) {
                    fragmentManager.beginTransaction().show(listFragment).commitAllowingStateLoss();
                    titleHolder.showTab();
                }
            }
        } else if (listFragment != null && listFragment.isAdded()) {
            getActivity().onBackPressed();
            listFragment = null;
        }
    }

    /**
     * @param detailtype -1 进入详情页，0，我发布的社区新鲜事，1我的消息，2，我赞过的，3 我参加过的活动
     */
    public void goDetail(NeighborInfoBean neighborInfoBean, int detailtype) {
        InputMethodUtils.hideSoftInput(getActivity());
        Bundle bundle = new Bundle();
        if (detailtype == -1) {
            titleHolder.setTitleText(StringUtils.getString(R.string.somebody_neighborhood,neighborInfoBean != null ? neighborInfoBean.familyMemberName: ""));
        } else {
            titleHolder.setTitleText(getString(R.string.my_neighbor_board));
        }
        bundle.putSerializable("data", neighborInfoBean);
        //  bundle.putString("currentType",getTablayout().getSelectedTabPosition())
        bundle.putInt("detailtype", detailtype);
        if (detailtype == -1) {
            /**
             * 进入社区新鲜事
             */
            if (null == detailFragment) {
                detailFragment = new NeighborDetialFragment();
            }
            detailFragment.setArguments(bundle);
            if (detailFragment.isAdded()) {
                fragmentManager.beginTransaction().show(detailFragment).addToBackStack("detail").commitAllowingStateLoss();
            } else {
                fragmentManager.beginTransaction().add(R.id.neighbor_frameLayout, detailFragment).commitAllowingStateLoss();
            }
        } else {
            if (null == myDetailFragment) {
                myDetailFragment = new NeighborDetialFragment();
            }
            myDetailFragment.setArguments(bundle);
            if (myDetailFragment.isAdded()) {
                fragmentManager.beginTransaction().show(myDetailFragment).addToBackStack("my").commitAllowingStateLoss();
            } else {
                fragmentManager.beginTransaction().add(R.id.neighbor_frameLayout, myDetailFragment).commitAllowingStateLoss();
            }
        }
        if (listFragment != null && listFragment.isAdded()) {
            fragmentManager.beginTransaction().hide(listFragment).commitAllowingStateLoss();
        }
    }

    @BusReceiver
    public void onEvent(GoDetailBusBean busBean){
        goDetail(busBean.infoBean,busBean.detailType);
    }

    @BusReceiver
    public void onEvent(SetTitleBusBean setTitleBusBean){
        titleHolder.setTabLayout(listFragment.getTitlePager());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bus.getDefault().unregister(this);
        titleHolder.setOnTitleClickListener(null);
    }
}
