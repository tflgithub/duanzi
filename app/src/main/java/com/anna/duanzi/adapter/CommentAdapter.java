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
import com.anna.duanzi.utils.DateUtils;
import com.anna.duanzi.utils.StringUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;

import java.util.List;

/**
 * Created by tfl on 2016/10/12.
 */
public class CommentAdapter extends
        TflListAdapter<Comment> {

    private Context mContext;


    public CommentAdapter(List<Comment> data, Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Comment comment = mData.get(position);
        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        userQuery.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
        userQuery.whereEqualTo("mobilePhoneNumber", comment.user);
        userQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> list, AVException e) {
                if (e == null) {
                    String nickName = list.get(0).getString("nickName");
                    String headPath = list.get(0).getString("headImage");
                    Glide.with(mContext).load(headPath).placeholder(R.drawable.default_round_head).into(((ViewHolder) holder).iv_user_head);
                    if (nickName.isEmpty()) {
                        ((ViewHolder) holder).tv_user_name.setText("路友");
                    } else {
                        ((ViewHolder) holder).tv_user_name.setText(nickName);
                    }
                }
            }
        });

        ((ViewHolder) holder).tv_comment_content.setText(comment.commentContent);
        ((ViewHolder) holder).tv_comment_time.setText(DateUtils.utc2Local(comment.createdAt, "yyyy-MM-dd'T'HH:mm:ss.SSS Z", "yyyy-MM-dd HH:mm"));
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
        TextView tv_user_name, tv_comment_content, tv_comment_time;
        ImageView iv_user_head;

        public ViewHolder(View rootView) {
            super(rootView);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_comment_content = (TextView) itemView.findViewById(R.id.tv_comment_content);
            tv_comment_time = (TextView) itemView.findViewById(R.id.tv_comment_time);
            iv_user_head = (ImageView) itemView.findViewById(R.id.iv_user_head);
        }
    }
}
