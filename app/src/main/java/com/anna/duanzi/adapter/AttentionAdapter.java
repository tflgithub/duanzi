package com.anna.duanzi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FollowCallback;
import com.bumptech.glide.Glide;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;

import java.util.List;

/**
 * Created by tfl on 2016/10/12.
 */
public class AttentionAdapter extends
        TflListAdapter<AVUser> {
    private Context mContext;

    public AttentionAdapter(List<AVUser> data, Context context) {
        super(data);
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_material, parent, false);
        ((TextView)view.findViewById(R.id.tv_list_empty)).setText("你还没有关注");
        return new TflSimpleViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attention_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final AVUser user = mData.get(position);
        Glide.with(mContext).load(user.getString("headImage")).placeholder(R.drawable.hugh).into(((ViewHolder) holder).img_head);
        ((ViewHolder) holder).tv_name.setText(user.getString("nickName"));
        Button btn=((ViewHolder) holder).btn_add;
        btn.setText("取消关注");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAttention(user.getObjectId(), position);
            }
        });
        holder.itemView.setTag(user);
    }

    private void cancelAttention(String userObjectId, final int position) {
        AVUser.getCurrentUser().unfollowInBackground(userObjectId, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                if (e == null) {
                    getData().remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "取消关注", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "取消关注失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        TextView tv_name;
        ImageView img_head;
        Button btn_add;

        public ViewHolder(View rootView) {
            super(rootView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            img_head = (ImageView) itemView.findViewById(R.id.iv_head);
            btn_add = (Button) itemView.findViewById(R.id.btn_add);
        }
    }
}
