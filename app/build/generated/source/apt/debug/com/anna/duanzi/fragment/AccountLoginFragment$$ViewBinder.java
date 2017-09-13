// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AccountLoginFragment$$ViewBinder<T extends com.anna.duanzi.fragment.AccountLoginFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624194, "field 'tv_phone_number_error'");
    target.tv_phone_number_error = finder.castView(view, 2131624194, "field 'tv_phone_number_error'");
    view = finder.findRequiredView(source, 2131624197, "field 'tv_password_error'");
    target.tv_password_error = finder.castView(view, 2131624197, "field 'tv_password_error'");
    view = finder.findRequiredView(source, 2131624193, "field 'et_phone_number'");
    target.et_phone_number = finder.castView(view, 2131624193, "field 'et_phone_number'");
    view = finder.findRequiredView(source, 2131624195, "field 'et_password'");
    target.et_password = finder.castView(view, 2131624195, "field 'et_password'");
    view = finder.findRequiredView(source, 2131624196, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131624199, "method 'onClick'");
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
    target.tv_password_error = null;
    target.et_phone_number = null;
    target.et_password = null;
  }
}
