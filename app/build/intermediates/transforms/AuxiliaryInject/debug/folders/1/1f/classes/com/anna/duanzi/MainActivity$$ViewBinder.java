// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.anna.duanzi.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558517, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131558517, "field 'mViewPager'");
  }

  @Override public void unbind(T target) {
    target.mViewPager = null;
  }
}
