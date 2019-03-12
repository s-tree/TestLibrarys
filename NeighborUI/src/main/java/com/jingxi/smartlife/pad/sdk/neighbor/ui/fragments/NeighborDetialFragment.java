package com.jingxi.smartlife.pad.sdk.neighbor.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.GoDetailBusBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborDetailDo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.UpdateData;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseTagObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.InputMethodUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.KeyboardStatusDetector;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.bgabadge.BGABadgeImageView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.widget.NeighBorhoodNoticeView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.widget.NeighborLeftView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.widget.NeighborRightView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.annotation.BusReceiver;

/**
 * Created by lb on 2017/11/13.
 */

public class NeighborDetialFragment extends Fragment implements KeyboardStatusDetector.KeyboardVisibilityListener {
    private FrameLayout fl_right, fl_left;
    private boolean onFavour;
    private NeighborLeftView neighborLeftView;
    private NeighborRightView neighborRightView;
    private NeighBorhoodNoticeView neighBorhoodNoticeView;
    private NeighborInfoBean nowNeighbor;
    private TabLayout tablayout;
    private String bz;
    public int detailtype;
    private View line;
    private KeyboardStatusDetector keyboardStatusDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.neighbor_detail_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        keyboardStatusDetector = new KeyboardStatusDetector()
                .registerFragment(this)
                .setmVisibilityListener(this);
        tablayout = (TabLayout) view.findViewById(R.id.tablayout);
        line = view.findViewById(R.id.line);
        Bundle bundle = getArguments();
        nowNeighbor = (NeighborInfoBean) bundle.getSerializable("data");
        int detailtype = bundle.getInt("detailtype");
        neighborLeftView = (NeighborLeftView) LayoutInflater.from(getContext()).inflate(R.layout.neighbor_left_detail, null);
        fl_left = (FrameLayout) view.findViewById(R.id.fl_left);
        fl_left.addView(neighborLeftView);
        fl_right = (FrameLayout) view.findViewById(R.id.fl_right);
        neighborLeftView.initView(this);
        this.detailtype = detailtype;
        if (detailtype == -1) {
            neighborRightView = (NeighborRightView) LayoutInflater.from(getContext()).inflate(R.layout.view_right_neighbor_detail, null);
            fl_right.addView(neighborRightView);
            neighborRightView.initView(this);
            bz = "";
            if (neighborLeftView.getleftPresenter() != null) {
                neighborLeftView.getleftPresenter().setData(nowNeighbor, bz, detailtype);
            }
            if (neighborRightView.getRightPresenter() != null) {
                neighborRightView.getRightPresenter().setData(nowNeighbor, bz);
            }
            tablayout.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            setTab(detailtype);
        }
    }

    /**
     * @param neighborDetailDo 0点赞,  1条目点击显示详情， 2条目点击完成跳转 3.我的消息 4.我的消息，条目删除同时小红点减1
     */
    public void handleEvent(NeighborDetailDo neighborDetailDo) {
        if (neighborDetailDo.type == NeighborDetailDo.TYPE_FAVOUR) {
            onFavour(neighborDetailDo.neighborInfoBean);
        } else if (neighborDetailDo.type == NeighborDetailDo.TYPE_DETAIL) {
            if (neighborRightView.getRightPresenter() != null) {
                neighborRightView.getRightPresenter().OnleftItemSelect(neighborDetailDo.neighborInfoBean);
            }
        } else if (neighborDetailDo.type == NeighborDetailDo.TYPE_GO_DETAIL) {
            GoDetailBusBean busBean = new GoDetailBusBean(neighborDetailDo.neighborInfoBean,-1);
            Bus.getDefault().post(busBean);
        } else if (neighborDetailDo.type == NeighborDetailDo.TYPE_MY_MESSAGE) {
//            neighBorhoodNoticeView.setNeighborBoardBean(neighborDetailDo.neighborInfoBean);
            tablayout.getTabAt(0).select();
        } else if (neighborDetailDo.type == NeighborDetailDo.TYPE_MY_DELETE) {
//            neighBorhoodNoticeView.setNeighborBoardBean(neighborDetailDo.neighborInfoBean);/**/
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bus.getDefault().unregister(this);
        if (neighborLeftView != null) {
            neighborLeftView.onDestroy();
        }
        if (neighborRightView != null) {
            neighborRightView.onDestroy();
        }
        if (keyboardStatusDetector != null) {
            keyboardStatusDetector.unregist();
        }
    }

    public void onFavour(NeighborInfoBean neighborInfoBean) {
        if (onFavour) {
            return;
        }
        onFavour = true;
        JXPadSdk.getNeighborManager().updateFavour(
                neighborInfoBean.neighborBoardId,
                !neighborInfoBean.isFavour)
                .subscribe(new ResponseTagObserver<String,NeighborInfoBean>(neighborInfoBean) {
                    @Override
                    public void onResponse(String s) {
                        onFavour = false;
                        if (tag == null) {
                            return;
                        }
                        boolean oldIsFavour = tag.isFavour;
                        tag.favourCounts = tag.favourCounts + (oldIsFavour ? -1 : 1);
                        tag.isFavour = !oldIsFavour;
                        if (neighborLeftView.getleftPresenter() != null) {
                            neighborLeftView.getleftPresenter().updateItem(1, tag);
                        }
                        if (neighborRightView.getRightPresenter() != null) {
                            neighborRightView.getRightPresenter().updateFavour(tag);
                        }
                        UpdateData updateData = new UpdateData();
                        updateData.setId(UpdateData.TYPE_NEIGHBOR_UPDATE_FAV);
                        JSONObject object = JSONObject.parseObject(JSONObject.toJSONString(tag));
                        object.put("from", detailtype == -1 ? -1 : 0);
                        updateData.setJsonObject(object);
                        Bus.getDefault().post(updateData);
                        tag = null;
                    }

                    @Override
                    public void onFaild(String message) {
                        onFavour = false;
                        tag = null;
                        ToastUtil.showToast(message);
                    }
                });
    }

    public String getNowNeighborId() {
        if (nowNeighbor != null) {
            return nowNeighbor.accid;
        }
        return null;
    }

    public int[] icons = {R.drawable.selector_tab_post, R.drawable.selector_tab_msg, R.drawable.selector_tab_favour, R.drawable.selector_tab_activity};
    public String[] titles = {
            StringUtils.getString(R.string.my_reported),
            StringUtils.getString(R.string.my_message),
            StringUtils.getString(R.string.my_favoured),
            StringUtils.getString(R.string.my_joined)};

    private void setTab(int selectIndex) {
        tablayout.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        for (int i = 0; i < icons.length; i++) {
            TabLayout.Tab tab = tablayout.newTab();
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_tab, null);
            TextView tv = (TextView) view.findViewById(R.id.title);
            tv.setText(titles[i]);
            BGABadgeImageView iv = (BGABadgeImageView) view.findViewById(R.id.img);
            iv.setImageResource(icons[i]);
            tab.setCustomView(view);
            tablayout.addTab(tab);
        }
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                InputMethodUtils.hideSoftInput(getActivity());
                detailtype = tab.getPosition();
                initViewByTabPositon(tab, tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tablayout.getTabAt(selectIndex).select();
        if (selectIndex == 0) {
            initViewByTabPositon(null, 0);
        }
        if (selectIndex != 1) {
            /**
             * 直接进入我的消息不需要显示小红点
             */
            setUnRead();
        }
    }

    public void setUnRead() {
        JXPadSdk.getNeighborManager().getCountNeighborBoardNotice()
                .subscribe(new ResponseObserver<Integer>() {
                    @Override
                    public void onResponse(Integer unread) {
                        if (unread > 0) {
                            TabLayout.Tab tab = tablayout.getTabAt(1);
                            ((BGABadgeImageView) tab.getCustomView().findViewById(R.id.img)).showCirclePointBadge();
                        }
                    }

                    @Override
                    public void onFaild(String message) {

                    }
                });
    }

    private void initViewByTabPositon(TabLayout.Tab tab, int selectIndex) {
        if (selectIndex == 0 || selectIndex == 2 || selectIndex == 3) {
            if (neighborRightView == null) {
                neighborRightView = (NeighborRightView) LayoutInflater.from(getContext()).inflate(R.layout.view_right_neighbor_detail, null);
                fl_right.addView(neighborRightView);
                neighborRightView.bringToFront();
                neighborRightView.initView(NeighborDetialFragment.this);
            } else {
                if (neighBorhoodNoticeView != null) {
                    neighBorhoodNoticeView.setRefreshing(false);
                }
                neighborRightView.bringToFront();
            }
        } else if (selectIndex == 1) {
            if (neighBorhoodNoticeView == null) {
                neighBorhoodNoticeView = new NeighBorhoodNoticeView(getContext());
                fl_right.addView(neighBorhoodNoticeView);
                neighBorhoodNoticeView.bringToFront();
                neighBorhoodNoticeView.setData();
            } else {
                if (neighborRightView != null) {
                    neighborRightView.stopRefresh();
                }
                neighBorhoodNoticeView.bringToFront();
                neighBorhoodNoticeView.setData();
            }
            ((BGABadgeImageView) tab.getCustomView().findViewById(R.id.img)).hiddenBadge();
        }
        if (neighborLeftView.getleftPresenter() != null) {
            neighborLeftView.getleftPresenter().setData(null, bz, selectIndex);
        }
    }

    public void setLeftNeighborBean(int type, NeighborInfoBean neighborInfoBean) {
        if (neighborLeftView.getleftPresenter() != null) {
            if (type == 0) {
                neighborLeftView.getleftPresenter().setLeftCommentCount(neighborInfoBean);
            } else if (type == 1) {
                neighborLeftView.getleftPresenter().setLeftbean(neighborInfoBean);
            }

        }
    }

    public String getTitle() {
        if (detailtype == -1) {
            return nowNeighbor.familyMemberName;
        } else {
            return getString(R.string.my_neighbor_board);
        }
    }

    @BusReceiver
    public void onEvent(UpdateData updateData) {
        if (updateData.getId() == UpdateData.TYPE_NEIGHBOR_UPDATE_FAV) {
            JSONObject jsonObject = updateData.getJsonObject();
            if (jsonObject.containsKey("from") && jsonObject.getInteger("from") == (detailtype == -1 ? -1 : 0)) {
                return;
            }
            NeighborInfoBean infoBean = JSONObject.parseObject(jsonObject.toJSONString(), NeighborInfoBean.class);
            if (neighborLeftView.getleftPresenter() != null) {
                neighborLeftView.getleftPresenter().updateItem(1, infoBean);
            }
            if (neighborRightView.getRightPresenter() != null) {
                neighborRightView.getRightPresenter().updateFavour(infoBean);
            }
        } else if (updateData.getId() == UpdateData.TYPE_NEIGHBOR_UPDATE_COMMENT) {
            JSONObject jsonObject = updateData.getJsonObject();
            NeighborInfoBean infoBean = JSONObject.parseObject(jsonObject.toJSONString(), NeighborInfoBean.class);
            if (neighborLeftView.getleftPresenter() != null) {
                neighborLeftView.getleftPresenter().updateItem(2, infoBean);
            }
        } else if (updateData.getId() == UpdateData.TYPE_NEIGHBOR_DEL) {
            JSONObject jsonObject = updateData.getJsonObject();
            if (jsonObject.containsKey("from") && jsonObject.getInteger("from") == (detailtype == -1 ? -1 : 0)) {
                return;
            }
            NeighborInfoBean infoBean = JSONObject.parseObject(jsonObject.toJSONString(), NeighborInfoBean.class);
            if (neighborLeftView.getleftPresenter() != null) {
                neighborLeftView.getleftPresenter().updateItem(0, infoBean);
            }
        } else if (updateData.getId() == UpdateData.TYPE_UPDATE_NEIGHBOR_COMMENTS_FAV) {
            JSONObject jsonObject = updateData.getJsonObject();
            NeighborInfoBean infoBean = JSONObject.parseObject(jsonObject.toJSONString(), NeighborInfoBean.class);
            if (neighborLeftView.getleftPresenter() != null) {
                neighborLeftView.getleftPresenter().updateItem(4, infoBean);
            }
        }
    }

    @Override
    public void onVisibilityChanged(boolean keyboardVisible, int keyBoardHeight) {
        if (neighborRightView != null && detailtype != 1) {
            neighborRightView.onKeyBoardVisibilityChanged(keyboardVisible);
        } else if (neighBorhoodNoticeView != null && detailtype == 1) {
            neighBorhoodNoticeView.onKeyBoardVisibilityChanged(keyboardVisible);
        }
    }
}
