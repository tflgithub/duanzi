package com.anna.duanzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.service.RegisterCodeTimer;
import com.anna.duanzi.service.RegisterCodeTimerService;
import com.anna.duanzi.utils.NetUtils;
import com.anna.duanzi.utils.StringUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DefaultLoginFragment extends BackHandledFragment {

    @Bind(R.id.tv_phone_number_error)
    TextView tv_phone_number_error;
    @Bind(R.id.tv_vail_code_error)
    TextView tv_vail_code_error;
    @Bind(R.id.et_phone_number)
    EditText et_phone_number;
    @Bind(R.id.et_vail_code)
    EditText et_vail_code;
    @Bind(R.id.tv_send_vail_code)
    TextView tv_send_vail_code;
    private static final String TAG = "DefaultLoginFragment";
    private Intent mIntent;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RegisterCodeTimerService.setHandler(mCodeHandler);
        mIntent = new Intent(getActivity(), RegisterCodeTimerService.class);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_default_login, null);
        (getActivity().findViewById(R.id.ib_back)).setVisibility(View.INVISIBLE);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick({R.id.tv_send_vail_code, R.id.tv_account_login, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (NetUtils.getNetType(getActivity()) == NetUtils.UNCONNECTED) {
                    Toast.makeText(getActivity(), "无网络，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                vailCode();
                break;
            case R.id.tv_account_login:
                toAccountLogin();
                break;
            case R.id.tv_send_vail_code:
                sendVailCode();
                break;
        }
    }

    private void sendVailCode() {
        if (et_phone_number.getText().toString().isEmpty() || !StringUtils.isMobileNumberValid(et_phone_number.getText().toString())) {
            tv_phone_number_error.setVisibility(View.VISIBLE);
            tv_vail_code_error.setVisibility(View.INVISIBLE);
            et_phone_number.setFocusable(true);
            return;
        }
        tv_send_vail_code.setEnabled(false);
        getActivity().startService(mIntent);
        AVUser user = new AVUser();
        user.setUsername(et_phone_number.getText().toString());
        // 其他属性可以像其他AVObject对象一样使用put方法添加
        user.put("mobilePhoneNumber", et_phone_number.getText().toString());
        mSVProgressHUD.showWithProgress("请稍后...", SVProgressHUD.SVProgressHUDMaskType.Black);
        user.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // successfully
                    mSVProgressHUD.dismiss();
                    Toast.makeText(getActivity(), "恭喜注册成功", Toast.LENGTH_SHORT).show();
                } else {
                    // failed
                    mSVProgressHUD.showErrorWithStatus("服务器忙，请稍后重试！");
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private void toAccountLogin() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        //设置替换和退栈的动画
        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in, R.anim.back_left_in, R.anim.back_right_out);
        AccountLoginFragment accountFragment = (AccountLoginFragment) manager.findFragmentByTag("account");
        if (accountFragment == null) {
            accountFragment = new AccountLoginFragment();
        }
        ft.replace(R.id.fl_container, accountFragment, "account");
        ft.addToBackStack("tag");
        ft.commit();
    }

    private void vailCode() {
        if (et_phone_number.getText().toString().isEmpty()) {
            tv_phone_number_error.setVisibility(View.VISIBLE);
            tv_vail_code_error.setVisibility(View.INVISIBLE);
            et_phone_number.setFocusable(true);
            return;
        } else if (et_vail_code.getText().toString().isEmpty()) {
            tv_vail_code_error.setVisibility(View.VISIBLE);
            tv_phone_number_error.setVisibility(View.INVISIBLE);
            et_vail_code.setFocusable(true);
            return;
        }
        AVUser.verifyMobilePhoneInBackground(et_vail_code.getText().toString(), new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failure", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }


    /**
     * 倒计时Handler
     */

    Handler mCodeHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == RegisterCodeTimer.IN_RUNNING) {// 正在倒计时
                tv_send_vail_code.setText(msg.obj.toString());
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                tv_send_vail_code.setEnabled(true);
                tv_send_vail_code.setText(msg.obj.toString());
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().stopService(mIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
