// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FolloweeFragment$$ViewBinder<T extends com.anna.duanzi.fragment.FolloweeFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624066, "field 'listRecyclerView'");
    target.listRecyclerView = finder.castView(view, 2131624066, "field 'listRecyclerView'");
  }

  @Override public void unbind(T target) {
    target.listRecyclerView = null;
  }
}
