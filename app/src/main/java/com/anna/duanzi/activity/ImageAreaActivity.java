package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.ImageAreaAdapter;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.domain.ImageArea;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflListInterface;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageAreaActivity extends BaseActivity {

    @Bind(R.id.recycler_view)
    TflListRecyclerView mRecyclerView;
    private TflListAdapter<ImageArea> tflListAdapter;
    private List<ImageArea> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_area_acitvity);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText(getString(R.string.image_area));
        initData();
    }

    private void initData() {
        tflListAdapter = new ImageAreaAdapter(dataList);
        tflListAdapter.setOnItemClickListener(new TflListInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object o) {
                ImageArea imageArea = (ImageArea) o;
                Intent intent = new Intent(ImageAreaActivity.this, ImagePageActivity.class);
                intent.putExtra("imageId", imageArea.objectId);
                intent.putExtra("imageTitle", imageArea.title);
                startActivity(intent);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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


    public void onClick(View view) {
        finish();
    }
}
