// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class UpdateMobileFragment$$ViewBinder<T extends com.anna.duanzi.fragment.UpdateMobileFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558593, "field 'tv_phone_number_error'");
    target.tv_phone_number_error = finder.castView(view, 2131558593, "field 'tv_phone_number_error'");
    view = finder.findRequiredView(source, 2131558592, "field 'et_phone_number'");
    target.et_phone_number = finder.castView(view, 2131558592, "field 'et_phone_number'");
    view = finder.findRequiredView(source, 2131558618, "field 'tv_bind_phone_number_error'");
    target.tv_bind_phone_number_error = finder.castView(view, 2131558618, "field 'tv_bind_phone_number_error'");
    view = finder.findRequiredView(source, 2131558617, "field 'et_bind_phone_number'");
    target.et_bind_phone_number = finder.castView(view, 2131558617, "field 'et_bind_phone_number'");
    view = finder.findRequiredView(source, 2131558604, "method 'onClick'");
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
    target.et_phone_number = null;
    target.tv_bind_phone_number_error = null;
    target.et_bind_phone_number = null;
  }
}
