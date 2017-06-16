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
import com.anna.duanzi.widget.BadgeView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.bumptech.glide.Glide;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;

import java.util.List;

/**
 * Created by tfl on 2016/10/12.
 */
public class TxtAdapter extends
        TflListAdapter<Duanzi> {

    private Context mContext;

    private AVQuery<Comment> commentAVQuery = AVQuery.getQuery("Comment");
    private AVQuery<AVObject> diggAVQuery = new AVQuery<>("Digg");
    private AVQuery<AVObject> clickAVQuery=new AVQuery<>("Click_Statistics");


    public TxtAdapter(List<Duanzi> data, Context context) {
        super(data);
        commentAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        diggAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        clickAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_material, parent, false);
        return new TflSimpleViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.txt_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Duanzi duanzi = mData.get(position);
        ((ViewHolder) holder).tv_title.setText(duanzi.title);
        AVFile imageFile = duanzi.getAVFile("image");
        if (imageFile != null) {
            ((ViewHolder) holder).iv_title.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imageFile.getUrl()).placeholder(R.drawable.image_default_normal).into(((ViewHolder) holder).iv_title);
        } else {
            ((ViewHolder) holder).iv_title.setVisibility(View.GONE);
        }
        commentAVQuery.whereEqualTo("commentId", duanzi.objectId);
        commentAVQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    ((ViewHolder) holder).tv_comment.setText(i + "条评论");
                } else {
                    // 查询失败
                }
            }
        });
        diggAVQuery.whereEqualTo("diggId", duanzi.objectId);
        diggAVQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    ((ViewHolder) holder).tv_digg.setText(i + "");
                } else {
                    // 查询失败
                }
            }
        });
        clickAVQuery.whereEqualTo("clickId", duanzi.objectId);
        clickAVQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    ((ViewHolder) holder).tv_click_num.setText(i + "人阅读过");
                } else {
                    // 查询失败
                }
            }
        });
        holder.itemView.setTag(duanzi);
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
        TextView tv_title, tv_click_num, tv_comment, tv_digg;
        ImageView iv_title;

        public ViewHolder(View rootView) {
            super(rootView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_click_num = (TextView) itemView.findViewById(R.id.tv_click_num);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_digg = (TextView) itemView.findViewById(R.id.tv_dig);
            iv_title = (ImageView) itemView.findViewById(R.id.iv_title);
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

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder, int position) {
        String footer = (String) mFooters.get(position);
        ((FooterViewHolder) holder).mText.setText(footer);
        holder.itemView.setTag(footer);
    }

    private static final class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView mText;
        public FooterViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }
}
