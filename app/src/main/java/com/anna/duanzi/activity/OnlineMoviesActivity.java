package com.anna.duanzi.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.OnlineMoviesAdapter;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.domain.WebMovie;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflListInterface;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;
import com.cn.fodel.tfl_list_recycler_view.TflLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCMediaManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by tfl on 2016/11/21.
 */
public class OnlineMoviesActivity extends BaseActivity implements TflLoadMoreListener {

    @Bind(R.id.recycler_view)
    TflListRecyclerView mRecyclerView;
    private TflListAdapter<WebMovie> tflListAdapter;
    private List<WebMovie> dataList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_movies);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText(getString(R.string.online_area));
        initData();
    }

    private void initData() {
        tflListAdapter = new OnlineMoviesAdapter(dataList,mContext);
        tflListAdapter.setOnItemClickListener(new TflListInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object o) {
                OnlineMoviesAdapter.DataViewHolder videoViewHolder = (OnlineMoviesAdapter.DataViewHolder) o;
                Intent intent = new Intent(OnlineMoviesActivity.this, VideoActivity.class);
                int position = videoViewHolder.getAdapterPosition();
                WebMovie movie = dataList.get(position);
                int CURRENT_STATE = videoViewHolder.jcVideoPlayer.CURRENT_STATE;
                intent.putExtra("current_state", CURRENT_STATE);
                intent.putExtra("videoId", movie.objectId);
                AVFile videoImageFile = movie.getAVFile("thumbImage");
                intent.putExtra("videoUrl", movie.url);
                intent.putExtra("imageUrl", videoImageFile.getUrl());
                intent.putExtra("title", movie.title);
                JCMediaManager.intance().mediaPlayer.pause();
                videoViewHolder.jcVideoPlayer.isClickFullscreen = true;
                JCMediaManager.intance().mediaPlayer.setDisplay(null);
                JCMediaManager.intance().backUpUuid();
                startActivity(intent);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(tflListAdapter);
        mRecyclerView.setDivider(R.drawable.bottom_line);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10));
        mRecyclerView.enableAutoLoadMore(this);
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public void loadMore() {
        if (mLoadingLock) {
            return;
        }
        if (tflListAdapter.getData().size() < mTotalDataCount && tflListAdapter.getData().size() > 0) {
            mLoadingLock = true;
            if (!tflListAdapter.getFooters().contains(getString(R.string.loading))) {
                tflListAdapter.addFooter(getString(R.string.loading));
            }
            data_skip = data_skip + data_limit;
            query.skip(data_skip);
            query.findInBackground(new FindCallback<WebMovie>() {
                @Override
                public void done(List<WebMovie> list, AVException e) {
                    if (list != null && list.size() > 0) {
                        tflListAdapter.addData(list);
                        mLoadingLock = false;
                    }
                }
            });
        } else {
            // no more
            if (tflListAdapter.getFooters().contains(getString(R.string.loading))) {
                tflListAdapter.removeFooter(getString(R.string.loading));
            }
            tflListAdapter.addFooter("没有更多的内容了...");
        }
    }


    AVQuery<WebMovie> query;
    private int mTotalDataCount = 0;
    private boolean mLoadingLock = false;

    private void loadData() {
        tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        query = AVObject.getQuery(WebMovie.class);
        query.limit(data_limit);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                mTotalDataCount = i;
                query.orderByDescending("createdAt");
                query.findInBackground(new FindCallback<WebMovie>() {
                    @Override
                    public void done(List<WebMovie> list, AVException e) {
                        if (e == null) {
                            dataList=list;
                            tflListAdapter.changeMode(TflListModel.MODE_DATA);
                            tflListAdapter.setData(list);
                        }
                    }
                });
            }
        });
    }


    public void onClick(View view) {
        finish();
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //不是第一个的格子都设一个左边和底部的间距
            outRect.left = space;
            outRect.bottom = space;
            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
            if (parent.getChildLayoutPosition(view) % 3 == 0) {
                outRect.left = 0;
            }
        }
    }
}
