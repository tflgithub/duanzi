package com.anna.duanzi.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.Contants;
import com.anna.duanzi.fragment.BackHandledFragment;
import com.anna.duanzi.fragment.BackHandledInterface;
import com.anna.duanzi.fragment.ModifyPasswordFragment;
import com.anna.duanzi.fragment.UpdateMobileFragment;
import com.anna.duanzi.fragment.UpdateNickFragment;

public class EditUserInfoActivity extends BaseActivity implements BackHandledInterface {
    private BackHandledFragment mBackHandedFragment;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        title = (TextView) findViewById(R.id.header_actionbar_title);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        int updateType = getIntent().getIntExtra(Contants.UPDATE_USERINFO_TYPE, 0);
        String updateContent = getIntent().getStringExtra(Contants.UPDATE_USERINFO_CONTENT);
        switch (updateType) {
            case Contants.UPDATE_USERINFO.EMAIL:
                title.setText("修改邮箱");
                ft.add(R.id.fl_container, UpdateNickFragment.newInstance(updateType, updateContent));
                break;
            case Contants.UPDATE_USERINFO.NICK_NAME:
                title.setText("修改昵称");
                ft.add(R.id.fl_container, UpdateNickFragment.newInstance(updateType, updateContent));
                break;
            case Contants.UPDATE_USERINFO.MOBILE:
                title.setText("更换手机");
                BackHandledFragment backHandledFragment = new UpdateMobileFragment();
                ft.replace(R.id.fl_container, backHandledFragment, "default");
                ft.addToBackStack("tag");
                break;
            case Contants.UPDATE_USERINFO.PASSWORD:
                title.setText("修改密码");
                ft.add(R.id.fl_container, ModifyPasswordFragment.newInstance(updateContent));
                break;
        }
        ft.commit();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_actionbar_back:
                onBackPressed();
                break;
        }
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
                else {
                    getSupportFragmentManager().popBackStack();
                }
            }
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }
}
