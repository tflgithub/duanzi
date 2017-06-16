package com.anna.duanzi.fragment;

import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.anna.duanzi.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentHome extends BaseFragment implements TabHost.OnTabChangeListener {

    @Bind(android.R.id.tabhost)
    public FragmentTabHost mTabHost;
    @Bind(android.R.id.tabs)
    TabWidget tabWidget;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_message, null);
        ButterKnife.bind(this, view);
        initTabHost();
        return view;
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


    private View getTabView(int idx) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.tab_item, null);
        ((TextView) view.findViewById(R.id.tv_title)).setText(TabDb.getTabsTxt()[idx]);
        if (idx == 0) {
            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.green));
            (view.findViewById(R.id.view_bottom)).setBackgroundColor(getResources().getColor(R.color.green));
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
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.green));
                (view.findViewById(R.id.view_bottom)).setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.black));
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
