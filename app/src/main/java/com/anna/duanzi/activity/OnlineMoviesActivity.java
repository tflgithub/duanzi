package com.anna.duanzi.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.OnlineMoviesAdapter;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.Contants;
import com.anna.duanzi.domain.Movies;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;
import com.cn.fodel.tfl_list_recycler_view.TflLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Created by tfl on 2016/11/21.
 */
public class OnlineMoviesActivity extends BaseActivity implements TflLoadMoreListener {

    @Bind(R.id.recycler_view)
    TflListRecyclerView mRecyclerView;
    private TflListAdapter<Movies> tflListAdapter;
    private List<Movies> dataList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_movies);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText(getString(R.string.online_area));
        initData();
    }

    private void initData() {
        tflListAdapter = new OnlineMoviesAdapter(dataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(tflListAdapter);
        mRecyclerView.setDivider(R.drawable.bottom_line);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10));
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
            query.findInBackground(new FindCallback<Movies>() {
                @Override
                public void done(List<Movies> list, AVException e) {
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
        }
    }


    AVQuery<Movies> query;
    private int mTotalDataCount = 0;
    private boolean mLoadingLock = false;

    private void loadData() {
        tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        query = AVObject.getQuery(Movies.class);
        query.whereEqualTo("category", Contants.MEMBER_AREA.CATEGORY_ONLINE);
        query.limit(data_limit);
        query.orderByDescending("createdAt");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                mTotalDataCount = i;
                query.findInBackground(new FindCallback<Movies>() {
                    @Override
                    public void done(List<Movies> list, AVException e) {
                        if (e == null) {
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
