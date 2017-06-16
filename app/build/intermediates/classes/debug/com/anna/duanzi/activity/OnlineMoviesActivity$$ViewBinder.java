// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class OnlineMoviesActivity$$ViewBinder<T extends com.anna.duanzi.activity.OnlineMoviesActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558496, "field 'mRecyclerView'");
    target.mRecyclerView = finder.castView(view, 2131558496, "field 'mRecyclerView'");
  }

  @Override public void unbind(T target) {
    target.mRecyclerView = null;
  }
}
