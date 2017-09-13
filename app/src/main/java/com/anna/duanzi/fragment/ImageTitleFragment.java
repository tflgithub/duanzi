package com.anna.duanzi.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.ImageAreaAdapter;
import com.anna.duanzi.domain.ImageArea;
import com.anna.duanzi.utils.ContextUtils;
import com.anna.duanzi.utils.FileUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflListInterface;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageTitleFragment extends BackHandledFragment {

    @Bind(R.id.recycler_view)
    TflListRecyclerView mRecyclerView;
    private TflListAdapter<ImageArea> tflListAdapter;
    private List<ImageArea> dataList = new ArrayList<>();

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_image_title, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        tflListAdapter = new ImageAreaAdapter(dataList);
        tflListAdapter.setOnItemClickListener(new TflListInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object o) {
                final ImageArea imageArea = (ImageArea) o;
                FileUtils.getInstance().setFileCacheRoot(ContextUtils.getContext().getExternalCacheDir().getAbsolutePath() + "/IMAGE_AREA");
                mSVProgressHUD.showWithProgress("加载中...", SVProgressHUD.SVProgressHUDMaskType.Black);
                //下载图片压缩包
                imageArea.getAVFile("zip").getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, AVException e) {
                        FileUtils.getInstance().write2SD(FileUtils.getInstance().getFileCacheRoot(), imageArea.title + ".zip", bytes);
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer integer) {
                        if (integer == 100) {
                            mSVProgressHUD.dismiss();
                            toImageFragment(imageArea.title);
                        }
                    }
                });
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(tflListAdapter);
        mRecyclerView.setDivider(R.drawable.bottom_line);
        loadData();
    }

    private void loadData() {
        tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        AVQuery<ImageArea> query = AVObject.getQuery(ImageArea.class);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ImageArea>() {
            @Override
            public void done(List<ImageArea> list, AVException e) {
                if (e == null) {
                    tflListAdapter.changeMode(TflListModel.MODE_DATA);
                    tflListAdapter.setData(list);
                }
            }
        });
    }

    private void toImageFragment(String title) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        //设置替换和退栈的动画
        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_in, R.anim.back_left_in, R.anim.back_right_out);
        ImageFragment imageFragment = (ImageFragment) manager.findFragmentByTag("image");
        if (imageFragment == null) {
            imageFragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            imageFragment.setArguments(bundle);
        }
        ft.replace(R.id.fl_container, imageFragment, "image");
        ft.addToBackStack("tag");
        ft.commit();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
