// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ModifyPasswordFragment$$ViewBinder<T extends com.anna.duanzi.fragment.ModifyPasswordFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624202, "field 'tv_vail_code_error'");
    target.tv_vail_code_error = finder.castView(view, 2131624202, "field 'tv_vail_code_error'");
    view = finder.findRequiredView(source, 2131624197, "field 'tv_password_error'");
    target.tv_password_error = finder.castView(view, 2131624197, "field 'tv_password_error'");
    view = finder.findRequiredView(source, 2131624201, "field 'et_vail_code'");
    target.et_vail_code = finder.castView(view, 2131624201, "field 'et_vail_code'");
    view = finder.findRequiredView(source, 2131624195, "field 'et_password'");
    target.et_password = finder.castView(view, 2131624195, "field 'et_password'");
    view = finder.findRequiredView(source, 2131624200, "field 'tv_send_vail_code' and method 'onClick'");
    target.tv_send_vail_code = finder.castView(view, 2131624200, "field 'tv_send_vail_code'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624216, "field 'tv_tip'");
    target.tv_tip = finder.castView(view, 2131624216, "field 'tv_tip'");
    view = finder.findRequiredView(source, 2131624215, "method 'onClick'");
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
    target.tv_vail_code_error = null;
    target.tv_password_error = null;
    target.et_vail_code = null;
    target.et_password = null;
    target.tv_send_vail_code = null;
    target.tv_tip = null;
  }
}