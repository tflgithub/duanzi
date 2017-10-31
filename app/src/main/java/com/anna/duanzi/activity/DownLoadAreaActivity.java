package com.anna.duanzi.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.DownLoadMoviesAdapter;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.Constants;
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
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.drakeet.materialdialog.MaterialDialog;

public class DownLoadAreaActivity extends BaseActivity implements TflLoadMoreListener {

    @Bind(R.id.recycler_view)
    TflListRecyclerView mRecyclerView;
    private TflListAdapter<Movies> tflListAdapter;
    private List<Movies> dataList = new ArrayList<>();
    AVQuery<Movies> query;
    private int mTotalDataCount = 0;
    private boolean mLoadingLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_area);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText(getString(R.string.down_load_area));
        initData();
    }

    private void initData() {
        tflListAdapter = new DownLoadMoviesAdapter(dataList, this);
        mRecyclerView.setAdapter(tflListAdapter);
        mRecyclerView.setDivider(R.drawable.bottom_line);
        tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        query = AVObject.getQuery(Movies.class);
        query.whereEqualTo("category", Constants.MEMBER_AREA.CATEGORY_DOWNLOAD);
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
                            if (list.isEmpty()) {
                                mRecyclerView.setLayoutManager(new LinearLayoutManager(DownLoadAreaActivity.this));
                            } else {
                                mRecyclerView.setLayoutManager(new GridLayoutManager(DownLoadAreaActivity.this, 3));
                            }
                            tflListAdapter.changeMode(TflListModel.MODE_DATA);
                            tflListAdapter.setData(list);
                        }
                    }
                });
            }
        });
    }


    public void onClick(View v) {
        back();
    }

    private void back() {
        final Map<Integer, AsyncTask> taskMap = ((DownLoadMoviesAdapter) tflListAdapter).getTaskMap();
        if (taskMap.size() > 0) {
            final MaterialDialog materialDialog = new MaterialDialog(this);
            materialDialog.setTitle("温馨提示")
                    .setMessage("你有" + taskMap.size() + "部电影正在下载,确定要退出吗？")
                    .setPositiveButton("退出", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (Map.Entry<Integer, AsyncTask> entry : taskMap.entrySet()) {
                                entry.getValue().cancel(true);
                            }
                            taskMap.clear();
                            finish();
                            materialDialog.dismiss();
                        }
                    })
                    .setNegativeButton("取消",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                }
                            });
            materialDialog.show();
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        back();
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
