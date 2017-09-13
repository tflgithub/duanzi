// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeActivity$$ViewBinder<T extends com.anna.duanzi.activity.HomeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
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
    view = finder.findRequiredView(source, 2131624301, "field 'ly_setting' and method 'onClick'");
    target.ly_setting = finder.castView(view, 2131624301, "field 'ly_setting'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624305, "field 'tv_update'");
    target.tv_update = finder.castView(view, 2131624305, "field 'tv_update'");
    view = finder.findRequiredView(source, 2131624124, "field 'tv_nick_name'");
    target.tv_nick_name = finder.castView(view, 2131624124, "field 'tv_nick_name'");
    view = finder.findRequiredView(source, 2131624253, "field 'tv_followee' and method 'onClick'");
    target.tv_followee = finder.castView(view, 2131624253, "field 'tv_followee'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624254, "field 'tv_follower' and method 'onClick'");
    target.tv_follower = finder.castView(view, 2131624254, "field 'tv_follower'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624115, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624304, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624303, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624302, "method 'onClick'");
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
    target.iv_user_head = null;
    target.ly_setting = null;
    target.tv_update = null;
    target.tv_nick_name = null;
    target.tv_followee = null;
    target.tv_follower = null;
  }
}
