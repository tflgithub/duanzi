package com.anna.duanzi.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.common.Constants;

import java.util.HashMap;

/**
 * Created by tfl on 2016/1/22.
 */
public class FragmentFactory {

    private static HashMap<Integer,Fragment> hashMap = new HashMap<Integer, Fragment>();

    public static BaseFragment createFragment(int position) {
        BaseFragment baseFragment = null;
        //有对象就获取，没对象就创建
        if (hashMap.containsKey(position)) {
            //从缓存中获取对象
            if (hashMap.get(position) != null) {
                baseFragment = (BaseFragment) hashMap.get(position);
            }
        } else {
            //没有对象的时候，创建
            Bundle bundle=new Bundle();
            switch (position) {
                case 0:
                    baseFragment = new FragmentHome();
                    break;
                case 1:
                    baseFragment = new HomeFragment();
                    bundle.putString(Constants.CATEGORY, Constants.CATEGORY_IMAGE);
                    baseFragment.setArguments(bundle);
                    break;
                case 2:
                    baseFragment = new HomeFragment();
                    bundle.putString(Constants.CATEGORY, Constants.CATEGORY_VIDEO);
                    baseFragment.setArguments(bundle);
                    break;
                case 3:
                    baseFragment = new FoundFragment();
                    break;
            }
            //缓存到本地
            hashMap.put(position, baseFragment);
        }
        return baseFragment;
    }
}
