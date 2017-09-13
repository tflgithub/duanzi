// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FragmentHome$$ViewBinder<T extends com.anna.duanzi.fragment.FragmentHome> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 16908306, "field 'mTabHost'");
    target.mTabHost = finder.castView(view, 16908306, "field 'mTabHost'");
    view = finder.findRequiredView(source, 16908307, "field 'tabWidget'");
    target.tabWidget = finder.castView(view, 16908307, "field 'tabWidget'");
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
    view = finder.findRequiredView(source, 2131624214, "method 'onClick'");
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
    target.mTabHost = null;
    target.tabWidget = null;
    target.iv_user_head = null;
  }
}
