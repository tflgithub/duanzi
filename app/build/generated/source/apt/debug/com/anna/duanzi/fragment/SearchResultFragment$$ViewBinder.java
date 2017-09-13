// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SearchResultFragment$$ViewBinder<T extends com.anna.duanzi.fragment.SearchResultFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624231, "field 'listView'");
    target.listView = finder.castView(view, 2131624231, "field 'listView'");
    view = finder.findRequiredView(source, 2131624293, "field 'loadingView'");
    target.loadingView = finder.castView(view, 2131624293, "field 'loadingView'");
    view = finder.findRequiredView(source, 2131624232, "field 'emptyResult'");
    target.emptyResult = finder.castView(view, 2131624232, "field 'emptyResult'");
  }

  @Override public void unbind(T target) {
    target.listView = null;
    target.loadingView = null;
    target.emptyResult = null;
  }
}
