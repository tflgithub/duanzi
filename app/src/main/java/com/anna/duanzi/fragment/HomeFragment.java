package com.anna.duanzi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.ImagePageActivity;
import com.anna.duanzi.activity.LoginActivity;
import com.anna.duanzi.activity.VideoActivity;
import com.anna.duanzi.adapter.ImageAdapter;
import com.anna.duanzi.adapter.TabAdapter;
import com.anna.duanzi.adapter.VideoAdapter;
import com.anna.duanzi.common.Contants;
import com.anna.duanzi.common.HttpHelper;
import com.anna.duanzi.domain.Area;
import com.anna.duanzi.domain.Duanzi;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
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
 * Created by tfl on 2016/1/22.
 */
public class HomeFragment extends BaseFragment implements TflLoadMoreListener {

    @Bind(R.id.rv_head)
    RecyclerView rv_head;
    @Bind(R.id.recycler_view)
    TflListRecyclerView mRecyclerView;
    private TflListAdapter<Duanzi> tflListAdapter;
    private List<Duanzi> dataList = new ArrayList<>();

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /**
     * fragment与activity产生关联是  回调这个方法
     */


    @Override
    public void initData() {
        category = getArguments().getString(Contants.CATEGORY);
        loadArea();
        switch (category) {
            case Contants.CATEGORY_IMAGE:
                tflListAdapter = new ImageAdapter(dataList, getActivity());
                tflListAdapter.setOnItemClickListener(new TflListInterface.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Object o) {
                        if (AVUser.getCurrentUser() == null) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Duanzi duanzi = (Duanzi) o;
                            Intent intent = new Intent(getActivity(), ImagePageActivity.class);
                            intent.putExtra("imageId", duanzi.objectId);
                            startActivity(intent);
                        }
                    }
                });
                break;
            case Contants.CATEGORY_VIDEO:
                tflListAdapter = new VideoAdapter(dataList);
                tflListAdapter.setOnItemClickListener(new TflListInterface.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Object o) {
                        if (AVUser.getCurrentUser() == null) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            VideoAdapter.DataViewHolder videoViewHolder = (VideoAdapter.DataViewHolder) o;
                            Intent intent = new Intent(getActivity(), VideoActivity.class);
                            int position = videoViewHolder.getAdapterPosition();
                            Duanzi duanzi = dataList.get(position);
                            int CURRENT_STATE = videoViewHolder.jcVideoPlayer.CURRENT_STATE;
                            intent.putExtra("current_state", CURRENT_STATE);
                            intent.putExtra("videoId", duanzi.objectId);
                            AVFile videoFile = duanzi.getAVFile("data");
                            AVFile videoImageFile = duanzi.getAVFile("image");
                            intent.putExtra("videoUrl", videoFile.getUrl());
                            intent.putExtra("imageUrl", videoImageFile.getUrl());
                            intent.putExtra("title", duanzi.title);
                            JCMediaManager.intance().mediaPlayer.pause();
                            videoViewHolder.jcVideoPlayer.isClickFullscreen = true;
                            JCMediaManager.intance().mediaPlayer.setDisplay(null);
                            JCMediaManager.intance().backUpUuid();
                            startActivity(intent);
                        }
                    }
                });
                break;
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(tflListAdapter);
        mRecyclerView.setDivider(R.drawable.bottom_line);

        if (area_list != null && area_list.size() > 0) {
            load();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (category.equals(Contants.CATEGORY_VIDEO)) {
            JCVideoPlayer.releaseAllVideos();
        }
    }

    private void load() {
        TabAdapter tabAdapter = new TabAdapter(area_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_head.setLayoutManager(linearLayoutManager);
        rv_head.setAdapter(tabAdapter);
        loadData(area_list.get(0).areaId, category);
        tabAdapter.setOnItemClickListener(new TabAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                resetDataSkip();//重新切换的地区的时候需要重置。
                loadData(area_list.get(position).areaId, category);
            }
        });
    }

    AVQuery<Duanzi> query;
    private int mTotalDataCount = 0;
    private boolean mLoadingLock = false;

    private void loadData(String mParam1, String mParam2) {
        tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        query = AVObject.getQuery(Duanzi.class);
        query.whereEqualTo("area", mParam1);
        query.whereEqualTo("category", mParam2);
        query.limit(data_limit);
        query.orderByDescending("createdAt");
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                mTotalDataCount = i;
                HttpHelper.getInstance().getData(query, new HttpHelper.Callback() {
                    @Override
                    public void onSuccess(List<?> responseInfo) {
                        dataList = (List<Duanzi>) responseInfo;
                        tflListAdapter.changeMode(TflListModel.MODE_DATA);
                        tflListAdapter.setData(dataList);
                    }

                    @Override
                    public void onFailure() {
                        tflListAdapter.changeMode(TflListModel.MODE_EMPTY);
                    }
                });
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            //fragment可见时,需要将忽略的数据重置，不然滑动几次会加载不到数据了。
            resetDataSkip();
            if (area_list != null && area_list.size() > 0) {
                load();
            }
        } else {
            //fragment不可见时不执行操作
        }
        super.setUserVisibleHint(isVisibleToUser);
    }


    List<Area> area_list;

    private void loadArea() {
        HttpHelper.getInstance().getAreas(new HttpHelper.Callback() {
            @Override
            public void onSuccess(List<?> responseInfo) {
                rv_head.setVisibility(View.VISIBLE);
                area_list = (List<Area>) responseInfo;
                load();
            }

            @Override
            public void onFailure() {

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
