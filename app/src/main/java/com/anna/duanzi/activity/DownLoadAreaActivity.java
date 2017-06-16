package com.anna.duanzi.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.fragment.FragmentMyDownLoadMovies;
import com.anna.duanzi.fragment.FragmentUnDownLoadMovies;

public class DownLoadAreaActivity extends BaseActivity implements TabHost.OnTabChangeListener {

    private FragmentTabHost mTabHost;
    private TabWidget tabWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_area);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText(getString(R.string.down_load_area));
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabWidget = (TabWidget) findViewById(android.R.id.tabs);
        initTabHost();
    }


    private void initTabHost() {
        tabWidget.setDividerDrawable(null);
        mTabHost.setup(this, getSupportFragmentManager(),
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
    public void onTabChanged(String tabId) {
        updateTab();
    }


    private void updateTab() {
        TabWidget tabw = mTabHost.getTabWidget();
        for (int i = 0; i < tabw.getChildCount(); i++) {
            View view = tabw.getChildAt(i);
            if (i == mTabHost.getCurrentTab()) {
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.orange));
                (view.findViewById(R.id.view_bottom)).setBackgroundColor(getResources().getColor(R.color.orange));
            } else {
                ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.black));
                (view.findViewById(R.id.view_bottom)).setBackgroundColor(getResources().getColor(R.color.gray));
            }
        }
    }

    private View getTabView(int idx) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        ((TextView) view.findViewById(R.id.tv_title)).setText(TabDb.getTabsTxt()[idx]);
        if (idx == 0) {
            ((TextView) view.findViewById(R.id.tv_title)).setTextColor(getResources().getColor(R.color.orange));
            (view.findViewById(R.id.view_bottom)).setBackgroundColor(getResources().getColor(R.color.orange));
        }
        return view;
    }

    static class TabDb {
        public static String[] getTabsTxt() {
            String[] tabs = {"精品下载", "我的下载"};
            return tabs;
        }

        public static Class[] getFragments() {
            Class[] clz = {FragmentUnDownLoadMovies.class, FragmentMyDownLoadMovies.class};
            return clz;
        }
    }

    public void onClick(View view) {
        finish();
    }
}
