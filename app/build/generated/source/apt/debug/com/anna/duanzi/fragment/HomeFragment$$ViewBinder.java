// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeFragment$$ViewBinder<T extends com.anna.duanzi.fragment.HomeFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624207, "field 'rv_head'");
    target.rv_head = finder.castView(view, 2131624207, "field 'rv_head'");
    view = finder.findRequiredView(source, 2131624066, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131624066, "field 'mRecyclerView'");
  }

  @Override public void unbind(T target) {
    target.rv_head = null;
    target.mRecyclerView = null;
  }
}