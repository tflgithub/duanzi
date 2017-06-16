package com.cn.fodel.tfl_list_recycler_view;

import android.view.View;

/**
 * Created by tfl on 2016/10/12.
 */
public interface TflListInterface {

    /**
     * click empty view
     */
    public interface OnEmptyViewClickListener {
        void onEmptyViewClick(View view);
    }

    /**
     * click error view
     */
    public interface OnErrorViewClickListener {
        void onErrorViewClick(View view);
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, T t);
    }
}
