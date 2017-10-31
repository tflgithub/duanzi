package com.anna.duanzi.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.DownLoadAreaActivity;
import com.anna.duanzi.domain.MoviesItem;
import com.cn.tfl.update.StorageUtils;

import java.io.File;


public class FragmentMyDownLoadMovies extends ListFragment {


    private ArrayAdapter<MoviesItem> adapter;
    LocalBroadcastManager broadcastManager;
    BroadcastReceiver mItemViewListClickReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DOWN_LOAD_COMPLETE_BROADCAST");
        mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startLoad();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);

        adapter = new ArrayAdapter<MoviesItem>(getActivity(), 0) {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.down_load_movies_item, null);
                }
                final MoviesItem item = getItem(position);
                TextView title = (TextView) convertView.findViewById(R.id.tv_title);
                title.setText(item.title);
                TextView play_btn = (TextView) convertView.findViewById(R.id.btn_play);
                play_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(Intent.ACTION_VIEW);
                        it.setDataAndType(Uri.parse(item.path), "video/MP4");
                        startActivity(it);
                    }
                });
                return convertView;
            }
        };
    }

    boolean isViewCreated = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        setEmptyText("没有下的载电影");
        setListAdapter(adapter);
        setListShown(false);
        getListView().setOnItemClickListener(null);
        startLoad();
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

    final Handler handler = new Handler();

    private void startLoad() {
        setListShown(true);
        if (!isViewCreated) {
            return;
        }
        if (adapter.getCount() > 0) {
            adapter.clear();
        }
        new Thread() {
            @Override
            public void run() {
                File file = StorageUtils.getCacheDirectory(getActivity());
                File[] files = file.listFiles();
                if (files != null) {
                    for (final File mp4 : files) {
                        if (mp4.exists() && mp4.getPath().toLowerCase().endsWith(".mp4")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    MoviesItem mp4Item = new MoviesItem(mp4.getName(), mp4.getAbsolutePath());
                                    adapter.add(mp4Item);
                                }
                            });
                        }
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
        super.onDestroy();
    }
}
