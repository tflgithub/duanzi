// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PublishActivity$$ViewBinder<T extends com.anna.duanzi.activity.PublishActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624263, "field 'header_actionbar_title'");
    target.header_actionbar_title = finder.castView(view, 2131624263, "field 'header_actionbar_title'");
    view = finder.findRequiredView(source, 2131624264, "field 'header_actionbar_action' and method 'onClick'");
    target.header_actionbar_action = finder.castView(view, 2131624264, "field 'header_actionbar_action'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624132, "method 'onClick'");
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
    target.header_actionbar_title = null;
    target.header_actionbar_action = null;
  }
}
