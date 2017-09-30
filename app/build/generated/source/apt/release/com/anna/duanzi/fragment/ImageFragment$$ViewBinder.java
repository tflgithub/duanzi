// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ImageFragment$$ViewBinder<T extends com.anna.duanzi.fragment.ImageFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624230, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131624230, "field 'mRecyclerView'");
    view = finder.findRequiredView(source, 2131624229, "field 'mBlurView'");
    target.mBlurView = finder.castView(view, 2131624229, "field 'mBlurView'");
  }

  @Override public void unbind(T target) {
    target.mRecyclerView = null;
    target.mBlurView = null;
  }
}
