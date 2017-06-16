package com.anna.duanzi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.common.DownloadProgressListener;
import com.anna.duanzi.common.FileDownloader;
import com.anna.duanzi.domain.Movies;
import com.anna.duanzi.widget.DownLoadButton;
import com.anna.duanzi.widget.DownLoadText;
import com.avos.avoscloud.AVFile;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;
import com.cn.tfl.update.StorageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/12.
 */
public class DownLoadMoviesAdapter extends
        TflListAdapter<Movies> {

    private Map<Integer, DownLoadThread> mItemLoadThreads;// 用于存放已启动的进度条 ,
    // 在点击启动线程的时候设置值
    private Map<Integer, DownLoadHandler> mItemLoadHandlers;
    private Map<Integer, BarInfo> mItemProgressInfo;// 用于存放所有进度条的信息，在创建bar的时候就要设置值了
    private Map<Integer, Boolean> mItemStopFlags;

    private Context mContext;

    public DownLoadMoviesAdapter(List<Movies> data, Context context) {
        super(data);
        this.mContext = context;
        mItemLoadThreads = new HashMap<>();
        mItemProgressInfo = new HashMap<>();
        mItemLoadHandlers = new HashMap<>();
        mItemStopFlags = new HashMap<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateEmptyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_empty_material, parent, false);
        return new TflSimpleViewHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.un_down_load_movies_item, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindDataViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Movies movies = mData.get(position);
        AVFile movieFile = movies.getAVFile("movie");
        final String downUrl = movieFile.getUrl();
        ((DataViewHolder) holder).tv_title.setText(movies.title);
        // 设置控件内容
        DownLoadHandler dlh = null;
        if (!mItemLoadHandlers.containsKey(position)) {// 给每一行分配一个handler
            dlh = new DownLoadHandler();
            mItemLoadHandlers.put(position, dlh);
            dlh.setBar(((DataViewHolder) holder).pb_down_load);
            dlh.setDownLoadText(((DataViewHolder) holder).downLoadButton);
        } else {
            dlh = mItemLoadHandlers.get(position);
            dlh.setBar(((DataViewHolder) holder).pb_down_load);
            dlh.setDownLoadText(((DataViewHolder) holder).downLoadButton);
        }
        if (!mItemProgressInfo.containsKey(position)) {// 设置 BarInfo 如果当前行已经存在就不用设置了
            BarInfo value = new BarInfo();
            value.currentProgress = 0;
            value.isDownLoad = false;
            value.maxProgress = 0;
            value.visible = false;
            mItemProgressInfo.put(position, value);
        }
        BarInfo bi = mItemProgressInfo.get(position);
        if (bi.visible) {
            ((DataViewHolder) holder).pb_down_load.setVisibility(View.VISIBLE);
        } else {
            ((DataViewHolder) holder).pb_down_load.setVisibility(View.INVISIBLE);
        }
        if (!mItemStopFlags.containsKey(position)) {
            mItemStopFlags.put(position, false);
        }
        ((DataViewHolder) holder).pb_down_load.setMax(bi.maxProgress);
        ((DataViewHolder) holder).pb_down_load.setProgress(bi.currentProgress);
        ((DataViewHolder) holder).downLoadButton.setOnDownLoadButtonClickListener(new DownLoadText.OnDownLoadButtonClickListener() {
            @Override
            public void onClick(View v, int curState) {
                switch (curState) {
                    case DownLoadButton.STATE_NORMAL:
                        ((DataViewHolder) holder).downLoadButton.setState(DownLoadButton.STATE_DOWNLOADING);
                        break;
                    case DownLoadButton.STATE_DOWNLOADING:
                        ((DataViewHolder) holder).downLoadButton.setState(DownLoadButton.STATE_STOPDOWNLOADING);
                        mItemStopFlags.put(position, true);
                        break;
                    case DownLoadButton.STATE_STOPDOWNLOADING:
                        ((DataViewHolder) holder).downLoadButton.setState(DownLoadButton.STATE_DOWNLOADING);
                        mItemStopFlags.put(position, false);
                        break;
                }
                if (downUrl != null) {
                    download(downUrl, position);
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
        public TextView tv_title;
        private DownLoadText downLoadButton;
        public ProgressBar pb_down_load;

        public DataViewHolder(View rootView) {
            super(rootView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            downLoadButton = (DownLoadText) itemView.findViewById(R.id.btn_down_load);
            pb_down_load = (ProgressBar) itemView.findViewById(R.id.pb_down_load);
        }
    }


    class BarInfo { // 用来分装 Bar（进度条）的信息
        boolean isDownLoad;// 是否已经下载,这个参数
        // 我暂时没有使用，但是想要将下载系统做的更完善，应该是需要它的，大家可以完善下~嘿嘿
        boolean visible;// bar是否显示
        int currentProgress;// 进度条当前的值
        int maxProgress;// 进度条的最大值
    }


    class DownLoadHandler extends Handler {
        private ProgressBar bar;
        private DownLoadText downLoadText;

        public void setBar(ProgressBar bar) {
            this.bar = bar;
        }

        public void setDownLoadText(DownLoadText downLoadText) {
            this.downLoadText = downLoadText;
        }

        @Override
        public void handleMessage(Message msg) {
            BarInfo bi = (BarInfo) msg.obj;
            if (bi.currentProgress >= bi.maxProgress) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.DOWN_LOAD_COMPLETE_BROADCAST");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                downLoadText.setState(DownLoadText.STATE_COMPLETE);
            }
            bar.setVisibility(View.VISIBLE);
            bar.setMax(bi.maxProgress);
            bar.setProgress(bi.currentProgress);

        }
    }


    private void download(final String path, final int position) {
        if (!mItemLoadThreads.containsKey(position)) {
            DownLoadThread downLoadThread = new DownLoadThread();
            downLoadThread.setPath(path);
            downLoadThread.setPosition(position);
            downLoadThread.start();
            mItemLoadThreads.put(position, downLoadThread);
        }
    }

    class DownLoadThread extends Thread {
        String path;
        int position;

        public void setPath(String path) {
            this.path = path;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void run() {
            final FileDownloader loader = new FileDownloader(mContext, path, StorageUtils.getCacheDirectory(mContext), 3, mItemStopFlags.get(position));
            final BarInfo bi = mItemProgressInfo.get(position);
            bi.maxProgress = loader.getFileSize();
            try {
                loader.download(new DownloadProgressListener() {
                    @Override
                    public void onDownloadSize(int size) {//实时获知文件已经下载的数据长度
                        bi.visible = true;
                        bi.currentProgress = size;
                        Message message = new Message();
                        message.obj = bi;
                        mItemLoadHandlers.get(position).sendMessage(message);
                    }
                });
            } catch (Exception e) {

            }
        }
    }
}
