package com.anna.duanzi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.common.DownloadProgressListener;
import com.anna.duanzi.common.FileDownloader;
import com.anna.duanzi.domain.Games;
import com.anna.duanzi.widget.DownLoadButton;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;
import com.cn.tfl.update.StorageUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotGamesFragment extends ListFragment {
    private ArrayAdapter<Games> adapter;
    // 在点击启动线程的时候设置值
    private Map<Integer, DownLoadHandler> mItemLoadHandlers;
    private Map<Integer, Boolean> mItemStopFlags;
    private final static int MESSAGE_DOWNLOADING = 0;
    private Map<Integer, Integer> mItemMaxProcess;
    private Map<Integer, DownLoadThread> mItemDownLoadThread;

    public HotGamesFragment() {
        mItemLoadHandlers = new HashMap<>();
        mItemStopFlags = new HashMap<>();
        mItemMaxProcess = new HashMap<>();
        mItemDownLoadThread = new HashMap<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<Games>(getActivity(), 0) {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                final Holder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.apk_item, null);
                    holder = new Holder();
                    holder.downLoadButton = (DownLoadButton) convertView.findViewById(R.id.button2);
                    holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                    holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                final Games item = getItem(position);
                AVFile imageFile = item.getAVFile("imageFile");
                if (imageFile != null) {
                    Glide.with(getActivity()).load(imageFile.getUrl()).placeholder(R.drawable.image_default_normal).into(holder.imageView);
                }
                AVFile gameFile = item.getAVFile("gameFile");
                final String downUrl = gameFile.getUrl();
                holder.textView1.setText(item.name);
                holder.textView2.setText(item.desc);
                // 设置控件内容
                DownLoadHandler dlh = null;
                if (!mItemLoadHandlers.containsKey(position)) {// 给每一行分配一个handler
                    dlh = new DownLoadHandler();
                    mItemLoadHandlers.put(position, dlh);
                    dlh.setDownLoadButton(holder.downLoadButton);
                } else {
                    dlh = mItemLoadHandlers.get(position);
                    dlh.setDownLoadButton(holder.downLoadButton);
                }
                if (!mItemStopFlags.containsKey(position)) {
                    mItemStopFlags.put(position, false);
                }
                holder.downLoadButton.setOnDownLoadButtonClickListener(new DownLoadButton.OnDownLoadButtonClickListener() {
                    @Override
                    public void onClick(View view, int curState) {
                        switch (curState) {
                            case DownLoadButton.STATE_NORMAL:
                                holder.downLoadButton.setState(DownLoadButton.STATE_DOWNLOADING);
                                break;
                            case DownLoadButton.STATE_DOWNLOADING:
                                holder.downLoadButton.setState(DownLoadButton.STATE_STOPDOWNLOADING);
                                mItemStopFlags.put(position, true);
                                break;
                            case DownLoadButton.STATE_STOPDOWNLOADING:
                                holder.downLoadButton.setState(DownLoadButton.STATE_DOWNLOADING);
                                mItemStopFlags.put(position, false);
                                break;
                        }
                        if (downUrl != null) {
                            download(downUrl, position);
                        }
                    }
                });
                return convertView;
            }
        };
    }


    public static class Holder {
        public DownLoadButton downLoadButton;
        public TextView textView1, textView2;
        public ImageView imageView;
    }

    boolean isViewCreated = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        setEmptyText("没有游戏可下载");
        setListAdapter(adapter);
        setListShown(false);
        getListView().setOnItemClickListener(null);
        startLoadInner();
    }

    @Override
    public void onDestroyView() {
        isViewCreated = false;
        super.onDestroyView();
    }

    @Override
    public void setListShown(boolean shown) {
        if (isViewCreated) {
            super.setListShown(shown);
        }
    }


    private void startLoadInner() {
        setListShown(true);
        if (!isViewCreated) {
            return;
        }
        AVQuery<Games> query = AVQuery.getQuery("Games");
        query.findInBackground(new FindCallback<Games>() {
            @Override
            public void done(List<Games> list, AVException e) {
                if (e == null) {

                    adapter.addAll(list);
                }
            }
        });
    }

    private void download(final String path, final int position) {
        if (!mItemDownLoadThread.containsKey(position)) {
            DownLoadThread downLoadThread = new DownLoadThread();
            downLoadThread.setPath(path);
            downLoadThread.setPosition(position);
            downLoadThread.start();
            mItemDownLoadThread.put(position, downLoadThread);
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
            final FileDownloader loader = new FileDownloader(getActivity(), path, StorageUtils.getCacheDirectory(getActivity()), 3, mItemStopFlags.get(position));
            if (!mItemMaxProcess.containsKey(position)) {
                mItemMaxProcess.put(position, loader.getFileSize());
            }
            try {
                loader.download(new DownloadProgressListener() {
                    @Override
                    public void onDownloadSize(int size) {//实时获知文件已经下载的数据长度
                        int maxProcess = mItemMaxProcess.get(position);
                        float num = (float) size / (float) maxProcess;
                        int result = (int) (num * 100);
                        Message message = new Message();
                        message.obj = result;
                        message.what = MESSAGE_DOWNLOADING;
                        mItemLoadHandlers.get(position).sendMessage(message);
                    }
                });
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class DownLoadHandler extends Handler {
        private DownLoadButton downLoadButton;

        public void setDownLoadButton(DownLoadButton downLoadButton) {
            this.downLoadButton = downLoadButton;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DOWNLOADING:
                    if ((int) msg.obj >= 100) {
                        downLoadButton.setState(DownLoadButton.STATE_COMPLETE);
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DOWN_LOAD_COMPLETE_BROADCAST");
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    } else {
                        downLoadButton.setDownLoadProgress((int) msg.obj);
                    }
                    break;
            }
        }
    }
}