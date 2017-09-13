package com.anna.duanzi.fragment;

import android.content.Intent;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.HomeActivity;
import com.anna.duanzi.activity.SearchActivity;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.widget.CircleImageView;
import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentHome extends BaseFragment implements TabHost.OnTabChangeListener {

    @Bind(android.R.id.tabhost)
    public FragmentTabHost mTabHost;
    @Bind(android.R.id.tabs)
    TabWidget tabWidget;
    @Bind(R.id.iv_user_head)
    CircleImageView iv_user_head;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_message, null);
        ButterKnife.bind(this, view);
        initTabHost();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AVUser.getCurrentUser() != null) {
            Glide.with(this).load(AVUser.getCurrentUser().getString("headImage")).placeholder(R.drawable.default_round_head).into(iv_user_head);
        }
    }

    private void initTabHost() {
        tabWidget.setDividerDrawable(null);
        mTabHost.setup(getActivity(), getChildFragmentManager(),
                android.R.id.tabcontent);
        String tabs[] = TabDb.getTabsTxt();
        for (int i = 0; i < tabs.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabs[i]).setIndicator(getTabView(i));
            mTabHost.addTab(tabSpec, TabDb.getFragments()[i], null);
            mTabHost.setTag(i);
        }
        mTabHost.setOnTabChangedListener(this);
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.iv_user_head, R.id.tv_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_user_head:
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_search:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                break;
        }
    }

    private View getTabView(int idx) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.tab_item, null);
        ((TextView) view.findViewById(R.id.tv_title)).setText(TabDb.getTabsTxt()[idx]);
        if (idx == 0) {
            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.colorPrimary));
            (view.findViewById(R.id.view_bottom)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        updateTab();
    }

    private void updateTab() {
        TabWidget tabw = mTabHost.getTabWidget();
        for (int i = 0; i < tabw.getChildCount(); i++) {
            View view = tabw.getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.colorPrimary));
                (view.findViewById(R.id.view_bottom)).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.gray));
                (view.findViewById(R.id.view_bottom)).setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }
    }

    static class TabDb {
        public static String[] getTabsTxt() {
            String[] tabs = {"段子", "小说"};
            return tabs;
        }

        public static Class[] getFragments() {
            Class[] clz = {TxtFragment.class, FictionFragment.class};
            return clz;
        }
    }
}
