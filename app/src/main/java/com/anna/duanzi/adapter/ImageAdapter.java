package com.anna.duanzi.adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.domain.Comment;
import com.anna.duanzi.domain.Duanzi;
import com.anna.duanzi.domain.Image;
import com.anna.duanzi.widget.BadgeView;
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
public class ImageAdapter extends
        TflListAdapter<Duanzi> {

    private Context mContext;

    private AVQuery<Comment> commentAVQuery = AVQuery.getQuery("Comment");
    private AVQuery<AVObject> diggAVQuery = new AVQuery<>("Digg");
    private AVQuery<AVObject> clickAVQuery = new AVQuery<>("Click_Statistics");

    public ImageAdapter(List<Duanzi> data, Context context) {
        super(data);
        diggAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        clickAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        commentAVQuery.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_material, parent, false);
        return new TflSimpleViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Duanzi duanzi = mData.get(position);
        AVFile imageFile = duanzi.getAVFile("image");
        if (imageFile != null) {
            Glide.with(mContext).load(imageFile.getUrl()).placeholder(R.drawable.image_default_normal).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (((ViewHolder) holder).img_content == null) {
                        return false;
                    }
                    if (((ViewHolder) holder).img_content.getScaleType() != ImageView.ScaleType.FIT_XY) {
                        ((ViewHolder) holder).img_content.setScaleType(ImageView.ScaleType.FIT_XY);
                    }
                    ViewGroup.LayoutParams params = ((ViewHolder) holder).img_content.getLayoutParams();
                    int vw = ((ViewHolder) holder).img_content.getWidth() - ((ViewHolder) holder).img_content.getPaddingLeft() - ((ViewHolder) holder).img_content.getPaddingRight();
                    float scale = (float) vw / (float) resource.getIntrinsicWidth();
                    int vh = Math.round(resource.getIntrinsicHeight() * scale);
                    params.height = vh + ((ViewHolder) holder).img_content.getPaddingTop() + ((ViewHolder) holder).img_content.getPaddingBottom();
                    ((ViewHolder) holder).img_content.setLayoutParams(params);
                    return false;
                }
            }).into(((ViewHolder) holder).img_content);
        }
        ((ViewHolder) holder).tv_title.setText(duanzi.title);
        countImage(duanzi.objectId, ((ViewHolder) holder).tv_image_count);
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
                    ((ViewHolder) holder).tv_click_num.setText(i + "人欣赏过");
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

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_image_count, tv_click_num, tv_comment, tv_digg;
        ImageView img_content;

        public ViewHolder(View rootView) {
            super(rootView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            img_content = (ImageView) itemView.findViewById(R.id.img_content);
            tv_image_count = (TextView) itemView.findViewById(R.id.tv_image_num);
            tv_click_num = (TextView) itemView.findViewById(R.id.tv_click_num);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_digg = (TextView) itemView.findViewById(R.id.tv_dig);
        }
    }

    private void countImage(String imageId, final TextView textView) {
        AVQuery<Image> query = AVQuery.getQuery("Image");
        query.whereEqualTo("imageId", imageId);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    // 查询成功，输出计数
                    textView.setText(i + " 图");
                } else {
                    // 查询失败
                }
            }
        });
    }
}
