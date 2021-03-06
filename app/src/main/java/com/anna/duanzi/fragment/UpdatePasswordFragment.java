package com.anna.duanzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.service.RegisterCodeTimer;
import com.anna.duanzi.service.RegisterCodeTimerService;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatePasswordFragment extends BackHandledFragment {

    @Bind(R.id.tv_vail_code_error)
    TextView tv_vail_code_error;
    @Bind(R.id.tv_password_error)
    TextView tv_password_error;
    @Bind(R.id.et_vail_code)
    EditText et_vail_code;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.tv_send_vail_code)
    TextView tv_send_vail_code;
    private Intent mIntent;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_password, null);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public View initView() {
        return null;
    }

    @Override
    public void initData() {

    }

    private void init() {
        RegisterCodeTimerService.setHandler(mCodeHandler);
        mIntent = new Intent(getActivity(), RegisterCodeTimerService.class);
        String phoneNumber = getArguments().getString("phoneNumber");
        AVUser.requestPasswordResetBySmsCodeInBackground(phoneNumber, new RequestMobileCodeCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    tv_send_vail_code.setEnabled(false);
                    getActivity().startService(mIntent);
                }
            }
        });
    }


    @OnClick({R.id.tv_send_vail_code, R.id.btn_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                vailCodeAndPassword();
                break;
            case R.id.tv_send_vail_code:
                sendVailCode();
                break;
        }
    }

    private void sendVailCode() {
        tv_send_vail_code.setEnabled(false);
        getActivity().startService(mIntent);
    }

    private void vailCodeAndPassword() {
        if (et_vail_code.getText().toString().isEmpty()) {
            tv_vail_code_error.setVisibility(View.VISIBLE);
            tv_password_error.setVisibility(View.INVISIBLE);
            et_vail_code.setFocusable(true);
            return;
        } else if (et_password.getText().toString().isEmpty()) {
            tv_password_error.setVisibility(View.VISIBLE);
            tv_vail_code_error.setVisibility(View.INVISIBLE);
            et_password.setFocusable(true);
            return;
        }
        mSVProgressHUD.showWithProgress("提交中...", SVProgressHUD.SVProgressHUDMaskType.Black);
        AVUser.resetPasswordBySmsCodeInBackground(et_vail_code.getText().toString(), et_password.getText().toString(), new UpdatePasswordCallback() {
            @Override
            public void done(AVException e) {
                mSVProgressHUD.dismiss();
                if (e == null) {
                    Toast.makeText(getActivity(), "密码重置成功", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "密码重置失败", Toast.LENGTH_SHORT).show();
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
