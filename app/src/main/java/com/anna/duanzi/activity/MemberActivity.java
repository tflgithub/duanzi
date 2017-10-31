package com.anna.duanzi.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.Constants;
import com.anna.duanzi.domain.ShareLog;
import com.anna.duanzi.utils.VersionPreferences;
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
import me.drakeet.materialdialog.MaterialDialog;

public class MemberActivity extends BaseActivity {

    private String grade = Constants.MEMBER.MEMBER_LEVEL_1;//默认一级会员
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        initView();
    }

    private void initView() {
        String vipLevel = getIntent().getStringExtra("vipLevel");
        ((TextView) findViewById(R.id.tv_current_user)).setText(AVUser.getCurrentUser().getString("nickName") + "，欢迎您，你当前等级为" + vipLevel);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("升级会员");
        button = (Button) findViewById(R.id.btn_ok);
        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.member_grade_rg);
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_grade_one) {
                    grade = Constants.MEMBER.MEMBER_LEVEL_1;
                    button.setText("分享给朋友");
                } else if (checkedId == R.id.rb_grade_two) {
                    grade = Constants.MEMBER.MEMBER_LEVEL_2;
                    button.setText("立即激活");
                } else if (checkedId == R.id.rb_grade_three) {
                    grade = Constants.MEMBER.MEMBER_LEVEL_3;
                    button.setText("立即激活");
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
                        share.setText(VersionPreferences.getInstance().getApkUrl());
                        share.show(this);
                        break;
                    case Constants.MEMBER.MEMBER_LEVEL_2:
                    case Constants.MEMBER.MEMBER_LEVEL_3:
                        final MaterialDialog materialDialog = new MaterialDialog(this);
                        materialDialog.setTitle("温馨提示")
                                .setMessage("请加微信号：tony_tyl,联系客服激活")
                                .setNegativeButton("我知道了",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                materialDialog.dismiss();
                                            }
                                        });
                        materialDialog.show();
                        break;
                }
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
