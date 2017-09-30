// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PublishEditActivity$$ViewBinder<T extends com.anna.duanzi.activity.PublishEditActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624115, "field 'tv_publish' and method 'onClick'");
    target.tv_publish = finder.castView(view, 2131624115, "field 'tv_publish'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624114, "field 'tv_cancel' and method 'onClick'");
    target.tv_cancel = finder.castView(view, 2131624114, "field 'tv_cancel'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624116, "field 'et_title'");
    target.et_title = finder.castView(view, 2131624116, "field 'et_title'");
  }

  @Override public void unbind(T target) {
    target.tv_publish = null;
    target.tv_cancel = null;
    target.et_title = null;
  }
}
