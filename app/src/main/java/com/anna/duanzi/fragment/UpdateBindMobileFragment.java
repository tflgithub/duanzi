package com.anna.duanzi.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.service.RegisterCodeTimer;
import com.anna.duanzi.service.RegisterCodeTimerService;
import com.anna.duanzi.utils.StringUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateBindMobileFragment extends BackHandledFragment {

    @Bind(R.id.tv_vail_code_error)
    TextView tv_vail_code_error;
    @Bind(R.id.et_vail_code)
    EditText et_vail_code;
    @Bind(R.id.tv_send_vail_code)
    TextView tv_send_vail_code;
    private Intent mIntent;
    String phoneNumber;
    @Bind(R.id.tv_send_tip)
    TextView tv_tip;

    @Override
    public boolean onBackPressed() {
        return false;
    }


    @Override
    public View initView() {
        View view = View.inflate(getActivity(),R.layout.fragment_update_bind_mobile, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        RegisterCodeTimerService.setHandler(mCodeHandler);
        mIntent = new Intent(getActivity(), RegisterCodeTimerService.class);
        phoneNumber = getArguments().getString("phoneNumber");
        tv_tip.setText("已经向手机号"+StringUtils.getMobile(phoneNumber)+"发送了验证码");
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
            et_vail_code.setFocusable(true);
            return;
        }
        mSVProgressHUD.showWithProgress("提交中...", SVProgressHUD.SVProgressHUDMaskType.Black);
        AVUser.verifyMobilePhoneInBackground(et_vail_code.getText().toString(), new AVMobilePhoneVerifyCallback() {
            @Override
            public void done(AVException e) {
                mSVProgressHUD.dismiss();
                if (e == null) {
                    AVUser.getCurrentUser().setMobilePhoneNumber(phoneNumber);
                    AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            Toast.makeText(getActivity(), "更换手机号成功", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    });
                } else {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "更换手机号失败", Toast.LENGTH_SHORT).show();
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
