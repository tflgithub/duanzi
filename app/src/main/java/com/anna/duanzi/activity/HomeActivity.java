package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.domain.Version;
import com.anna.duanzi.utils.UIUtils;
import com.anna.duanzi.utils.VersionPreferences;
import com.anna.duanzi.widget.BadgeView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.bumptech.glide.Glide;
import com.cn.tfl.update.AppUtils;
import com.cn.tfl.update.UpdateChecker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class HomeActivity extends BaseActivity {

    private FeedbackAgent agent;
    @Bind(R.id.iv_user_head)
    ImageView iv_user_head;
    private Intent mIntent;
    @Bind(R.id.ly_setting)
    LinearLayout ly_setting;
    @Bind(R.id.tv_update)
    TextView tv_update;
    String apk_url, upgrade_desc;
    int version_code;
    @Bind(R.id.tv_nick_name)
    TextView tv_nick_name;
    @Bind(R.id.tv_followee)
    TextView tv_followee;
    @Bind(R.id.tv_follower)
    TextView tv_follower;
    @Bind(R.id.uc_zoomiv)
    ImageView uc_zoomiv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        agent = new FeedbackAgent(this);
        agent.sync();
        mIntent = new Intent();
        AVQuery<Version> query = AVObject.getQuery(Version.class);
        query.getFirstInBackground(new GetCallback<Version>() {
            @Override
            public void done(Version version, AVException e) {
                if (e == null) {
                    AVFile apkFile = version.getAVFile("apkFile");
                    upgrade_desc = version.upgradeDesc;
                    version_code = version.versionCode;
                    apk_url = apkFile.getUrl();
                    VersionPreferences.getInstance().setApkUrl(apkFile.getUrl());
                    if (version_code > AppUtils.getVersionCode(HomeActivity.this)) {
                        BadgeView badgeView = new BadgeView(HomeActivity.this, tv_update);
                        badgeView.setText("new");
                        badgeView.setTextSize(10);
                        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                        badgeView.setAlpha(1f);
                        badgeView.setBadgeMargin(0, 35);
                        badgeView.show();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLogin) {
            tv_nick_name.setText("点击头像登录");
            iv_user_head.setImageResource(R.drawable.default_round_head);
            tv_followee.setVisibility(View.GONE);
            tv_follower.setVisibility(View.GONE);
            ly_setting.setVisibility(View.GONE);
        } else {
            currentUser = AVUser.getCurrentUser();
            if (currentUser != null) {
                tv_nick_name.setText(currentUser.getString("nickName") == null ? AVUser.getCurrentUser().getUsername() : currentUser.getString("nickName"));
                ly_setting.setVisibility(View.VISIBLE);
                countFolloweeAndFollower();
                //Glide.with(mContext).load(currentUser.getString("headImage")).placeholder(R.drawable.hugh).into(uc_zoomiv);
                Glide.with(mContext)
                        .load(currentUser.getString("headImage"))
                                .crossFade()
                                .centerCrop()
                                .bitmapTransform(new CropCircleTransformation(mContext))
                                .placeholder(R.drawable.default_round_head)
                                .into(iv_user_head);
            }
        }
    }

    //统计关注和粉丝
    private void countFolloweeAndFollower() {
        tv_followee.setVisibility(View.VISIBLE);
        tv_follower.setVisibility(View.VISIBLE);
        AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
        followeeQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                tv_followee.setText("关注：" + i);
            }
        });
        AVQuery<AVUser> followerQuery = AVUser.followerQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
        followerQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                tv_follower.setText("粉丝：" + i);
            }
        });
    }

    @OnClick({R.id.header_actionbar_back, R.id.iv_user_head, R.id.ly_setting, R.id.ly_version_update, R.id.tv_feedback, R.id.tv_share, R.id.tv_follower, R.id.tv_followee})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_actionbar_back:
                finish();
                break;
            case R.id.iv_user_head:
                if (currentUser != null && isLogin) {
                    mIntent.setClass(this, UserInfoActivity.class);
                } else {
                    mIntent.setClass(this, LoginActivity.class);
                }
                startActivity(mIntent);
                break;
            case R.id.ly_setting:
                mIntent.setClass(this, SettingActivity.class);
                startActivity(mIntent);
                break;
            case R.id.ly_version_update:
                UpdateChecker.checkForDialog(this, apk_url, upgrade_desc, version_code);
                break;
            case R.id.tv_feedback:
                agent.startDefaultThreadActivity();
                break;
            case R.id.tv_share:
                UIUtils.shareTxt(this, "应用下载地址:", VersionPreferences.getInstance().getApkUrl());
                break;
            case R.id.tv_followee:
                mIntent.setClass(this, AttentionPersonActivity.class);
                mIntent.putExtra("type", AttentionPersonActivity.tabTitle[0]);
                startActivity(mIntent);
                break;
            case R.id.tv_follower:
                mIntent.setClass(this, AttentionPersonActivity.class);
                mIntent.putExtra("type", AttentionPersonActivity.tabTitle[1]);
                startActivity(mIntent);
                break;
        }
    }
}
