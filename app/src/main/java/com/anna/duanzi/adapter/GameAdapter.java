package com.anna.duanzi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.domain.Comment;
import com.anna.duanzi.domain.Duanzi;
import com.anna.duanzi.domain.Games;
import com.anna.duanzi.domain.Image;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;

import java.util.List;

/**
 * Created by tfl on 2016/10/12.
 */
public class GameAdapter extends
        TflListAdapter<Games> {
    private Context mContext;
    public GameAdapter(List<Games> data, Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.apk_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Games  games = mData.get(position);
        AVFile imageFile=games.imageFile;
        if (imageFile != null) {
            Glide.with(mContext).load(imageFile.getUrl()).placeholder(R.drawable.image_default_normal).into(((ViewHolder) holder).img_icon);
        }
        ((ViewHolder) holder).tv_name.setText(games.name);
        ((ViewHolder) holder).tv_desc.setText(games.desc);
        holder.itemView.setTag(games);
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
        TextView tv_name,tv_desc;
        ImageView img_icon;

        public ViewHolder(View rootView) {
            super(rootView);
            tv_name = (TextView) itemView.findViewById(R.id.textView1);
            img_icon = (ImageView) itemView.findViewById(R.id.imageView);
            tv_desc = (TextView) itemView.findViewById(R.id.textView2);
        }
    }
}
