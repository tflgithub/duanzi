// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AttentionPersonActivity$$ViewBinder<T extends com.anna.duanzi.activity.AttentionPersonActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624070, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131624070, "field 'mViewPager'");
    view = finder.findRequiredView(source, 2131624068, "field 'tabLayout'");
    target.tabLayout = finder.castView(view, 2131624068, "field 'tabLayout'");
    view = finder.findRequiredView(source, 2131624067, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624069, "method 'onClick'");
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
    target.mViewPager = null;
    target.tabLayout = null;
  }
}
