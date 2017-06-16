package com.anna.duanzi.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.LoginActivity;
import com.anna.duanzi.activity.SettingActivity;
import com.anna.duanzi.activity.UserInfoActivity;
import com.anna.duanzi.utils.UIUtils;
import com.anna.duanzi.widget.CircleImageView;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.bumptech.glide.Glide;
import com.cn.tfl.update.UpdateChecker;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tfl on 2016/1/22.
 */
public class WdFragment extends BaseFragment {

    private FeedbackAgent agent;
    @Bind(R.id.tv_login_click)
    TextView tv_login;
    @Bind(R.id.iv_user_head)
    CircleImageView iv_user_head;
    private Intent mIntent;
    @Bind(R.id.ly_setting)
    LinearLayout ly_setting;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_wd, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        agent = new FeedbackAgent(getActivity());
        agent.sync();
        mIntent = new Intent();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (AVUser.getCurrentUser() == null) {
            tv_login.setText("点击登录");
            ly_setting.setVisibility(View.GONE);
            iv_user_head.setImageResource(R.drawable.default_round_head);
        } else {
            ly_setting.setVisibility(View.VISIBLE);
            tv_login.setText(AVUser.getCurrentUser().getString("nickName") == null ? AVUser.getCurrentUser().getUsername() : AVUser.getCurrentUser().getUsername());
            Glide.with(getActivity()).load(AVUser.getCurrentUser().getString("headImage")).placeholder(R.drawable.default_round_head).into(iv_user_head);
        }
    }

    @OnClick({R.id.ly_user, R.id.ly_setting, R.id.ly_version_update, R.id.tv_feedback, R.id.tv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ly_user:
                if (AVUser.getCurrentUser() != null) {
                    mIntent.setClass(getActivity(), UserInfoActivity.class);
                } else {
                    mIntent.setClass(getActivity(), LoginActivity.class);
                }
                startActivity(mIntent);
                break;
            case R.id.ly_setting:
                mIntent.setClass(getActivity(), SettingActivity.class);
                startActivity(mIntent);
                break;
            case R.id.ly_version_update:
                UpdateChecker.checkForDialog(getActivity());
                break;
            case R.id.tv_feedback:
                agent.startDefaultThreadActivity();
                break;
            case R.id.tv_share:
                SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("version", Context.MODE_PRIVATE);
                UIUtils.showShare(getActivity(), null, true, "应用下载地址", null, mSharedPreferences.getString("apkUrl", ""), null);
                break;
        }
    }
}
