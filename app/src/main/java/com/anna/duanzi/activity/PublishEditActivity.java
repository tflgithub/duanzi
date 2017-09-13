package com.anna.duanzi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.fragment.PublishImageFragment;
import com.anna.duanzi.utils.UIUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishEditActivity extends BaseActivity implements UIUtils.DialogListener {

    @Bind(R.id.tv_publish)
    TextView tv_publish;
    @Bind(R.id.tv_cancel)
    TextView tv_cancel;
    public ArrayList<String> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_edit);
        ButterKnife.bind(this);
        imageList = getIntent().getStringArrayListExtra("imageList");
        loadFragment(PublishImageFragment.newInstance(imageList));
    }

    public void loadFragment(BaseFragment fragment) {
        BaseFragment baseFragment = fragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, baseFragment, "default");
        ft.commit();
    }

    @OnClick({R.id.tv_publish, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_publish:
                break;
            case R.id.tv_cancel:
                quit();
                break;
        }
    }

    private void quit() {
        UIUtils quitUtils = new UIUtils(this);
        quitUtils.setDialogListener(this);
        quitUtils.createConfirmDialog("确定退出?", "",
                "退出", "取消", UIUtils.DialogListener.LOGOUT);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                quit();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void disBack(int action) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
