package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
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
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.SaveCallback;

public class TxtActivity extends BaseActivity implements View.OnClickListener {

    private ImageView comment_btn, digg_btn, iv_back;

    private TextView tv_title, tv_content, tv_comment, tv_digg_number_animation;

    private BadgeView commentBadgeView;

    private android.view.animation.Animation animation;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_txt);
        initView();
        initData();
    }

    private void initView() {
        comment_btn = (ImageView) findViewById(R.id.comment_btn);
        digg_btn = (ImageView) findViewById(R.id.digg_btn);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        animation = AnimationUtils.loadAnimation(this, R.anim.applaud_animation);
        tv_digg_number_animation = (TextView) findViewById(R.id.tv_digg_number_animation);
        digg_btn.setOnClickListener(this);
        comment_btn.setOnClickListener(this);
        tv_comment.setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
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
                Intent intent = new Intent(TxtActivity.this, CommentActivity.class);
                intent.putExtra("commentId", id);
                intent.putExtra("isComment", true);
                startActivityForResult(intent, 1000);
                break;
            case R.id.digg_btn:
                digg();
                break;
            case R.id.comment_btn:
                Intent intent1 = new Intent(TxtActivity.this, CommentActivity.class);
                intent1.putExtra("commentId", id);
                startActivity(intent1);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_share:
                UIUtils.showShare(this, null, true, tv_title.getText().toString(), null, tv_content.getText().toString(), null);
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
