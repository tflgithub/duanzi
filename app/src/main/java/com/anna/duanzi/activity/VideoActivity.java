package com.anna.duanzi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.CommentAdapter;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.HttpHelper;
import com.anna.duanzi.domain.Comment;
import com.anna.duanzi.widget.BadgeView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import fm.jiecao.jcvideoplayer_lib.JCMediaManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.PlayListener;
import fm.jiecao.jcvideoplayer_lib.VideoEvents;

public class VideoActivity extends BaseActivity implements View.OnClickListener {

    private JCVideoPlayer jcVideoPlayer;

    private TflListRecyclerView recyclerView;
    MaterialRefreshLayout refreshLayout;

    private TflListAdapter<Comment> tflListAdapter;

    private AVQuery<Comment> query;

    private List<Comment> dataList = new ArrayList<>();

    private String videoId;

    private int data_skip = 0;//忽略前多少个

    private int data_limit = 20;//最多加载多少条记录。

    CommentPopupWindow commentPopupWindow;

    private ImageView digg_btn, comment_btn;

    private TextView tv_comment, tv_digg_number_animation;

    private android.view.animation.Animation animation;

    //用于判断是否了显示成功或失败，防止重复。
    boolean isShow = false;

    boolean isSuccess = false;

    private BadgeView commentBadgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
        initData();
    }


    private void initView() {
        jcVideoPlayer = (JCVideoPlayer) findViewById(R.id.video_controller);
        jcVideoPlayer.setThumbImageViewScalType(ImageView.ScaleType.FIT_XY);
        recyclerView = (TflListRecyclerView) findViewById(R.id.recycler_view);
        refreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        digg_btn = (ImageView) findViewById(R.id.digg_btn);
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        findViewById(R.id.iv_share).setVisibility(View.INVISIBLE);
        comment_btn = (ImageView) findViewById(R.id.comment_btn);
        animation = AnimationUtils.loadAnimation(this, R.anim.applaud_animation);
        tv_digg_number_animation = (TextView) findViewById(R.id.tv_digg_number_animation);
        digg_btn.setOnClickListener(this);
        tv_comment.setOnClickListener(this);
        mSVProgressHUD = new SVProgressHUD(this);
        commentPopupWindow = new CommentPopupWindow(VideoActivity.this, itemsOnClick);
        //设置弹出窗体需要软键盘
        commentPopupWindow.setSoftInputMode(CommentPopupWindow.INPUT_METHOD_NEEDED);
        //设置模式，和Activity的一样，覆盖，调整大小。
        commentPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        findViewById(R.id.iv_share).setOnClickListener(this);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                if (isShow) {
                    return;
                }
                if (isSuccess) {
                    hud.showSuccessWithStatus("提交成功");
                    initData();
                    tv_comment.setEnabled(false);
                } else {
                    hud.showErrorWithStatus("提交失败");
                }
                isShow = true;
            }
        });
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        tv_comment.setOnClickListener(this);
        recyclerView = (TflListRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                if (query == null) {
                    return;
                }
                query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
                data_skip = data_skip + data_limit;
                query.skip(data_skip);
                query.limit(data_limit);
                query.findInBackground(new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> list, AVException e) {
                        refreshLayout.finishRefresh();
                        if (list != null && list.size() > 0) {
                            tflListAdapter.addData(list);
                        } else {
                            Toast.makeText(VideoActivity.this, "暂无更新评论", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if (query == null) {
                    return;
                }
                query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
                data_skip = data_skip + data_limit;
                query.skip(data_skip);
                query.limit(data_limit);
                query.findInBackground(new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> list, AVException e) {
                        refreshLayout.finishRefreshLoadMore();
                        if (list != null && list.size() > 0) {
                            tflListAdapter.addData(list);
                        } else {
                            Toast.makeText(VideoActivity.this, "没有更多的评论了", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    String videoUrl, title;

    private void initData() {
        videoId = getIntent().getExtras().getString("videoId");
        title = getIntent().getExtras().getString("title");
        videoUrl = getIntent().getExtras().getString("videoUrl");
        String imageUrl = getIntent().getExtras().getString("imageUrl");
        int STATE = getIntent().getExtras().getInt("current_state", jcVideoPlayer.CURRENT_STATE_NORMAL);
        jcVideoPlayer.setUpForVideoActivity(videoUrl, imageUrl);
        jcVideoPlayer.setState(STATE);
        JCMediaManager.intance().setUuid(jcVideoPlayer.uuid);
        jcVideoPlayer.setPlayListener(new PlayListener() {
            @Override
            public void onPlay() {
                HttpHelper.getInstance().clickStatistics(videoId);
            }
        });
        if (STATE == jcVideoPlayer.CURRENT_STATE_NORMAL) {
            jcVideoPlayer.ivStart.performClick();
        }
        tflListAdapter = new CommentAdapter(dataList, this);
        recyclerView.setAdapter(tflListAdapter);
        tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        query = AVObject.getQuery(Comment.class);
        query.whereEqualTo("commentId", videoId);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.limit(data_limit);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> list, AVException e) {
                if (e == null) {
                    tflListAdapter.changeMode(TflListModel.MODE_DATA);
                    tflListAdapter.setData(list);
                }
            }
        });
        countDigg();
        countComment();
    }


    @Override
    public void onBackPressed() {
        jcVideoPlayer.isClickFullscreen = false;
        JCVideoPlayer.releaseAllVideos();
        finish();
    }

    public void onEventMainThread(VideoEvents videoEvents) {
        if (videoEvents.type == VideoEvents.VE_SURFACEHOLDER_FINISH_VIDDEOACTIVITY) {
            jcVideoPlayer.isClickFullscreen = false;
            JCVideoPlayer.releaseAllVideos();
            finish();
        } else if (videoEvents.type == VideoEvents.VE_IS_VIP) {
            jcVideoPlayer.isClickFullscreen = false;
            JCVideoPlayer.releaseAllVideos();
            Intent intent = new Intent(this, MemberActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comment:
                //显示窗口
                commentPopupWindow.showAtLocation(VideoActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                break;
            case R.id.digg_btn:
                digg();
                break;
            case R.id.iv_share:
               // UIUtils.showShare(this, null, true, title, null, null, videoUrl);
                break;
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            commentPopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.fb_btn:
                    postCommit(videoId);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 点赞
     */
    private void digg() {
        tv_digg_number_animation.setVisibility(View.VISIBLE);
        tv_digg_number_animation.setText("+1");
        tv_digg_number_animation.startAnimation(animation);
        AVObject avObject = new AVObject("Digg");
        avObject.put("diggId", videoId);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    diggNum++;
                    tv_digg_number_animation.setText("+" + diggNum);
                    digg_btn.setEnabled(false);
                }
            }
        });
    }

    int diggNum;

    /**
     * 统计点赞
     */
    private void countDigg() {
        AVQuery<AVObject> query = new AVQuery<>("Digg");
        query.whereEqualTo("diggId", videoId);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    diggNum = i;
                    tv_digg_number_animation.setVisibility(View.VISIBLE);
                    tv_digg_number_animation.setText("+" + i);
                } else {
                    // 查询失败
                }
            }
        });
    }

    /**
     * 统计评论
     */
    private void countComment() {
        AVQuery<Comment> query = AVQuery.getQuery("Comment");
        query.whereEqualTo("commentId", videoId);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    commentBadgeView = new BadgeView(VideoActivity.this, comment_btn);
                    commentBadgeView.setText(i + "");
                    commentBadgeView.setTextSize(8);
                    commentBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                    commentBadgeView.setAlpha(1f);
                    commentBadgeView.setBadgeMargin(0, 8);
                    commentBadgeView.show();
                } else {
                    // 查询失败
                }
            }
        });
    }

    /**
     * 发表评论
     */
    private void postCommit(String commentId) {
        mSVProgressHUD.showWithStatus("提交中，请稍后...");
        Comment comment = new Comment();
        comment.put("commentId", commentId);
        comment.put("user", AVUser.getCurrentUser().getMobilePhoneNumber());
        comment.put("commentContent", editText.getText().toString());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    setResult(RESULT_OK);
                    commentPopupWindow.dismiss();
                    isSuccess = true;
                }
                mSVProgressHUD.dismiss();
            }
        });
    }


    private EditText editText;

    class CommentPopupWindow extends PopupWindow {

        private View mMenuView;
        private Button fb_btn;

        public CommentPopupWindow(Activity context, View.OnClickListener itemsOnClick) {
            super(context);
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mMenuView = inflater.inflate(R.layout.comment_popupwindow, null);
            editText = (EditText) mMenuView.findViewById(R.id.edit_comment);
            fb_btn = (Button) mMenuView.findViewById(R.id.fb_btn);
            fb_btn.setOnClickListener(itemsOnClick);
            editText.setFocusable(true);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        fb_btn.setEnabled(true);
                    } else {
                        fb_btn.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            //设置SelectPicPopupWindow的View
            this.setContentView(mMenuView);
            //设置SelectPicPopupWindow弹出窗体的宽
            this.setWidth(ActionMenuView.LayoutParams.MATCH_PARENT);
            //设置SelectPicPopupWindow弹出窗体的高
            this.setHeight(ActionMenuView.LayoutParams.WRAP_CONTENT);
            //设置SelectPicPopupWindow弹出窗体可点击
            this.setFocusable(true);
            //设置SelectPicPopupWindow弹出窗体动画效果
            this.setAnimationStyle(R.style.AnimBottom);
            //实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            //设置PopupWindow弹出窗体的背景
            this.setBackgroundDrawable(dw);
            //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
            mMenuView.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {

                    int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                    int y = (int) event.getY();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (y < height) {
                            dismiss();
                        }
                    }
                    return true;
                }
            });
        }
    }
}
