// Generated code from Butter Knife. Do not modify!
package com.anna.duanzi.fragment;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class SearchDefaultFragment$$ViewBinder<T extends com.anna.duanzi.fragment.SearchDefaultFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624249, "field 'tagViewFast'");
    target.tagViewFast = finder.castView(view, 2131624249, "field 'tagViewFast'");
    view = finder.findRequiredView(source, 2131624251, "field 'tagViewHistory'");
    target.tagViewHistory = finder.castView(view, 2131624251, "field 'tagViewHistory'");
    view = finder.findRequiredView(source, 2131624250, "field 'tagViewGuess'");
    target.tagViewGuess = finder.castView(view, 2131624250, "field 'tagViewGuess'");
  }

  @Override public void unbind(T target) {
    target.tagViewFast = null;
    target.tagViewHistory = null;
    target.tagViewGuess = null;
  }
}
