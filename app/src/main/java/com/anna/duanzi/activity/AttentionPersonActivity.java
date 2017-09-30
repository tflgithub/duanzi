package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.fragment.FolloweeFragment;
import com.anna.duanzi.fragment.FollowerFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AttentionPersonActivity extends BaseActivity {

    @Bind(R.id.id_viewpager)
    ViewPager mViewPager;
    @Bind(R.id.tab)
    TabLayout tabLayout;
    private TabAdapter tabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        ButterKnife.bind(this);
        initViewPager();
    }

    public static String tabTitle[] = new String[]{"关注", "粉丝"};


    private class TabAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }
    }


    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        final FolloweeFragment followeeFragment = new FolloweeFragment();
        final FollowerFragment followerFragment = new FollowerFragment();
        fragments.add(followeeFragment);
        fragments.add(followerFragment);
        tabAdapter = new TabAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(tabAdapter);
        //将TabLayout和ViewPager关联起来。
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        String type = getIntent().getStringExtra("type");
        if (type.equals(tabTitle[1])) {
            recomputeTlOffset1(1);
        }
    }


    @OnClick({R.id.btn_back, R.id.add_attention})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.add_attention:
                Intent intent = new Intent(this, AddAttentionActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 重新计算需要滚动的距离
     *
     * @param index 选择的tab的下标
     */
    private void recomputeTlOffset1(int index) {
        if (tabLayout.getTabAt(index) != null) tabLayout.getTabAt(index).select();
        final int width = (int) (getTabLayoutOffsetWidth(index) * getResources().getDisplayMetrics().density);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.smoothScrollTo(width, 0);
            }
        });
    }

    //重中之重是这个计算偏移量的方法，各位看官看好了。

    /**
     * 根据字符个数计算偏移量
     *
     * @param index 选中tab的下标
     * @return 需要移动的长度
     */
    private int getTabLayoutOffsetWidth(int index) {
        String str = "";
        for (int i = 0; i < index; i++) {
            //channelNameList是一个List<String>的对象，里面转载的是30个词条
            //取出直到index的tab的文字，计算长度
            str += tabTitle[i];
        }
        return str.length() * 14 + index * 12;
    }
}
