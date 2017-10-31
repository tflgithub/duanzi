package com.anna.duanzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DefaultLoginFragment extends BackHandledFragment {

    @Bind(R.id.et_phone_number)
    EditText et_phone_number;
    @Bind(R.id.et_password)
    EditText et_password;
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

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_default_login, null);
        (getActivity().findViewById(R.id.ib_back)).setVisibility(View.INVISIBLE);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.tv_send_vail_code, R.id.btn_register, R.id.btn_login})
    public void onClick(View view) {
        if (NetUtils.getNetType(getActivity()) == NetUtils.UNCONNECTED) {
            Toast.makeText(getActivity(), "无网络，请检查您的网络连接", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.btn_register:
                register();
                break;
            case R.id.btn_login:
                toAccountLogin();
                break;
            case R.id.tv_send_vail_code:
                sendVailCode();
                break;
        }
    }

    private void sendVailCode() {
        if (et_phone_number.getText().toString().isEmpty() || !StringUtils.isMobileNumberValid(et_phone_number.getText().toString())) {
            showToast("手机号码无效");
            et_phone_number.setFocusable(true);
            return;
        }
        if (et_password.getText().toString().length() < 6) {
            showToast("密码不少于6个字符");
            et_password.setFocusable(true);
            return;
        }
        AVUser user = new AVUser();
        user.setUsername(et_phone_number.getText().toString());
        // 其他属性可以像其他AVObject对象一样使用put方法添加
        user.put("mobilePhoneNumber", et_phone_number.getText().toString());
        user.put("password", et_password.getText().toString());
        user.signUpInBackground(new SignUpCallback() {
            public void done(AVException e) {
                if (e == null) {
                    tv_send_vail_code.setEnabled(false);
                    getActivity().startService(mIntent);
                } else {
                    // {"code":214,"error":"Mobile phone number has already been taken."}
                    // {"code":201,"error":"Password is missing or empty."}
                    try {
                        Log.e(TAG, e.getMessage());
                        JSONObject jsonObject = new JSONObject(e.getMessage());
                        int code = jsonObject.optInt("code");
                        switch (code) {
                            case 214:
                                Toast.makeText(getActivity(), "手机号码被注册！", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getActivity(), "请稍后再试！", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
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
            Bundle bundle = new Bundle();
            bundle.putString("username", et_phone_number.getText().toString());
            bundle.putString("password", et_password.getText().toString());
            accountFragment.setArguments(bundle);
        }
        ft.replace(R.id.fl_container, accountFragment, "account");
        ft.addToBackStack("tag");
        ft.commit();
    }

    private void register() {
        if (et_phone_number.getText().toString().isEmpty()) {
            showToast("手机号码无效");
            et_phone_number.setFocusable(true);
            return;
        } else if (et_vail_code.getText().toString().isEmpty()) {
            showToast("请填写验证码");
            et_vail_code.setFocusable(true);
            return;
        }
        AVUser.verifyMobilePhoneInBackground(et_vail_code.getText().toString(), new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "恭喜注册成功!", Toast.LENGTH_LONG).show();
                    toAccountLogin();
                } else {
                    Toast.makeText(getActivity(), "验证码无效！", Toast.LENGTH_SHORT).show();
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
