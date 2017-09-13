package com.anna.duanzi.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.utils.NetUtils;
import com.anna.duanzi.utils.StringUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountLoginFragment extends BackHandledFragment {

    @Bind(R.id.tv_phone_number_error)
    TextView tv_phone_number_error;
    @Bind(R.id.tv_password_error)
    TextView tv_password_error;
    @Bind(R.id.et_phone_number)
    EditText et_phone_number;
    @Bind(R.id.et_password)
    EditText et_password;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                if (isShow) {
                    return;
                }
                if (isSuccess) {
                    getActivity().finish();
                } else {
                    hud.showErrorWithStatus("手机号不正或者确密码错误");
                }
                isShow = true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_login, null);
        (getActivity().findViewById(R.id.ib_back)).setVisibility(View.VISIBLE);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public View initView() {
        return null;
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.tv_find_password, R.id.tv_vail_code_login, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (NetUtils.getNetType(getActivity()) == NetUtils.UNCONNECTED) {
                    Toast.makeText(getActivity(), "无网络，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                vailPhoneAndCode();
                break;
            case R.id.tv_vail_code_login:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.tv_find_password:
                toFoundPassword();
                break;
        }
    }

    private void toFoundPassword() {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        //设置替换和退栈的动画
        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in, R.anim.back_left_in, R.anim.back_right_out);
        FoundPasswordFragment accountFragment = (FoundPasswordFragment) manager.findFragmentByTag("foundPassword");
        if (accountFragment == null) {
            accountFragment = new FoundPasswordFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("phoneNumber", et_phone_number.getText().toString());
        accountFragment.setArguments(bundle);
        ft.replace(R.id.fl_container, accountFragment, "foundPassword");
        ft.addToBackStack("tag");
        ft.commit();
    }

    private void vailPhoneAndCode() {
        if (et_phone_number.getText().toString().isEmpty() || !StringUtils.isMobileNumberValid(et_phone_number.getText().toString())) {
            tv_phone_number_error.setVisibility(View.VISIBLE);
            tv_password_error.setVisibility(View.INVISIBLE);
            et_phone_number.setFocusable(true);
            return;
        } else if (et_password.getText().toString().isEmpty()) {
            tv_password_error.setVisibility(View.VISIBLE);
            tv_phone_number_error.setVisibility(View.INVISIBLE);
            et_password.setFocusable(true);
            return;
        }
        isShow = false;
        mSVProgressHUD.showWithProgress("登录中...", SVProgressHUD.SVProgressHUDMaskType.Black);
        AVUser.logInInBackground(et_phone_number.getText().toString(), et_password.getText().toString(), new LogInCallback<AVUser>() {
            @Override
            public void done(AVUser avUser, AVException e) {
                mSVProgressHUD.dismiss();
                if (e == null) {
                    isSuccess = true;
                } else {
                    isSuccess = false;
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
