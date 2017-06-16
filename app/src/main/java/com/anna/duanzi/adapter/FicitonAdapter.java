package com.anna.duanzi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.common.Contants;
import com.anna.duanzi.domain.Fiction;
import com.anna.duanzi.utils.ContextUtils;
import com.avos.avoscloud.AVFile;
import com.bumptech.glide.Glide;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;

import java.util.List;

/**
 * Created by tfl on 2016/10/12.
 */
public class FicitonAdapter extends
        TflListAdapter<Fiction> {
    private Context mContext;

    public FicitonAdapter(List<Fiction> data, Context context) {
        super(data);
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_material, parent, false);
        return new TflSimpleViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fiction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position) {
        Fiction fiction = mData.get(position);
        ((ViewHolder) holder).tv_name.setText(fiction.name);
        ((ViewHolder) holder).tv_state.setText(fiction.state);
        if (fiction.state.equals("连载")) {
            ((ViewHolder) holder).tv_state.setTextColor(ContextUtils.getContext().getResources().getColor(R.color.green));
        } else {
            ((ViewHolder) holder).tv_state.setTextColor(ContextUtils.getContext().getResources().getColor(R.color.orange));
        }
        if (fiction.readPermission.equals(Contants.MEMBER.MEMBER_LEVEL_0)) {
            ((ViewHolder) holder).tv_read_permission.setText("免费");
            ((ViewHolder) holder).tv_read_permission.setTextColor(ContextUtils.getContext().getResources().getColor(R.color.green));
        } else {
            ((ViewHolder) holder).tv_read_permission.setText("会员");
            ((ViewHolder) holder).tv_read_permission.setTextColor(ContextUtils.getContext().getResources().getColor(R.color.orange));
        }
        AVFile imageFile = fiction.getAVFile("coverImage");
        if (imageFile != null) {
            Glide.with(mContext).load(imageFile.getUrl()).placeholder(R.drawable.image_default_normal).into(((ViewHolder) holder).iv_cover);
        }
        holder.itemView.setTag(fiction);
    }

    @Override
    public RecyclerView.ViewHolder onCreateErrorViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_net_error_material, parent, false);
        return new TflSimpleViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateLoadingViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_loading_material, parent, false);
        return new TflSimpleViewHolder(view);
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_state, tv_read_permission;
        ImageView iv_cover;

        public ViewHolder(View rootView) {
            super(rootView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_state = (TextView) itemView.findViewById(R.id.tv_state);
            tv_read_permission = (TextView) itemView.findViewById(R.id.tv_read_permission);
            iv_cover = (ImageView) itemView.findViewById(R.id.iv_cover);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        String footer = (String) mHeaders.get(position);
        ((HeaderViewHolder) holder).mText.setText(footer);
        holder.itemView.setTag(footer);
    }

    private static final class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView mText;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }
}
