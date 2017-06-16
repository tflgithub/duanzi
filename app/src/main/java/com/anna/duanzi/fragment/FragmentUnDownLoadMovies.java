package com.anna.duanzi.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.DownLoadMoviesAdapter;
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

public class FragmentUnDownLoadMovies extends BaseFragment implements TflLoadMoreListener {

    @Bind(R.id.recycler_view)
    TflListRecyclerView mRecyclerView;
    private TflListAdapter<Movies> tflListAdapter;
    private List<Movies> dataList = new ArrayList<>();
    AVQuery<Movies> query;
    private int mTotalDataCount = 0;
    private boolean mLoadingLock = false;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.recyclerview, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void initData() {
        tflListAdapter = new DownLoadMoviesAdapter(dataList, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(tflListAdapter);
        mRecyclerView.setDivider(R.drawable.bottom_line);
        tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        query = AVObject.getQuery(Movies.class);
        query.whereEqualTo("category", Contants.MEMBER_AREA.CATEGORY_DOWNLOAD);
        query.orderByDescending("createdAt");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                mTotalDataCount = i;
                query.limit(data_limit);
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
}
