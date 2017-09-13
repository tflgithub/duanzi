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

import com.anna.duanzi.R;
import com.anna.duanzi.utils.StringUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FoundPasswordFragment extends BackHandledFragment {

    @Bind(R.id.tv_phone_number_error)
    TextView tv_phone_number_error;
    @Bind(R.id.et_phone_number)
    EditText et_phone_number;

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
        View view = View.inflate(getActivity(), R.layout.fragment_found_password, null);
        ButterKnife.bind(this, view);
        String phoneNumber = getArguments().getString("phoneNumber");
        et_phone_number.setText(phoneNumber);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                if (isShow) {
                    return;
                }
                if (isSuccess) {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    //设置替换和退栈的动画
                    ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in, R.anim.back_left_in, R.anim.back_right_out);
                    UpdatePasswordFragment updatePasswordFragment = (UpdatePasswordFragment) manager.findFragmentByTag("updatePassword");
                    if (updatePasswordFragment == null) {
                        updatePasswordFragment = new UpdatePasswordFragment();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("phoneNumber", et_phone_number.getText().toString());
                    updatePasswordFragment.setArguments(bundle);
                    ft.replace(R.id.fl_container, updatePasswordFragment, "updatePassword");
                    ft.addToBackStack("tag");
                    ft.commit();
                } else {
                    hud.showErrorWithStatus("手机号未注册");
                    isShow = true;
                }
            }
        });
        return view;
    }

    @Override
    public View initView() {
        return null;
    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.btn_next_step})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_step:
                toUpdatePassword();
                break;
        }
    }

    private void toUpdatePassword() {
        if (et_phone_number.getText().toString().isEmpty() || !StringUtils.isMobileNumberValid(et_phone_number.getText().toString())) {
            tv_phone_number_error.setVisibility(View.VISIBLE);
            et_phone_number.setFocusable(true);
            return;
        }
        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        userQuery.whereEqualTo("username", et_phone_number.getText().toString());
        mSVProgressHUD.showWithProgress("请稍后...", SVProgressHUD.SVProgressHUDMaskType.Black);
        isShow = false;
        userQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                mSVProgressHUD.dismiss();
                if (e == null) {
                    if (list.size() > 0) {
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                    }
                } else {
                    isSuccess = false;
                    e.printStackTrace();
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
