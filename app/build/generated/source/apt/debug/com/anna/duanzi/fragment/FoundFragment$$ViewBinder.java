// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FoundFragment$$ViewBinder<T extends com.anna.duanzi.fragment.FoundFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624236, "field 'title'");
    target.title = finder.castView(view, 2131624236, "field 'title'");
    view = finder.findRequiredView(source, 2131624115, "field 'back'");
    target.back = finder.castView(view, 2131624115, "field 'back'");
    view = finder.findRequiredView(source, 2131624204, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624205, "method 'onClick'");
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
    target.title = null;
    target.back = null;
  }
}