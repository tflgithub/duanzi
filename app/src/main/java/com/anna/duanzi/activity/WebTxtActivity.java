package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.HttpHelper;
import com.anna.duanzi.domain.Comment;
import com.anna.duanzi.widget.BadgeView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.SaveCallback;

public class WebTxtActivity extends BaseActivity implements OnClickListener {

    WebView mWebView;
    ProgressBar myProgressBar;
    private ImageView comment_btn, digg_btn;

    private TextView tv_comment, tv_digg_number_animation;

    private BadgeView commentBadgeView;

    private android.view.animation.Animation animation;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_txt);
        myProgressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        mWebView = (WebView) findViewById(R.id.wv_content);
        mWebView.loadUrl(getIntent().getExtras().getString("url"));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    myProgressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    myProgressBar.setProgress(newProgress);//设置进度值
                }
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        initView();
        initData();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initView() {
        comment_btn = (ImageView) findViewById(R.id.comment_btn);
        digg_btn = (ImageView) findViewById(R.id.digg_btn);
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        animation = AnimationUtils.loadAnimation(this, R.anim.applaud_animation);
        tv_digg_number_animation = (TextView) findViewById(R.id.tv_digg_number_animation);
        digg_btn.setOnClickListener(this);
        comment_btn.setOnClickListener(this);
        tv_comment.setOnClickListener(this);
    }

    private void initData() {
        id = getIntent().getExtras().getString("id");
        HttpHelper.getInstance().clickStatistics(id);
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
                    commentBadgeView = new BadgeView(WebTxtActivity.this, comment_btn);
                    commentBadgeView.setText(i + "");
                    commentBadgeView.setTextSize(8);
                    commentBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                    commentBadgeView.setAlpha(1f);
                    commentBadgeView.setBadgeMargin(0, 0);
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
                Intent intent = new Intent(WebTxtActivity.this, CommentActivity.class);
                intent.putExtra("commentId", id);
                intent.putExtra("isComment", true);
                startActivityForResult(intent, 1000);
                break;
            case R.id.digg_btn:
                digg();
                break;
            case R.id.comment_btn:
                Intent intent1 = new Intent(WebTxtActivity.this, CommentActivity.class);
                intent1.putExtra("commentId", id);
                startActivity(intent1);
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
}
