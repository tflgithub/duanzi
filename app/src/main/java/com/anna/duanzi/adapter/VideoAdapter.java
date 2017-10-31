package com.anna.duanzi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.common.HttpHelper;
import com.anna.duanzi.domain.Comment;
import com.anna.duanzi.domain.Duanzi;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.PlayListener;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Administrator on 2016/10/12.
 */
public class VideoAdapter extends
        TflListAdapter<Duanzi> {
    private Context mContext;
    private AVQuery<Comment> commentAVQuery = AVQuery.getQuery("Comment");
    private AVQuery<AVObject> diggAVQuery = new AVQuery<>("Digg");
    private AVQuery<AVObject> clickAVQuery = new AVQuery<>("Click_Statistics");
    private AVQuery<AVUser> avUserAVQuery = new AVQuery<>("_User");
    public VideoAdapter(List<Duanzi> data,Context context) {
        super(data);
        diggAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        clickAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        commentAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        mContext=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_material, parent, false);
        return new TflSimpleViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Duanzi duanzi = mData.get(position);
        AVFile videoFile = duanzi.getAVFile("data");
        AVFile videoImageFile = duanzi.getAVFile("image");
        DataViewHolder videoViewHolder = (DataViewHolder) holder;
        if (videoFile.getUrl() != null && videoImageFile.getUrl() != null) {
            avUserAVQuery.getInBackground(videoFile.getOwnerObjectId(), new GetCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null && avUser != null) {
                        ((VideoAdapter.DataViewHolder) holder).tv_username.setText(avUser.getUsername());
                        Glide.with(mContext)
                                .load(avUser.getString("headImage"))
                                .crossFade()
                                .centerCrop()
                                .bitmapTransform(new CropCircleTransformation(mContext))
                                .placeholder(R.drawable.default_round_head)
                                .into((((VideoAdapter.DataViewHolder) holder).header_image_publisher));
                    }
                }
            });
            videoViewHolder.jcVideoPlayer.setUp(videoFile.getUrl(),
                    videoImageFile.getUrl(),
                    duanzi.title);
        }
        //这个监听是为了获取用户播放次数的。
        videoViewHolder.jcVideoPlayer.setPlayListener(new PlayListener() {
            @Override
            public void onPlay() {
                HttpHelper.getInstance().clickStatistics(duanzi.objectId);
            }
        });
        commentAVQuery.whereEqualTo("commentId", duanzi.objectId);
        commentAVQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    ((DataViewHolder) holder).tv_comment.setText(i + "条评论");
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
                    ((DataViewHolder) holder).tv_digg.setText(i + "");
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
                    ((DataViewHolder) holder).tv_click_num.setText(i + "次播放");
                } else {
                    // 查询失败
                }
            }
        });
        holder.itemView.setTag(videoViewHolder);
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

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder, int position) {
        String header = (String) mFooters.get(position);
        ((FooterViewHolder) holder).mText.setText(header);
        holder.itemView.setTag(header);
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView mText;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.tv_header);
        }
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        public JCVideoPlayer jcVideoPlayer;
        public TextView tv_click_num, tv_comment, tv_digg,tv_username;
        ImageView header_image_publisher;
        public DataViewHolder(View rootView) {
            super(rootView);
            jcVideoPlayer = (JCVideoPlayer) itemView.findViewById(R.id.video_controller);
            jcVideoPlayer.setThumbImageViewScalType(ImageView.ScaleType.FIT_XY);
            tv_click_num = (TextView) itemView.findViewById(R.id.tv_click_num);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_digg = (TextView) itemView.findViewById(R.id.tv_dig);
            tv_username = (TextView) itemView.findViewById(R.id.tv_user_name);
            header_image_publisher = (ImageView) itemView.findViewById(R.id.header_image_publisher);
        }
    }
}
