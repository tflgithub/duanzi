package com.anna.duanzi.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.domain.Area;
import com.anna.duanzi.utils.ContextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by tfl on 2016/10/11.
 */
public class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabViewHolder> implements View.OnClickListener {

    private List<Area> mItems = new ArrayList<>();

    public TabAdapter(List<Area> mItems) {
        this.mItems = mItems;
        for (int i = 0; i < mItems.size(); i++) {
            if (i == 0) {
                //默认选中第一个。
                vector.add(true);
            }
            vector.add(false);
        }
    }

    Vector<Boolean> vector = new Vector<>();

    @Override
    public TabViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_tab, parent, false);
        view.setOnClickListener(this);
        return new TabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TabViewHolder holder, final int position) {
        Area area = mItems.get(position);
        holder.title.setText(area.areaName);
        holder.itemView.setTag(position);
        if (vector.get(position)) {
            holder.title.setTextColor(ContextUtils.getContext().getResources().getColor(R.color.colorPrimary));
        } else {
            holder.title.setTextColor(Color.GRAY);
        }
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        vector.set(position, !vector.get(position));
        //将没有选中的选项全部还原。
        for (int i = 0; i < vector.size(); i++) {
            if (position == i) {
                continue;
            }
            vector.set(i, false);
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onClick(v, position);
        }
    }

    public class TabViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public TabViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(View view, final int position);
    }
}
