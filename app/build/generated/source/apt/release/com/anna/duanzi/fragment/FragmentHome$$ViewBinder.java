// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FragmentHome$$ViewBinder<T extends com.anna.duanzi.fragment.FragmentHome> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624236, "field 'tabLayout'");
    target.tabLayout = finder.castView(view, 2131624236, "field 'tabLayout'");
    view = finder.findRequiredView(source, 2131624237, "field 'viewPager'");
    target.viewPager = finder.castView(view, 2131624237, "field 'viewPager'");
    view = finder.findRequiredView(source, 2131624087, "field 'iv_user_head' and method 'onClick'");
    target.iv_user_head = finder.castView(view, 2131624087, "field 'iv_user_head'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624235, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.tabLayout = null;
    target.viewPager = null;
    target.iv_user_head = null;
  }
}
