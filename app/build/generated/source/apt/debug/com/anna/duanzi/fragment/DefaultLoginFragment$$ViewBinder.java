// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DefaultLoginFragment$$ViewBinder<T extends com.anna.duanzi.fragment.DefaultLoginFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624194, "field 'tv_phone_number_error'");
    target.tv_phone_number_error = finder.castView(view, 2131624194, "field 'tv_phone_number_error'");
    view = finder.findRequiredView(source, 2131624202, "field 'tv_vail_code_error'");
    target.tv_vail_code_error = finder.castView(view, 2131624202, "field 'tv_vail_code_error'");
    view = finder.findRequiredView(source, 2131624193, "field 'et_phone_number'");
    target.et_phone_number = finder.castView(view, 2131624193, "field 'et_phone_number'");
    view = finder.findRequiredView(source, 2131624201, "field 'et_vail_code'");
    target.et_vail_code = finder.castView(view, 2131624201, "field 'et_vail_code'");
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
    view = finder.findRequiredView(source, 2131624203, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624198, "method 'onClick'");
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
    target.tv_phone_number_error = null;
    target.tv_vail_code_error = null;
    target.et_phone_number = null;
    target.et_vail_code = null;
    target.tv_send_vail_code = null;
  }
}