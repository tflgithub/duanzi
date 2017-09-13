package com.anna.duanzi.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.domain.WebMovie;
import com.avos.avoscloud.AVFile;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by Administrator on 2016/10/12.
 */
public class OnlineMoviesAdapter extends
        TflListAdapter<WebMovie> {

    public OnlineMoviesAdapter(List<WebMovie> data) {
        super(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_material, parent, false);
        return new TflSimpleViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_item, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final WebMovie movies = mData.get(position);
        AVFile videoImageFile = movies.getAVFile("thumbImage");
        DataViewHolder videoViewHolder = (DataViewHolder) holder;
        if(videoImageFile!=null) {
            videoViewHolder.jcVideoPlayer.setUp(movies.url,
                    videoImageFile.getUrl(),
                    movies.title);
        }
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

        public DataViewHolder(View rootView) {
            super(rootView);
            jcVideoPlayer = (JCVideoPlayer) itemView.findViewById(R.id.video_controller);
        }
    }
}
