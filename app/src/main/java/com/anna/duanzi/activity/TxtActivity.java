package com.anna.duanzi.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.HttpHelper;
import com.anna.duanzi.domain.Comment;
import com.anna.duanzi.utils.UIUtils;
import com.anna.duanzi.widget.BadgeView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;

import net.youmi.android.nm.bn.BannerManager;
import net.youmi.android.nm.bn.BannerViewListener;

public class TxtActivity extends BaseActivity implements View.OnClickListener {

    private ImageView comment_btn, digg_btn, iv_back;

    private TextView tv_title, tv_content, tv_comment, tv_digg_number_animation;

    private BadgeView commentBadgeView;

    private android.view.animation.Animation animation;

    private LinearLayout linearLayout;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_txt);
        initView();
        initData();
    }


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            commentPopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.fb_btn:
                    if (!isLogin) {
                        Intent intent = new Intent(TxtActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        postCommit(id);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //用于判断是否了显示成功或失败，防止重复。
    boolean isShow = false;
    boolean isSuccess = false;

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

    private void initView() {
        comment_btn = (ImageView) findViewById(R.id.comment_btn);
        digg_btn = (ImageView) findViewById(R.id.digg_btn);
        iv_back = (ImageView) findViewById(R.id.header_actionbar_back);
        iv_back.setOnClickListener(this);
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        animation = AnimationUtils.loadAnimation(this, R.anim.applaud_animation);
        tv_digg_number_animation = (TextView) findViewById(R.id.tv_digg_number_animation);
        digg_btn.setOnClickListener(this);
        comment_btn.setOnClickListener(this);
        tv_comment.setOnClickListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.main);
        final FrameLayout.LayoutParams layoutParams =
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置广告条的悬浮位置，这里示例为右下角
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        // 获取广告条
        final View bannerView = BannerManager.getInstance(mContext)
                .getBannerView(mContext, new BannerViewListener() {

                    @Override
                    public void onRequestSuccess() {
                    }

                    @Override
                    public void onSwitchBanner() {
                    }

                    @Override
                    public void onRequestFailed() {
                    }
                });
        // 添加广告条到窗口中
        addContentView(bannerView, layoutParams);
        findViewById(R.id.iv_share).setOnClickListener(this);
        commentPopupWindow = new CommentPopupWindow(this, itemsOnClick);
        //设置弹出窗体需要软键盘
        commentPopupWindow.setSoftInputMode(CommentActivity.CommentPopupWindow.INPUT_METHOD_NEEDED);
        //设置模式，和Activity的一样，覆盖，调整大小。
        commentPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
    }


    private void initData() {
        id = getIntent().getExtras().getString("id");
        HttpHelper.getInstance().clickStatistics(id);
        tv_title.setText(getIntent().getExtras().getString("title"));
        tv_content.setText(Html.fromHtml(getIntent().getExtras().getString("content")));
        initComment();
        initDigg();
    }


    /**
     * 获取点赞
     */
    private void initDigg() {
        AVQuery<AVObject> query = new AVQuery<>("Digg");
        query.whereEqualTo("diggId", id);
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
     * 获取评论
     */
    private void initComment() {
        AVQuery<Comment> query = AVQuery.getQuery("Comment");
        query.whereEqualTo("commentId", id);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    commentBadgeView = new BadgeView(TxtActivity.this, comment_btn);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comment:
                commentPopupWindow.showAtLocation(this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                editText.requestFocus();
                break;
            case R.id.digg_btn:
                digg();
                break;
            case R.id.comment_btn:
                Intent intent1 = new Intent(TxtActivity.this, CommentActivity.class);
                intent1.putExtra("commentId", id);
                startActivity(intent1);
                break;
            case R.id.header_actionbar_back:
                finish();
                break;
            case R.id.iv_share:
                UIUtils.shareTxt(this, tv_title.getText().toString(),tv_content.getText().toString());
                break;
        }
    }

    int diggNum;

    /**
     * 点赞
     */
    private void digg() {
        tv_digg_number_animation.setVisibility(View.VISIBLE);
        tv_digg_number_animation.setText("+1");
        tv_digg_number_animation.startAnimation(animation);
        AVObject avObject = new AVObject("Digg");
        avObject.put("diggId", id);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            int count = Integer.valueOf(commentBadgeView.getText().toString()) + 1;
            commentBadgeView.setText(count + "");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    CommentPopupWindow commentPopupWindow;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BannerManager.getInstance(mContext).onDestroy();
    }
}
