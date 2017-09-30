package com.anna.duanzi.fragment;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.HomeActivity;
import com.anna.duanzi.activity.SearchActivity;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.widget.CircleImageView;
import com.avos.avoscloud.AVUser;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentHome extends BaseFragment {

    @Bind(R.id.tab_layout)
    public TabLayout tabLayout;
    @Bind(R.id.pager)
    ViewPager viewPager;
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
        tabLayout.addTab(tabLayout.newTab().setText("段子"));
        tabLayout.addTab(tabLayout.newTab().setText("小说"));
        fragments.add(new TxtFragment());
        fragments.add(new FictionFragment());
        PagerAdapter adapter = new PagerAdapter
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(0);
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

    List<Fragment> fragments = new ArrayList<>();

    class PagerAdapter extends FragmentStatePagerAdapter {

        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
