package com.anna.duanzi.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.Constants;
import com.anna.duanzi.domain.ShareLog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MemberActivity extends BaseActivity {

    private String grade = Constants.MEMBER.MEMBER_LEVEL_1;//默认一级会员

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.tv_current_user)).setText(AVUser.getCurrentUser().getString("nickName") + "，欢迎您，你当前等级为普通用户");
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("升级会员");
        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.member_grade_rg);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_grade_one) {
                    grade = Constants.MEMBER.MEMBER_LEVEL_1;
                } else if (checkedId == R.id.rb_grade_two) {
                    grade = Constants.MEMBER.MEMBER_LEVEL_2;
                } else if (checkedId == R.id.rb_grade_three) {
                    grade = Constants.MEMBER.MEMBER_LEVEL_3;
                }
            }
        });
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_actionbar_back:
                finish();
                break;
            case R.id.btn_ok:
                switch (grade) {
                    case Constants.MEMBER.MEMBER_LEVEL_1:
                        break;
                    case Constants.MEMBER.MEMBER_LEVEL_2:
                        break;
                    case Constants.MEMBER.MEMBER_LEVEL_3:
                        break;
                }
                SharedPreferences mSharedPreferences = getSharedPreferences("version", Context.MODE_PRIVATE);
                OnekeyShare share = new OnekeyShare();
                share.setCallback(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        recordShare(platform.getName());
                        Log.d("MemberActivity", "分享回调");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                share.disableSSOWhenAuthorize();
                share.setText(mSharedPreferences.getString("apkUrl", ""));
                share.show(this);
                break;
        }
    }

    /**
     * 分享记录
     *
     * @param platFormName
     */
    private void recordShare(String platFormName) {
        ShareLog shareLog = new ShareLog();
        shareLog.put("username", AVUser.getCurrentUser().getUsername());// 设置名称
        shareLog.put("sharePlatform", platFormName);
        shareLog.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null && AVUser.getCurrentUser().getString("vip").equals(Constants.MEMBER.MEMBER_LEVEL_0)) {
                    countShare();
                }
            }
        });// 保存到服务端
    }

    private void countShare() {
        AVQuery<AVObject> shareLogAVQuery = new AVQuery<>("ShareLog");
        shareLogAVQuery.whereEqualTo("username", AVUser.getCurrentUser().getUsername());
        shareLogAVQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null && i >= Constants.SHARE_COUNT_NUM_TEMP) {
                    Toast.makeText(MemberActivity.this, "您分享已达到10次，已升级为1级会员,感谢您的支持！", Toast.LENGTH_SHORT).show();
                    AVUser.getCurrentUser().put("vip", Constants.MEMBER.MEMBER_LEVEL_1);
                    AVUser.getCurrentUser().saveInBackground();
                }
            }
        });
    }
}
