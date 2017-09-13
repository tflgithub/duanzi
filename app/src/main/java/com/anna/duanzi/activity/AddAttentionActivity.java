package com.anna.duanzi.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.AddAttentionAdapter;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/1.
 */

public class AddAttentionActivity extends AppCompatActivity {

    TflListRecyclerView listRecyclerView;
    AddAttentionAdapter addAttentionAdapter;
    List<AVUser> users = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_attention);
        listRecyclerView = (TflListRecyclerView) findViewById(R.id.recycler_view);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listRecyclerView.setDivider(R.drawable.bottom_line);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("加关注");
        initData();
    }

    private void initData() {
        addAttentionAdapter = new AddAttentionAdapter(users, AddAttentionActivity.this);
        addAttentionAdapter.changeMode(TflListModel.MODE_LOADING);
        listRecyclerView.setAdapter(addAttentionAdapter);

        AVQuery<AVUser> userAVQuery = AVUser.getUserQuery(AVUser.class);
        userAVQuery.whereNotEqualTo("objectId", AVUser.getCurrentUser().getObjectId());
        userAVQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(final List<AVUser> allUsers, AVException avException) {
                if (avException == null) {
                    //查询已关注
                    AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
                    followeeQuery.findInBackground(new FindCallback<AVUser>() {
                        @Override
                        public void done(List<AVUser> followees, AVException avException) {
                            if (avException == null) {
                                if (followees.isEmpty()) {
                                    addAttentionAdapter.setData(allUsers);
                                } else {
                                    List<AVUser> newList = new ArrayList<>();
                                    for (AVUser user : allUsers) {
                                        for (AVUser followee : followees) {
                                            if (!user.getObjectId().equals(followee.getObjectId())) {
                                                newList.add(user);
                                            }
                                        }
                                    }
                                    addAttentionAdapter.setData(newList);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_actionbar_back:
                finish();
                break;
        }
    }
}
