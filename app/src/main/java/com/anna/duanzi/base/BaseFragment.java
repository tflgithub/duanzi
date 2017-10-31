package com.anna.duanzi.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anna.duanzi.utils.LoginPreferences;
import com.bigkoo.svprogresshud.SVProgressHUD;

/**
 * Created by tfl on 2016/1/22.
 */
public abstract class BaseFragment extends Fragment {
    protected int data_skip = 0;//忽略前多少个
    protected int data_limit = 20;//最多加载多少条记录。
    protected String category;//分类
    protected SVProgressHUD mSVProgressHUD;
    protected boolean isLogin = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSVProgressHUD = new SVProgressHUD(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        initData();
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        isLogin = LoginPreferences.getInstance().getLoginStatus();
        super.onResume();
    }

    /**
     * 初始化界面
     *
     * @return
     */
    public abstract View initView();

    /**
     * 初始化数据
     */
    public abstract void initData();

    protected void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 重置忽略数据
     */
    public void resetDataSkip() {
        data_skip = 0;
    }


    @Override
    public void onDetach() {
        super.onDetach();
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
