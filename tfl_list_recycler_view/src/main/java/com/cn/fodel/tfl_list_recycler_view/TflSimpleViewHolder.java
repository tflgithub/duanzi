package com.cn.fodel.tfl_list_recycler_view;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by tfl on 2016/10/12.
 */
public class TflSimpleViewHolder extends RecyclerView.ViewHolder {
    public TflSimpleViewHolder(View itemView) {
        super(itemView);
        Log.e("TflSimpleViewHolder", "we should find views or add views listener in TflListAdapter.onCreateDataViewHolder method," +
                "so we need to define our own ViewHolder,and find views,add listener inside it, instead of using TflSimpleViewHolder.");
    }
}
