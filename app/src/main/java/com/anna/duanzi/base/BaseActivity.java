package com.anna.duanzi.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.anna.duanzi.common.Constants;
import com.anna.duanzi.utils.LoginPreferences;
import com.anna.duanzi.utils.NetUtils;
import com.avos.avoscloud.AVUser;
import com.bigkoo.svprogresshud.SVProgressHUD;

import fm.jiecao.jcvideoplayer_lib.JCMediaManager;

/**
 * Created by tfl on 2016/11/7.
 */
public class BaseActivity extends FragmentActivity {

    protected AVUser currentUser;
    protected int data_skip = 0;//忽略前多少个
    protected int data_limit = 20;//最多加载多少条记录。
    protected Context mContext;
    protected SVProgressHUD mSVProgressHUD;
    protected boolean isLogin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSVProgressHUD = new SVProgressHUD(this);
        currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getString("vip").equals(Constants.MEMBER.MEMBER_LEVEL_0)) {
                JCMediaManager.intance().isVip = false;
                JCMediaManager.intance().NO_VIP_FREE_TIME = Constants.MEMBER.NO_VIP_FREE_TIME;
            }
        }
        mContext = this;
    }

    /**
     * 检查网络
     *
     * @param context
     * @return
     */
    protected boolean checkNetWork(Context context) {
        if (NetUtils.getNetType(context) == NetUtils.UNCONNECTED) {
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        if (!checkNetWork(this)) {
            Toast.makeText(this, "无网络，请检查您的网络连接", Toast.LENGTH_SHORT).show();
        }
        isLogin = LoginPreferences.getInstance().getLoginStatus();
        super.onResume();
    }

    protected void showShortToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
