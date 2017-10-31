package com.anna.duanzi.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.domain.Movies;
import com.anna.duanzi.upgrade.DownloadProgressListener;
import com.anna.duanzi.upgrade.FileDownloader;
import com.anna.duanzi.upgrade.FileService;
import com.anna.duanzi.widget.DownloadProgressButton;
import com.avos.avoscloud.AVFile;
import com.bumptech.glide.Glide;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflSimpleViewHolder;
import com.cn.tfl.update.StorageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/10/12.
 */
public class DownLoadMoviesAdapter extends
        TflListAdapter<Movies> {

    private Context mContext;

    private FileService fileService;

    private Executor THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(3);//线程池 默认开三条线程。

    public DownLoadMoviesAdapter(List<Movies> data, Context context) {
        super(data);
        this.mContext = context;
        fileService = new FileService(context);

    }

    @Override
    public void setData(List<Movies> data) {
        THREAD_POOL_EXECUTOR = Executors.newFixedThreadPool(data.size());
        super.setData(data);
    }

    private Map<Integer, AsyncTask> taskMap = new HashMap<>();


    public Map<Integer, AsyncTask> getTaskMap() {
        return taskMap;
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
        final AVFile movieFile = movies.getAVFile("movie");
        final String downUrl = movieFile.getUrl();
        ((DataViewHolder) holder).tv_title.setText(movies.title);
        AVFile imageFile = movies.getAVFile("image");
        if (imageFile != null) {
            Glide.with(mContext).load(imageFile.getUrl()).placeholder(R.drawable.image_default_normal).into((((DataViewHolder) holder)).iv_thumb);
        }
        String path = checkDownLoad(movies.name + ".mp4");
        if (path != null && taskMap.isEmpty()) {
            if (fileService.getData(downUrl).size() > 0) {
                ((DataViewHolder) holder).downloadProgressButton.pause();
            } else {
                ((DataViewHolder) holder).downloadProgressButton.finish();
            }
        }
        ((DataViewHolder) holder).downloadProgressButton.setOnDownLoadClickListener(new DownloadProgressButton.OnDownLoadClickListener() {
            @Override
            public void clickDownload() {
                if (downUrl != null) {
                    taskMap.put(position, new DownLoadTask(((DataViewHolder) holder).downloadProgressButton, position).executeOnExecutor(THREAD_POOL_EXECUTOR, new String[]{downUrl, movies.name}));
                }
            }

            @Override
            public void clickPause() {

            }

            @Override
            public void clickResume() {
                taskMap.put(position, new DownLoadTask(((DataViewHolder) holder).downloadProgressButton, position).executeOnExecutor(THREAD_POOL_EXECUTOR, new String[]{downUrl, movies.name}));
            }

            @Override
            public void clickFinish() {
                String path = checkDownLoad(movies.name + ".mp4");
                if (path != null) {
                    Intent it = new Intent(Intent.ACTION_VIEW);
                    it.setDataAndType(Uri.parse(path), "video/MP4");
                    mContext.startActivity(it);
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
        public ImageView iv_thumb;
        public DownloadProgressButton downloadProgressButton;

        public DataViewHolder(View rootView) {
            super(rootView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            iv_thumb = (ImageView) itemView.findViewById(R.id.iv_thumb);
            downloadProgressButton = (DownloadProgressButton) itemView.findViewById(R.id.btn_down_load);
        }
    }


    private class DownLoadTask extends AsyncTask<String, Integer, Integer> {
        DownloadProgressButton downloadProgressButton;
        int position = 0;

        public DownLoadTask(DownloadProgressButton downloadProgressButton, int position) {
            this.downloadProgressButton = downloadProgressButton;
            this.position = position;
        }

        @Override
        protected Integer doInBackground(String... params) {
            FileDownloader loader = new FileDownloader(mContext, params[0], StorageUtils.getCacheDirectory(mContext), params[1], 3);
            final int maxProgress = loader.getFileSize();
            try {
                loader.download(new DownloadProgressListener() {
                    @Override
                    public void onDownloadSize(int size) {
                        Log.e("progress:", (int) ((size / (float) maxProgress) * 100) + "%");
                        publishProgress((int) ((size / (float) maxProgress) * 100));
                    }
                });
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            downloadProgressButton.reset();
            downloadProgressButton.setMaxProgress(100);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.e("显示进度:", values[0] + "%");
            downloadProgressButton.setProgress(values[0]);
            if (values[0] == downloadProgressButton.getMaxProgress()) {
                taskMap.remove(position);
            }
        }
    }

    private String checkDownLoad(String fileName) {
        File file = StorageUtils.getCacheDirectory(mContext);
        File[] files = file.listFiles();
        if (files != null) {
            for (File mp4 : files) {
                if (mp4.exists() && mp4.getPath().toLowerCase().endsWith(".mp4")) {
                    if (fileName.equals(mp4.getName())) {
                        return mp4.getAbsolutePath();
                    }
                }
            }
        }
        return null;
    }
}
