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
  }

  @Override public void unbind(T target) {
    target.mTabHost = null;
    target.tabWidget = null;
  }
}
