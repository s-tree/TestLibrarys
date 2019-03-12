package com.jingxi.smartlife.pad.sdk.neighbor.ui.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter.NeighBorhoodNoticeAdapter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.GoDetailBusBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborNoticeBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseListObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.InputMethodUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.NeighborhoodUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.pk.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的消息
 */
public class NeighBorhoodNoticeView extends SwipeRefreshLayout implements
        View.OnClickListener, BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {
    private String neighborBoardId;
    private int pageIndex = 1;
    private NeighBorhoodNoticeAdapter neighBorhoodNoticeAdapter;
    private int pageSize = 20;
    private View noMoreView;
    private RecyclerView recyclerView;
    private RelativeLayout rl_noticeComment;
    private EditText et_noticeComment;
    private LayoutInflater layoutInflater;
    private String replyId;
    private TextView commit_comment;
    private View noContentView;

    public NeighBorhoodNoticeView(Context context) {
        super(context);
        initView(context);
    }

    public NeighBorhoodNoticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        setBackgroundResource(R.color.white);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.neighbor_notice_view, null);
        addView(view);
        commit_comment = (TextView) findViewById(R.id.tv_commit_comment);
        commit_comment.setOnClickListener(this);
        setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        setOnRefreshListener(this);
        setRefreshing(false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_notice);
        recyclerView.setLayoutManager(new MyLinearLayoutManager(context));
        rl_noticeComment = (RelativeLayout) view.findViewById(R.id.rl_noticeComment);
        et_noticeComment = (EditText) view.findViewById(R.id.et_noticeComment);
        et_noticeComment.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (TextUtils.isEmpty(et_noticeComment.getText().toString())) {
                        ToastUtil.showToast(StringUtils.getString(R.string.reply_content_can_not_be_empty));
                        return true;
                    }
                    sendReply();
                    et_noticeComment.setText("");
                    rl_noticeComment.setVisibility(GONE);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 获取我的消息
     */
    private synchronized void queryNeighborBoardNotice() {
        JXPadSdk.getNeighborManager().queryNeighborBoardNotice(pageIndex)
                .subscribe(quertResponse);
    }

    private ResponseListObserver<NeighborNoticeBean> quertResponse = new ResponseListObserver<NeighborNoticeBean>() {
        @Override
        public void onResponse(List<NeighborNoticeBean> neighborNoticeBeans) {
            setRefreshing(false);
            if(!isAttachedToWindow()){
                return;
            }
            setAdapter(neighborNoticeBeans, pageIndex != 1);
        }

        @Override
        public void onFaild(String message) {
            setRefreshing(false);
            if(!isAttachedToWindow()){
                return;
            }
            ToastUtil.showToast(message);
            if (neighBorhoodNoticeAdapter != null) {
                neighBorhoodNoticeAdapter.loadComplete();
            }
            if (neighBorhoodNoticeAdapter == null) {
                List<NeighborNoticeBean> jsonObjectList = new ArrayList<>();
                neighBorhoodNoticeAdapter = new NeighBorhoodNoticeAdapter(jsonObjectList, NeighBorhoodNoticeView.this);
                neighBorhoodNoticeAdapter.openLoadAnimation();
                neighBorhoodNoticeAdapter.openLoadMore(pageSize);
                neighBorhoodNoticeAdapter.setOnLoadMoreListener(NeighBorhoodNoticeView.this);
                recyclerView.setAdapter(neighBorhoodNoticeAdapter);
            }
            neighBorhoodNoticeAdapter.showLoadMoreFailedView((ViewGroup) getParent());
        }
    };

    private void setAdapter(final List<NeighborNoticeBean> jsonObjectList, boolean add) {
        int size = jsonObjectList.size();
        if (neighBorhoodNoticeAdapter == null) {
            neighBorhoodNoticeAdapter = new NeighBorhoodNoticeAdapter(jsonObjectList, this);
            neighBorhoodNoticeAdapter.openLoadAnimation();
            neighBorhoodNoticeAdapter.openLoadMore(pageSize > size ? pageSize : size);
            neighBorhoodNoticeAdapter.setOnLoadMoreListener(this);
            recyclerView.setAdapter(neighBorhoodNoticeAdapter);
        } else {
            if (add) {
                Disposable d = Observable.just(jsonObjectList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map(new Function<List<NeighborNoticeBean>, List<NeighborNoticeBean>>() {
                            @Override
                            public List<NeighborNoticeBean> apply(List<NeighborNoticeBean> neighborNoticeBeen) {
                                return NeighborhoodUtil.getDuplicatedNoticeList(neighBorhoodNoticeAdapter.getData(),jsonObjectList);
                            }
                        }).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<NeighborNoticeBean>>() {
                            @Override
                            public void accept(List<NeighborNoticeBean> neighborNoticeBeans) throws Exception {
                                neighBorhoodNoticeAdapter.addData(neighborNoticeBeans);
                            }
                        });
            } else {
                neighBorhoodNoticeAdapter.setNewData(jsonObjectList);
                neighBorhoodNoticeAdapter.openLoadMore(pageSize > size ? pageSize : size);
            }
        }
        if (jsonObjectList.size() < pageSize) {
            neighBorhoodNoticeAdapter.loadComplete();
            if (neighBorhoodNoticeAdapter.getData().size() == 0) {
                if(noContentView == null){
                    noContentView = LayoutInflater.from(getContext()).inflate(R.layout.neighbor_nocontent, null);
                }
                neighBorhoodNoticeAdapter.addFooterView(noContentView);
            } else {
                if (noMoreView == null) {
                    noMoreView = LayoutInflater.from(getContext()).inflate(R.layout.def_load_more_failed, null);
                }
                ((TextView) noMoreView.findViewById(R.id.tv_prompt)).setText(StringUtils.getString(R.string.no_more_data));
                neighBorhoodNoticeAdapter.addFooterView(noMoreView);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.reply) {
            /**
             * 回复
             */
            NeighborNoticeBean object = (NeighborNoticeBean) v.getTag();
            replyId = String.valueOf(object.replyId);
            neighborBoardId = String.valueOf(object.neighborBoardId);
            rl_noticeComment.setVisibility(VISIBLE);
            et_noticeComment.requestFocus();
            et_noticeComment.setHint(StringUtils.getString(R.string.neighbor_reply_with_name,object.replyMemberName));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodUtils.toggleSoftInput(getContext());
                }
            }, 300);
        } else if (id == R.id.neighborNotice) {
            /**
             * 社区新鲜事详情
             */
            NeighborNoticeBean object = (NeighborNoticeBean) v.getTag();
            goDetail(object);
        } else if (id == R.id.tv_commit_comment) {
            sendReply();
        }
    }

    @Override
    public void onLoadMoreRequested() {
        pageIndex++;
        queryNeighborBoardNotice();
    }

    @Override
    public void onRefresh() {
        if (neighBorhoodNoticeAdapter != null) {
            neighBorhoodNoticeAdapter.removeAllFooterView();
        }
        pageIndex = 1;
        queryNeighborBoardNotice();
    }

    public void goDetail(NeighborNoticeBean jsonObject) {
        clickNeighborhood(jsonObject);
    }

    private boolean onVerify;

    public void clickNeighborhood(final NeighborNoticeBean mData) {
        if (onVerify) {
            return;
        }
        onVerify = true;
        JXPadSdk.getNeighborManager().getNeighborBoardInfo(String.valueOf(mData.neighborBoardId))
                .subscribe(new ResponseObserver<NeighborInfoBean>() {
                    @Override
                    public void onResponse(NeighborInfoBean infoBean) {
                        onVerify = false;
                        GoDetailBusBean busBean = new GoDetailBusBean(infoBean,-1);
                        Bus.getDefault().post(busBean);
                    }

                    @Override
                    public void onFaild(String message) {
                        onVerify = false;
                        ToastUtil.showToast(message);
                    }
                });
    }

    public void setData() {
        pageIndex = 1;
        setRefreshing(true);
        if (neighBorhoodNoticeAdapter != null) {
            neighBorhoodNoticeAdapter.removeAllFooterView();
        }
        queryNeighborBoardNotice();
    }

    public void onKeyBoardVisibilityChanged(boolean keyboardVisible) {
        if (!keyboardVisible) {
            et_noticeComment.setText("");
            rl_noticeComment.setVisibility(GONE);
        }
    }

    private void sendReply(){
        if (TextUtils.isEmpty(et_noticeComment.getText().toString())) {
            ToastUtil.showToast(StringUtils.getString(R.string.reply_content_can_not_be_empty));
            return;
        }
        InputMethodUtils.hideSoftInput(et_noticeComment);
        JXPadSdk.getNeighborManager().sendReply( neighborBoardId, replyId, et_noticeComment.getText().toString())
                .subscribe(new ResponseObserver<String>() {
                    @Override
                    public void onResponse(String s) {
                        replyId = "";
                        neighborBoardId = "";
                        ToastUtil.showToast(StringUtils.getString(R.string.neighbor_reply_ok));
                    }

                    @Override
                    public void onFaild(String message) {
                        ToastUtil.showToast(StringUtils.getString(R.string.neighbor_reply_faild_with_code,message));
                    }
                });
        et_noticeComment.setText("");
        rl_noticeComment.setVisibility(GONE);
    }
}
