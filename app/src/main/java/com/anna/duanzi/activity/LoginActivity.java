package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.fragment.BackHandledFragment;
import com.anna.duanzi.fragment.BackHandledInterface;
import com.anna.duanzi.fragment.DefaultLoginFragment;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.sns.SNS;
import com.avos.sns.SNSBase;
import com.avos.sns.SNSCallback;
import com.avos.sns.SNSException;
import com.avos.sns.SNSType;

public class LoginActivity extends BaseActivity implements View.OnClickListener, BackHandledInterface {

    private ImageButton ib_back, ib_close_login, ib_qq_login, ib_wx_login, ib_wb_login;
    private BackHandledFragment mBackHandedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        ib_qq_login = (ImageButton) findViewById(R.id.ib_qq_login);
        ib_wx_login = (ImageButton) findViewById(R.id.ib_wx_login);
        ib_wb_login = (ImageButton) findViewById(R.id.ib_wb_login);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_close_login = (ImageButton) findViewById(R.id.ib_close_login);
        ib_back.setOnClickListener(this);
        ib_close_login.setOnClickListener(this);
        ib_qq_login.setOnClickListener(this);
        ib_wx_login.setOnClickListener(this);
        ib_wb_login.setOnClickListener(this);
        DefaultLoginFragment defaultFragment = new DefaultLoginFragment();
        loadFragment(defaultFragment);
    }

    public void loadFragment(BackHandledFragment fragment) {
        BackHandledFragment backHandledFragment = fragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, backHandledFragment, "default");
        ft.addToBackStack("tag");
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                onBackPressed();
                break;
            case R.id.ib_close_login:
                finish();
                break;
            case R.id.ib_qq_login:
                //authQQLogin();
                Toast.makeText(this, "QQ登录暂时没有开放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_wx_login:
                Toast.makeText(this, "微信登录暂时没有开放", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ib_wb_login:
                Toast.makeText(this, "微博登录暂时没有开放", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    //QQ 授权登录
    private void authQQLogin() {
        try {
            SNS.setupPlatform(SNSType.AVOSCloudSNSQQ, "https://leancloud.cn/1.1/sns/goto/f9chu72m16txryrm");
            SNS.loginWithCallback(this, SNSType.AVOSCloudSNSQQ, new SNSCallback() {
                @Override
                public void done(SNSBase base, SNSException e) {
                    if (e == null) {
                        SNS.loginWithAuthData(base.userInfo(), new LogInCallback<AVUser>() {
                            @Override
                            public void done(final AVUser user,
                                             AVException e) {
                                if (e == null) {
                                    Toast.makeText(LoginActivity.this, "QQ授权登录成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        } catch (AVException e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SNS.onActivityResult(requestCode, resultCode, data, SNSType.AVOSCloudSNSQQ);
    }

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    finish();
                }
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }
}
