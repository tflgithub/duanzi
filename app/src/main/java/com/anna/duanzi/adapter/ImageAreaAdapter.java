package com.anna.duanzi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.domain.Image;
import com.anna.duanzi.domain.ImageArea;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;

import java.util.List;

/**
 * Created by tfl on 2016/10/12.
 */
public class ImageAreaAdapter extends
        TflListAdapter<ImageArea> {

    public ImageAreaAdapter(List<ImageArea> data) {
        super(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_material, parent, false);
        return new TflSimpleViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_area_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ImageArea imageArea = mData.get(position);
        ((ViewHolder) holder).tv_title.setText(imageArea.title);
        countImage(imageArea.objectId, ((ViewHolder) holder).tv_image_count);
        holder.itemView.setTag(imageArea);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_image_count;

        public ViewHolder(View rootView) {
            super(rootView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_image_count = (TextView) itemView.findViewById(R.id.tv_image_num);
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
                    textView.setText(i + " P");
                } else {
                    // 查询失败
                }
            }
        });
    }
}
