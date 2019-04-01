package com.jingxi.smartlife.pad.sdk.neighbor.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jingxi.smartlife.pad.sdk.JXPadSdk;
import com.jingxi.smartlife.pad.sdk.neighbor.NeighborManager;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.R;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter.CommentAdapter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter.EnrollPeopleAdapter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.adapter.NeighborImgAdapter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.CommentBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.EnrollPeopleBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborDetailDo;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.NeighborInfoBean;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.bean.UpdateData;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.LibTipDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.NeighborhoodActivityDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.NeighborhoodOKDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.dialogs.ReportDialog;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.fragments.NeighborDetialFragment;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.ipresenters.IRightNeighborBoardPresenter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.iviews.IRightNeighborView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.MyAction;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.observer.ResponseObserver;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.presenters.RightNeighborBoardPresenter;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.DisplayUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.InputMethodUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.LibAppUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.StringUtils;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.utils.ToastUtil;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.HeaderLinearLayout;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.HeaderScrollHelper;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.NeighborSwipeRefreshLayout;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.RoundImageView;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.views.ninegrid.PicassoImageLoader;
import com.jingxi.smartlife.pad.sdk.neighbor.ui.xbus.Bus;
import com.jingxi.smartlife.pad.sdk.utils.JXContextWrapper;

import org.reactivestreams.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by lb on 2017/11/10.
 */

public class NeighborRightView extends RelativeLayout implements IRightNeighborView, View.OnClickListener,
        HeaderLinearLayout.OnScrollListener,
        HeaderScrollHelper.ScrollableContainer, SwipeRefreshLayout.OnRefreshListener {
    private RoundImageView riv_neighborPic;
    private TextView tv_neighborName, tv_neighborDate, tv_neighborContent, tv_orginalPrice, tv_neighborPrice, tv_hour, tv_min,
            tv_sec, tv_enrollNumber, tv_enroll, tv_neighborType, tv_commentCount, tv_praiseCount, tv_noComments, tv_enrollEnd, tv_enrollPeople;
    private ImageView iv_warning, iv_praise;
    private RelativeLayout rl_price, rl_enroll, rl_countdown, rl_sendMessage;
    private RecyclerView rv_neighborImgList, rv_comment;
    private View view_whiteBg, view_liner, view_liner2, view_commentCount, view_enrollPeople;
    private EditText et_comment;
    private HeaderLinearLayout scrollableLayout;
    private NeighborSwipeRefreshLayout swipe_refresh;
    private LinearLayoutManager verticalLinearLayoutManager, horizentalLinearLayoutManager;
    private IRightNeighborBoardPresenter rightNeighborBoardPresenter;

    private NeighborImgAdapter neighborImgAdapter;
    private CommentAdapter commentAdapter;
    private EnrollPeopleAdapter enrollPeopleAdapter;
    private NeighborInfoBean neighborInfoBean;
    private int pageIndex;
    private int lastVisibleItem;
    private String replyId;
    private NeighborDetialFragment neighBorDetialFragment;
    private Subscription subscription = null;
    private NeighborhoodActivityDialog neighborhoodActivityDialog;
    private NeighborhoodOKDialog neighborhoodOKDialog;
    private ReportDialog reportDialog;
    private TextView noContent;
    private View rl_neighborInfo;
    private TextView commit_comment;
    private LibTipDialog libTipDialog;

    public NeighborRightView(Context context) {
        this(context, null);
    }

    public NeighborRightView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NeighborRightView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rightNeighborBoardPresenter = new RightNeighborBoardPresenter(this);
    }

    public void initView(NeighborDetialFragment neighBorDetialFragment) {
        this.neighBorDetialFragment = neighBorDetialFragment;
        setBackgroundResource(R.color.white);
        rl_neighborInfo = findViewById(R.id.rl_neighborInfo);
        riv_neighborPic = (RoundImageView) findViewById(R.id.riv_neighborPic);
        tv_neighborName = (TextView) findViewById(R.id.tv_neighborName);
        tv_neighborDate = (TextView) findViewById(R.id.tv_neighborDate);
        tv_neighborContent = (TextView) findViewById(R.id.tv_neighborContent);
        tv_orginalPrice = (TextView) findViewById(R.id.tv_orginalPrice);
        tv_neighborPrice = (TextView) findViewById(R.id.tv_neighborPrice);
        tv_noComments = (TextView) findViewById(R.id.tv_noComments);
        tv_hour = (TextView) findViewById(R.id.tv_hour);
        tv_min = (TextView) findViewById(R.id.tv_min);
        tv_sec = (TextView) findViewById(R.id.tv_sec);
        tv_enrollEnd = (TextView) findViewById(R.id.tv_enrollEnd);
        view_liner = findViewById(R.id.view_liner);
        view_liner2 = findViewById(R.id.view_liner2);
        view_enrollPeople = findViewById(R.id.view_enrollPeople);
        view_commentCount = findViewById(R.id.view_commentCount);
        rl_countdown = (RelativeLayout) findViewById(R.id.rl_countdown);
        rl_sendMessage = (RelativeLayout) findViewById(R.id.rl_sendMessage);
        swipe_refresh = (NeighborSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        scrollableLayout = (HeaderLinearLayout) findViewById(R.id.scrollableLayout);
        scrollableLayout.canPtr();
        scrollableLayout.setOnScrollListener(this);
        scrollableLayout.post(new Runnable() {
            @Override
            public void run() {
                scrollableLayout.setTopOffset((int) (DisplayUtil.getContentViewHeight() - JXContextWrapper.context.getResources().getDimension(R.dimen.dp_54) -
                        rl_sendMessage.getHeight() - JXContextWrapper.context.getResources().getDimension(R.dimen.dp_30)));
            }
        });
        tv_enrollNumber = (TextView) findViewById(R.id.tv_enrollNumber);
        tv_enroll = (TextView) findViewById(R.id.tv_enroll);
        tv_neighborType = (TextView) findViewById(R.id.tv_neighborType);
        tv_commentCount = (TextView) findViewById(R.id.tv_commentCount);
        tv_enrollPeople = (TextView) findViewById(R.id.tv_enrollPeople);
        tv_praiseCount = (TextView) findViewById(R.id.tv_praiseCount);
        iv_warning = (ImageView) findViewById(R.id.iv_warning);
        rl_price = (RelativeLayout) findViewById(R.id.rl_price);
        rl_enroll = (RelativeLayout) findViewById(R.id.rl_enroll);
        noContent = (TextView) findViewById(R.id.tv_nocontent);
        commit_comment = (TextView) findViewById(R.id.tv_commit_comment);
        verticalLinearLayoutManager = new LinearLayoutManager(getContext());
        horizentalLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        swipe_refresh = (NeighborSwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setProgressBackgroundColorSchemeResource(android.R.color.white);
        //设置刷新时动画的颜色，可以设置4个
        swipe_refresh.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        swipe_refresh.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        rv_neighborImgList = (RecyclerView) findViewById(R.id.rv_neighborImgList);
        rv_neighborImgList.setHasFixedSize(true);
        rv_neighborImgList.setMotionEventSplittingEnabled(false);
        rv_neighborImgList.addItemDecoration(new MyCustomItemDecoration((int) getResources().getDimension(R.dimen.dp_13),
                (int) getResources().getDimension(R.dimen.dp_0),
                (int) getResources().getDimension(R.dimen.dp_0),
                (int) getResources().getDimension(R.dimen.dp_0)));
        rv_neighborImgList.setLayoutManager(horizentalLinearLayoutManager);
        rv_comment = (RecyclerView) findViewById(R.id.rv_comment);
        rv_comment.setHasFixedSize(true);
        rv_comment.setMotionEventSplittingEnabled(false);
        rv_comment.setLayoutManager(verticalLinearLayoutManager);
        view_whiteBg = findViewById(R.id.view_whiteBg);
        et_comment = (EditText) findViewById(R.id.et_comment);
        /**
         * 过滤字符并最大长度为500
         */
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
        et_comment.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodUtils.hideSoftInput(v);
                    if (neighborInfoBean == null) {
                        ToastUtil.showToast(StringUtils.getString(R.string.no_reply_user));
                        return true;
                    }
                    if (TextUtils.isEmpty(et_comment.getText().toString())) {
                        ToastUtil.showToast(StringUtils.getString(R.string.no_input_message));
                        return true;
                    }
                    sendReply(neighborInfoBean.neighborBoardId, replyId, et_comment.getText().toString());
                    et_comment.setText("");
                    return true;
                }
                return false;
            }
        });
        iv_praise = (ImageView) findViewById(R.id.iv_praise);
        swipe_refresh.setOnRefreshListener(this);
        rv_comment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (commentAdapter != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == commentAdapter.getItemCount() && swipe_refresh.isUp()) {
                        if (commentAdapter.load_more_status != 2) {
                            commentAdapter.changeMoreStatus(1);
                        } else {
                            return;
                        }
                        NeighborRightView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rightNeighborBoardPresenter.getComment(neighborInfoBean.neighborBoardId, pageIndex);
                            }
                        }, 200);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItem = verticalLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
        rl_neighborInfo.setOnClickListener(this);
        tv_neighborContent.setOnClickListener(this);
        tv_enroll.setOnClickListener(this);
        iv_praise.setOnClickListener(this);
        iv_warning.setOnClickListener(this);
        tv_enrollPeople.setOnClickListener(this);
        tv_commentCount.setOnClickListener(this);
        commit_comment.setOnClickListener(this);
    }

    private void sendReply(String neighborBoardId,String mReplyId,String content){
        JXPadSdk.getNeighborManager().sendReply(neighborBoardId,replyId,content)
                .subscribe(new ResponseObserver<String>() {
                    @Override
                    public void onResponse(String s) {
                        replyId = "";
                        if (rightNeighborBoardPresenter != null) {
                            onRefresh();
                        }
                    }

                    @Override
                    public void onFaild(String message) {
                        ToastUtil.showToast(message);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_enroll) {
            if (TextUtils.equals(StringUtils.getString(R.string.neighbor_i_want_signup), tv_enroll.getText().toString())) {
                if (neighborhoodActivityDialog == null) {
                    neighborhoodActivityDialog = new NeighborhoodActivityDialog(getContext(), new MyAction<Boolean>() {
                        @Override
                        public void call(Boolean aBoolean) {
                            if (aBoolean) {
                                neighborhoodActivityDialog.dismiss();
                                tv_enroll.setText(StringUtils.getString(R.string.have_signed));
                                tv_enrollNumber.setText(StringUtils.getString(R.string.have_signed_count, neighborInfoBean.joinedCount + 1));
                                tv_enroll.setEnabled(false);
                                view_whiteBg.setVisibility(VISIBLE);
                                if (neighborhoodOKDialog == null) {
                                    neighborhoodOKDialog = new NeighborhoodOKDialog(getContext());
                                }
                                neighborhoodOKDialog.setNeighborhoodType(NeighborhoodOKDialog.NeighborhoodType.ACTIVITY_TYPE);
                                neighborhoodOKDialog.show();
                            }
                        }

                        @Override
                        public void faild(int errorNo) {

                        }
                    });
                }
                neighborhoodActivityDialog.setNeighborBoardId(neighborInfoBean.neighborBoardId);
                neighborhoodActivityDialog.show();
            } else if (TextUtils.equals(StringUtils.getString(R.string.stop_signup_title), tv_enroll.getText().toString())) {
                if (libTipDialog == null) {
                    libTipDialog = new LibTipDialog(getContext(), new MyAction<Object>() {
                        @Override
                        public void call(Object o) {
                            JXPadSdk.getNeighborManager().terminationActivityJoin(neighborInfoBean.neighborBoardId)
                                    .subscribe(new ResponseObserver<String>() {
                                        @Override
                                        public void onResponse(String s) {
                                            ToastUtil.showToast(StringUtils.getString(R.string.stop_signup_ok));
                                            tv_enrollEnd.setVisibility(VISIBLE);
                                            tv_enrollEnd.setText(StringUtils.getString(R.string.have_signed_count, neighborInfoBean.joinedCount));
                                            tv_enrollEnd.setBackgroundColor(getResources().getColor(R.color.transparent));
                                            rl_countdown.setVisibility(GONE);
                                            view_liner.setVisibility(GONE);
                                            view_liner2.setVisibility(GONE);
                                            view_whiteBg.setVisibility(GONE);
                                            tv_enrollNumber.setVisibility(GONE);
                                            tv_enroll.setVisibility(GONE);
                                        }

                                        @Override
                                        public void onFaild(String message) {
                                            ToastUtil.showToast(message);
                                        }
                                    });
                        }

                        @Override
                        public void faild(int errorNo) {

                        }
                    });
                    libTipDialog.setAffirm(StringUtils.getString(R.string.makeSure));
                    libTipDialog.setObject(neighborInfoBean, StringUtils.getString(R.string.makeSure_to_stop_signup));
                }
                libTipDialog.show();
            }
        } else if (v.getId() == R.id.iv_praise) {
            NeighborDetailDo neighborDetailDo = new NeighborDetailDo();
            neighborDetailDo.type = NeighborDetailDo.TYPE_FAVOUR;
            if (neighborInfoBean != null) {
                neighborDetailDo.neighborInfoBean = neighborInfoBean;
            } else {
                ToastUtil.showToast(StringUtils.getString(R.string.no_favour_user));
                return;
            }
            neighBorDetialFragment.handleEvent(neighborDetailDo);
        } else if (v.getId() == R.id.iv_warning) {
            if (reportDialog == null) {
                reportDialog = new ReportDialog(getContext(), new MyAction<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            reportDialog.dismiss();
                            if (neighborhoodOKDialog == null) {
                                neighborhoodOKDialog = new NeighborhoodOKDialog(getContext());
                            }
                            neighborhoodOKDialog.setNeighborhoodType(NeighborhoodOKDialog.NeighborhoodType.REPRT_TYPE);
                            neighborhoodOKDialog.show();
                        }
                    }

                    @Override
                    public void faild(int errorNo) {

                    }
                });
                reportDialog.setNeighborBoardId(neighborInfoBean.neighborBoardId);
                reportDialog.show();
            } else {
                reportDialog.setNeighborBoardId(neighborInfoBean.neighborBoardId);
                reportDialog.show();
            }
        } else if (v.getId() == R.id.iv_neighborImg) {
            int index = (int) v.getTag();
            goPreview(index, LibAppUtils.getImageList(neighborInfoBean.images), null);
        } else if (v.getId() == R.id.rl_content) {
            CommentBean bean = (CommentBean) v.getTag();
            et_comment.setHint(StringUtils.getString(R.string.neighbor_reply_with_name, bean.getFamilyMemberName()));
            replyId = String.valueOf(bean.getNeighborBoardReplyId());
            et_comment.requestFocus();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodUtils.showSoftInput(NeighborRightView.this);
                }
            }, 300);
        } else if (v.getId() == R.id.tv_enrollPeople) {
            view_commentCount.setVisibility(GONE);
            view_enrollPeople.setVisibility(VISIBLE);
            if (rightNeighborBoardPresenter != null) {
                rightNeighborBoardPresenter.getEnrollPeople(neighborInfoBean.neighborBoardId);
            }
        } else if (v.getId() == R.id.tv_commentCount) {
            view_commentCount.setVisibility(VISIBLE);
            view_enrollPeople.setVisibility(GONE);
            if (rightNeighborBoardPresenter != null) {
                pageIndex = 1;
                rightNeighborBoardPresenter.getComment(neighborInfoBean.neighborBoardId, pageIndex);
            }
        } else if (v.getId() == R.id.tv_commit_comment) {
            senReply();
        }
    }

    public IRightNeighborBoardPresenter getRightPresenter() {
        return rightNeighborBoardPresenter;
    }

    @Override
    public void setData(final NeighborInfoBean neighborInfoBean) {
        scrollableLayout.setVisibility(View.VISIBLE);
        noContent.setVisibility(View.GONE);
        if (TextUtils.equals(neighborInfoBean.accid, JXContextWrapper.accid)) {
            iv_warning.setVisibility(View.GONE);
        } else {
            iv_warning.setVisibility(View.VISIBLE);
        }
        setTopInfo(neighborInfoBean, true);
        if (rightNeighborBoardPresenter != null) {
            pageIndex = 1;
            rightNeighborBoardPresenter.getComment(neighborInfoBean.neighborBoardId, pageIndex);
        }
    }

    @Override
    public void setComment(List<CommentBean> commentBeanList, String totalSize) {
        scrollableLayout.setCurrentScrollableContainer(this);
        tv_commentCount.setText(StringUtils.getString(R.string.neighbor_reply_with_name2, totalSize));
        if (commentBeanList != null && commentBeanList.size() > 0) {
            tv_noComments.setVisibility(GONE);
            rv_comment.setVisibility(VISIBLE);
            if (commentAdapter == null) {
                commentAdapter = new CommentAdapter(commentBeanList, this);
                rv_comment.setAdapter(commentAdapter);
            } else {
                if (!(rv_comment.getAdapter() instanceof CommentAdapter)) {
                    rv_comment.setAdapter(commentAdapter);
                }
                commentAdapter.setNewData(commentBeanList);
                rv_comment.getLayoutManager().scrollToPosition(0);
                scrollableLayout.scrollTo(0, 0);
            }
            if (commentBeanList.size() < 20) {
                commentAdapter.changeMoreStatus(2);
            } else if (pageIndex != 1 && commentBeanList.size() == 0) {
                commentAdapter.changeMoreStatus(1);
            } else if (commentBeanList.size() == 20) {
                pageIndex += 1;
                commentAdapter.changeMoreStatus(3);
            }
        } else {
            tv_noComments.setText(R.string.noComments);
            tv_noComments.setVisibility(VISIBLE);
            rv_comment.setVisibility(GONE);
            if (!(rv_comment.getAdapter() instanceof CommentAdapter)) {
                rv_comment.setAdapter(commentAdapter);
            }
        }
        neighborInfoBean.replyCounts = Integer.parseInt(totalSize);
        neighBorDetialFragment.setLeftNeighborBean(0, neighborInfoBean);
        Bus.getDefault().post(new UpdateData().setId(UpdateData.TYPE_NEIGHBOR_UPDATE_COMMENT).setJsonObject(JSONObject.parseObject(JSON.toJSONString(neighborInfoBean))));
    }

    @Override
    public void loadMoreComment(List<CommentBean> commentBeanList) {
        scrollableLayout.setCurrentScrollableContainer(this);
        List<Integer> positionList = new ArrayList<>();
        for (int i = 0; i < commentAdapter.getData().size(); i++) {
            for (int j = 0; j < commentBeanList.size(); j++) {
                if (commentAdapter.getData().get(i).getNeighborBoardReplyId() == commentBeanList.get(j).getNeighborBoardReplyId()) {
                    positionList.add(j);
                }
            }
        }
        if (positionList.size() > 0) {
            for (int k = 0; k < positionList.size(); k++) {
                commentBeanList.remove((int) positionList.get(k));
            }
        }
        if (commentBeanList != null && commentBeanList.size() > 0) {
            commentAdapter.addData(commentBeanList);
        }
        if (commentBeanList.size() < 20) {
            commentAdapter.changeMoreStatus(2);
        } else if (pageIndex != 1 && commentBeanList.size() == 0) {
            commentAdapter.changeMoreStatus(1);
        } else if (commentBeanList.size() == 20) {
            pageIndex += 1;
            commentAdapter.changeMoreStatus(3);
        }
    }

    @Override
    public void setNewData(NeighborInfoBean neighborInfoBean) {
        if (neighborInfoBean != null) {
            swipe_refresh.setVisibility(VISIBLE);
            setData(neighborInfoBean);
            scrollableLayout.scrollTo(0, 0);
            rl_sendMessage.setVisibility(VISIBLE);
            noContent.setVisibility(View.GONE);
            scrollableLayout.setVisibility(View.VISIBLE);
        } else {
            this.neighborInfoBean = neighborInfoBean;
            replyId = "";
            if (subscription != null) {
                subscription.cancel();
            }
            swipe_refresh.setVisibility(GONE);
            rl_sendMessage.setVisibility(GONE);
            if (neighBorDetialFragment != null) {
                Drawable drawable = null;
                if (neighBorDetialFragment.detailtype == -1 || neighBorDetialFragment.detailtype == 0) {
                    drawable = ContextCompat.getDrawable(getContext(), R.mipmap.neighborboard_no_publish);
                    noContent.setText(R.string.no_published);
                } else if (neighBorDetialFragment.detailtype == 2) {
                    drawable = ContextCompat.getDrawable(getContext(), R.mipmap.neighborboard_no_favoured);
                    noContent.setText(R.string.no_favoured_other);
                } else if (neighBorDetialFragment.detailtype == 3) {
                    drawable = ContextCompat.getDrawable(getContext(), R.mipmap.neighborboard_no_activity);
                    noContent.setText(R.string.no_joined_activity);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                noContent.setCompoundDrawables(null, drawable, null, null);
            }
            noContent.setVisibility(View.VISIBLE);
            scrollableLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void stopRefresh() {
        swipe_refresh.setRefreshing(false);
    }

    @Override
    public void updateFavour(NeighborInfoBean mData) {
        if (TextUtils.equals(mData.neighborBoardId, neighborInfoBean.neighborBoardId)) {
            tv_praiseCount.setText(StringUtils.getString(R.string.neighbor_favour_with_count, mData.favourCounts));
            neighborInfoBean.isFavour = mData.isFavour;
            neighborInfoBean.favourCounts = mData.favourCounts;
            Drawable iv_praiseDrawable = iv_praise.getDrawable();
            if (iv_praiseDrawable == null) {
                if (mData.isFavour) {
                    PicassoImageLoader.getMyPicasso().load(R.mipmap.neighbor_praise_on).into(iv_praise);
                } else {
                    PicassoImageLoader.getMyPicasso().load(R.mipmap.neighbor_praise_off).into(iv_praise);
                }
            } else {
                if (mData.isFavour) {
                    PicassoImageLoader.getMyPicasso().load(R.mipmap.neighbor_praise_on).placeholder(iv_praiseDrawable).into(iv_praise);
                } else {
                    PicassoImageLoader.getMyPicasso().load(R.mipmap.neighbor_praise_off).placeholder(iv_praiseDrawable).into(iv_praise);
                }
            }
        }
    }

    @Override
    public void setEnrollPeople(List<EnrollPeopleBean> enrollPeopleBeanList) {
        if (enrollPeopleBeanList != null && enrollPeopleBeanList.size() > 0) {
            tv_noComments.setVisibility(GONE);
            rv_comment.setVisibility(VISIBLE);
            tv_enrollNumber.setText(StringUtils.getString(R.string.neighbor_signup_count, enrollPeopleBeanList.size()));
            tv_enrollPeople.setText(StringUtils.getString(R.string.neighbor_signup_count, enrollPeopleBeanList.size()));
            if (enrollPeopleAdapter == null) {
                enrollPeopleAdapter = new EnrollPeopleAdapter(enrollPeopleBeanList);
                rv_comment.setAdapter(enrollPeopleAdapter);
            } else {
                if (!(rv_comment.getAdapter() instanceof EnrollPeopleAdapter)) {
                    rv_comment.setAdapter(enrollPeopleAdapter);
                }
                enrollPeopleAdapter.setNewData(enrollPeopleBeanList);
            }
        } else {
            tv_noComments.setText(R.string.no_one_join);
            tv_noComments.setVisibility(VISIBLE);
            rv_comment.setVisibility(GONE);
            if (!(rv_comment.getAdapter() instanceof EnrollPeopleAdapter)) {
                rv_comment.setAdapter(enrollPeopleAdapter);
            }
        }
    }

    @Override
    public void onScroll(int currentY, int maxY) {
        swipe_refresh.setEnabled(currentY == 0);
    }

    @Override
    public View getScrollableView() {
        return rv_comment;
    }

    //预览图片

    public void goPreview(int i, List<String> urlList, View v) {
        /**
         * 不包含这个模块
         */
//        Bundle bundle = null;
//        if (v != null) {
//            v.setTransitionName(RouterConfig.PreviewImageActivity_Image);
//            if (neighBorDetialFragment.getActivity() != null) {
//                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        neighBorDetialFragment.getActivity(),
//                        new Pair<View, String>(v, RouterConfig.PreviewImageActivity_Image));
//                bundle = activityOptions.toBundle();
//            }
//        } else {
//            bundle = new Bundle();
//        }
//        bundle.putInt("index", i);
//        bundle.putSerializable("data", new ArrayList<>(urlList));
//        if (v != null) {
//            bundle.putInt("image_width", v.getWidth());
//            bundle.putInt("image_height", v.getHeight());
//        }
//        String result = "";
//        if (v != null) {
//            result = JumpCenter.jumpWithOptions(getContext(), RouterConfig.getPreviewUri(), bundle);
//        } else {
//            result = JumpCenter.jump(neighBorDetialFragment.getActivity(), RouterConfig.getPreviewUri(), bundle);
//        }
//        if (!TextUtils.isEmpty(result)) {
//            ToastUtil.showToast(result);
//        }
    }

    @Override
    public void setTopInfo(final NeighborInfoBean neighborInfoBeans, boolean first) {
        this.neighborInfoBean = neighborInfoBeans;
        replyId = "";
        if (subscription != null) {
            subscription.cancel();
        }
        noContent.setVisibility(View.GONE);
        if (first) {
            view_commentCount.setVisibility(VISIBLE);
            view_enrollPeople.setVisibility(GONE);
        }
        if (TextUtils.equals(neighborInfoBean.neighborBoardType, NeighborManager.NEIGHBOR_TYPE_CHAT)) {
            rl_enroll.setVisibility(GONE);
            rl_price.setVisibility(GONE);
            tv_enrollPeople.setVisibility(GONE);
            view_enrollPeople.setVisibility(GONE);
        } else if (TextUtils.equals(neighborInfoBean.neighborBoardType, NeighborManager.NEIGHBOR_TYPE_ACTIVITY)) {
            rl_enroll.setVisibility(VISIBLE);
            rl_price.setVisibility(GONE);
            tv_enrollPeople.setVisibility(VISIBLE);
            tv_commentCount.setVisibility(VISIBLE);
            if (first) {
                view_enrollPeople.setVisibility(GONE);
                view_commentCount.setVisibility(VISIBLE);
            }
            tv_commentCount.setText(StringUtils.getString(R.string.neighbor_reply_with_name2, neighborInfoBean.replyCounts));
            if (TextUtils.equals(neighborInfoBean.accid, JXContextWrapper.accid)) {
                if (Long.parseLong(neighborInfoBean.activityDeadline) - System.currentTimeMillis() < 0) {
                    tv_enrollEnd.setVisibility(VISIBLE);
                    tv_enrollEnd.setText(StringUtils.getString(R.string.neighbor_signup_count, neighborInfoBean.joinedCount));
                    tv_enrollEnd.setBackgroundColor(getResources().getColor(R.color.transparent));
                    rl_countdown.setVisibility(GONE);
                    view_liner.setVisibility(GONE);
                    view_liner2.setVisibility(GONE);
                    view_whiteBg.setVisibility(GONE);
                    tv_enrollNumber.setVisibility(GONE);
                    tv_enroll.setVisibility(GONE);
                    tv_enrollPeople.setText(StringUtils.getString(R.string.neighbor_signup_count, neighborInfoBean.joinedCount));
                } else {
                    tv_enrollEnd.setVisibility(GONE);
                    rl_countdown.setVisibility(VISIBLE);
                    view_liner.setVisibility(VISIBLE);
                    view_liner2.setVisibility(VISIBLE);
                    view_whiteBg.setVisibility(VISIBLE);
                    tv_enrollNumber.setVisibility(VISIBLE);
                    tv_enroll.setVisibility(VISIBLE);
                    tv_enroll.setText(StringUtils.getString(R.string.stop_signup_title));
                    tv_enroll.setEnabled(true);
                    view_whiteBg.setVisibility(GONE);
                    tv_enrollNumber.setText(StringUtils.getString(R.string.neighbor_signup_count, neighborInfoBean.joinedCount));
                    tv_enrollPeople.setText(StringUtils.getString(R.string.neighbor_signup_count, neighborInfoBean.joinedCount));
                }
            } else {
                if (Long.parseLong(neighborInfoBean.activityDeadline) - System.currentTimeMillis() < 0) {
                    tv_enrollEnd.setVisibility(VISIBLE);
                    rl_countdown.setVisibility(GONE);
                    view_liner.setVisibility(GONE);
                    view_liner2.setVisibility(GONE);
                    view_whiteBg.setVisibility(GONE);
                    tv_enrollNumber.setVisibility(GONE);
                    tv_enroll.setVisibility(GONE);
                    tv_enrollEnd.setText(StringUtils.getString(R.string.enrollEnd));
                    tv_enrollEnd.setBackgroundColor(getResources().getColor(R.color.color_99ffffff));
                    tv_enrollPeople.setVisibility(GONE);
                } else {
                    tv_enrollEnd.setVisibility(GONE);
                    rl_countdown.setVisibility(VISIBLE);
                    view_liner.setVisibility(VISIBLE);
                    view_liner2.setVisibility(VISIBLE);
                    view_whiteBg.setVisibility(VISIBLE);
                    tv_enrollNumber.setVisibility(VISIBLE);
                    tv_enroll.setVisibility(VISIBLE);
                    tv_enrollPeople.setVisibility(GONE);
                    if (neighborInfoBean.isJoinedActivity) {
                        tv_enroll.setText(StringUtils.getString(R.string.have_signed));
                        tv_enroll.setEnabled(false);
                        view_whiteBg.setVisibility(VISIBLE);
                    } else {
                        tv_enroll.setText(StringUtils.getString(R.string.neighbor_i_want_signup));
                        tv_enroll.setEnabled(true);
                        view_whiteBg.setVisibility(GONE);
                    }
                    tv_enrollNumber.setText(StringUtils.getString(R.string.neighbor_signup_count, neighborInfoBean.joinedCount));
                }
            }
            Flowable.interval(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new FlowableSubscriber<Long>() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            subscription = s;
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onNext(Long aLong) {
                            notifyNeighborActivity();
                            if(subscription != null){
                                subscription.request(1);
                            }
                        }
                    });
        } else if (TextUtils.equals(neighborInfoBean.neighborBoardType, NeighborManager.NEIGHBOR_TYPE_HELP)) {
            rl_enroll.setVisibility(GONE);
            rl_price.setVisibility(GONE);
            tv_enrollPeople.setVisibility(GONE);
            view_enrollPeople.setVisibility(GONE);
        } else if (TextUtils.equals(neighborInfoBean.neighborBoardType, NeighborManager.NEIGHBOR_TYPE_SELL)) {
            rl_enroll.setVisibility(GONE);
            rl_price.setVisibility(VISIBLE);
            tv_enrollPeople.setVisibility(GONE);
            view_enrollPeople.setVisibility(GONE);
            if (TextUtils.isEmpty(neighborInfoBean.originalPrice)) {
                tv_orginalPrice.setVisibility(View.INVISIBLE);
            } else {
                tv_orginalPrice.setVisibility(View.VISIBLE);
                tv_orginalPrice.setText(StringUtils.getString(R.string.oldPrice_with_num, neighborInfoBean.originalPrice));
            }
            tv_neighborPrice.setText(neighborInfoBean.price);
        }
        PicassoImageLoader.getMyPicasso()
                .load(neighborInfoBean.familyMemberHeadImage)
                .placeholder(R.mipmap.mrtx)
                .error(R.mipmap.mrtx)
                .into(riv_neighborPic);
        tv_neighborName.setText(neighborInfoBean.familyMemberName);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        tv_neighborDate.setText(sdf.format(neighborInfoBean.createDate));
        if (TextUtils.isEmpty(neighborInfoBean.content)) {
            tv_neighborContent.setVisibility(GONE);
        } else {
            tv_neighborContent.setVisibility(VISIBLE);
            tv_neighborContent.setText(neighborInfoBean.content);
        }
        if (!TextUtils.isEmpty(neighborInfoBean.images)) {
            rv_neighborImgList.setVisibility(VISIBLE);
            if (neighborImgAdapter == null) {
                neighborImgAdapter = new NeighborImgAdapter(LibAppUtils.getImageList(neighborInfoBean.images), this);
                rv_neighborImgList.setAdapter(neighborImgAdapter);
            } else {
                neighborImgAdapter.setNewData(LibAppUtils.getImageList(neighborInfoBean.images));
            }
        } else {
            rv_neighborImgList.setVisibility(GONE);
        }
        if (neighborInfoBean.isFavour) {
            PicassoImageLoader.getMyPicasso().load(R.mipmap.neighbor_praise_on).into(iv_praise);
        } else {
            PicassoImageLoader.getMyPicasso().load(R.mipmap.neighbor_praise_off).into(iv_praise);
        }
        tv_neighborType.setText(neighborInfoBean.neighborBoardTypeName);
        if (!TextUtils.isEmpty(neighborInfoBean.neighborBoardTypeColor)) {
            tv_neighborType.setBackgroundColor(Color.parseColor(neighborInfoBean.neighborBoardTypeColor));
        }
        tv_praiseCount.setText(StringUtils.getString(R.string.neighbor_favour_with_count, neighborInfoBean.favourCounts));
    }

    private void notifyNeighborActivity() {
        if (Long.parseLong(neighborInfoBean.activityDeadline) - System.currentTimeMillis() > 0) {
            tv_hour.setText(String.valueOf((Long.parseLong(neighborInfoBean.activityDeadline) - System.currentTimeMillis()) / 1000 / 60 / 60));
            tv_min.setText(String.valueOf((Long.parseLong(neighborInfoBean.activityDeadline) - System.currentTimeMillis()) / 1000 / 60 % 60));
            tv_sec.setText(String.valueOf((Long.parseLong(neighborInfoBean.activityDeadline) - System.currentTimeMillis()) / 1000 % 60));
        } else {
            tv_enrollEnd.setVisibility(VISIBLE);
            if (TextUtils.equals(neighborInfoBean.accid, JXContextWrapper.accid)) {
                tv_enrollEnd.setText(StringUtils.getString(R.string.neighbor_signup_count, neighborInfoBean.joinedCount));
                tv_enrollEnd.setBackgroundColor(getResources().getColor(R.color.transparent));
            } else {
                tv_enrollEnd.setText(StringUtils.getString(R.string.enrollEnd));
                tv_enrollEnd.setBackgroundColor(getResources().getColor(R.color.color_99ffffff));
            }
            rl_countdown.setVisibility(GONE);
            view_liner.setVisibility(GONE);
            view_liner2.setVisibility(GONE);
            view_whiteBg.setVisibility(GONE);
            tv_enrollNumber.setVisibility(GONE);
            tv_enroll.setVisibility(GONE);
            if(subscription != null ){
                subscription.cancel();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.cancel();
        }
    }

    @Override
    public void startRefresh() {
        swipe_refresh.setRefreshing(true);
    }

    @Override
    public void sendBean(NeighborInfoBean neighborInfoBean) {
        if (neighBorDetialFragment != null) {
            neighBorDetialFragment.setLeftNeighborBean(1, neighborInfoBean);
            Bus.getDefault().post(new UpdateData().setId(UpdateData.TYPE_UPDATE_NEIGHBOR_COMMENTS_FAV).setJsonObject(JSONObject.parseObject(JSON.toJSONString(neighborInfoBean))));
        }
    }

    @Override
    public void onRefresh() {
        if (neighborInfoBean == null) {
            stopRefresh();
            return;
        }
        if (commentAdapter != null) {
            commentAdapter.changeMoreStatus(3);
        }
        rightNeighborBoardPresenter.getNeighborBoardInfo(neighborInfoBean.neighborBoardId);
        if (view_commentCount.getVisibility() == VISIBLE) {
            pageIndex = 1;
            /**
             * 注释掉防止评论时出现评论区闪的问题
             */
//            if (commentAdapter != null) {
//                commentAdapter.clearData();
//            }
            rightNeighborBoardPresenter.getComment(neighborInfoBean.neighborBoardId, pageIndex);
        } else if (view_enrollPeople.getVisibility() == VISIBLE) {
            rightNeighborBoardPresenter.getEnrollPeople(neighborInfoBean.neighborBoardId);
        }
    }

    private void senReply() {
        InputMethodUtils.hideSoftInput(et_comment);
        if (neighborInfoBean == null) {
            ToastUtil.showToast(StringUtils.getString(R.string.no_reply_user));
            return;
        }
        if (TextUtils.isEmpty(et_comment.getText().toString())) {
            ToastUtil.showToast(StringUtils.getString(R.string.no_input_message));
            return;
        }
        sendReply(neighborInfoBean.neighborBoardId, replyId, et_comment.getText().toString());
        et_comment.setText("");
        iv_praise.setVisibility(View.VISIBLE);
        commit_comment.setVisibility(View.GONE);
    }

    /**
     * 键盘抬起时，隐藏点赞，显示评论
     *
     * @param keyboardVisible
     */
    @Override
    public void onKeyBoardVisibilityChanged(boolean keyboardVisible) {
        if (getVisibility() == View.VISIBLE) {
            if (keyboardVisible) {
                iv_praise.setVisibility(View.GONE);
                commit_comment.setVisibility(View.VISIBLE);
            } else {
                replyId = "";
                et_comment.setHint(StringUtils.getString(R.string.comment));
                iv_praise.setVisibility(View.VISIBLE);
                commit_comment.setVisibility(View.GONE);
            }
        }
    }
}
