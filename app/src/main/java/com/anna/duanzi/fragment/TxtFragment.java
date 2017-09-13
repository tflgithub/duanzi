package com.anna.duanzi.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.TxtActivity;
import com.anna.duanzi.activity.WebTxtActivity;
import com.anna.duanzi.adapter.TxtAdapter;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.common.Constants;
import com.anna.duanzi.domain.Duanzi;
import com.avos.avoscloud.AVException;
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

public class TxtFragment extends BaseFragment implements TflLoadMoreListener {

    @Bind(R.id.recycler_view)
    TflListRecyclerView recyclerView;
    private TflListAdapter<Duanzi> tflListAdapter;
    private AVQuery<Duanzi> query;
    private List<Duanzi> dataList = new ArrayList<>();

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_txt, null);
        ButterKnife.bind(this, view);
        LinearLayoutManager mgr = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mgr);
        return view;
    }


    @Override
    public void initData() {
        tflListAdapter = new TxtAdapter(dataList, getActivity());
        tflListAdapter.setOnItemClickListener(new TflListInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object o) {
                Duanzi duanzi = (Duanzi) o;
                Intent intent = new Intent();
                intent.putExtra("id", duanzi.objectId);
                intent.putExtra("title", duanzi.title);
                intent.putExtra("content", duanzi.content);
                String subString = duanzi.content.substring(0, 4);
                if (subString.equals("http")) {
                    intent.putExtra("url", duanzi.content);
                    intent.setClass(getActivity(), WebTxtActivity.class);
                } else {
                    intent.setClass(getActivity(), TxtActivity.class);
                }
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(tflListAdapter);
        recyclerView.setDivider(R.drawable.bottom_line);
        recyclerView.enableAutoLoadMore(this);
        tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        query = AVObject.getQuery(Duanzi.class);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.whereEqualTo("category", Constants.CATEGORY_TXT);
        query.orderByDescending("createdAt");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                mTotalDataCount = i;
                query.limit(data_limit);
                query.findInBackground(new FindCallback<Duanzi>() {
                    @Override
                    public void done(List<Duanzi> list, AVException e) {
                        if (e == null) {
                            tflListAdapter.changeMode(TflListModel.MODE_DATA);
                            tflListAdapter.setData(list);
                        }
                    }
                });
            }
        });
    }

    private int mTotalDataCount = 0;
    private boolean mLoadingLock = false;

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
            query.findInBackground(new FindCallback<Duanzi>() {
                @Override
                public void done(List<Duanzi> list, AVException e) {
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


