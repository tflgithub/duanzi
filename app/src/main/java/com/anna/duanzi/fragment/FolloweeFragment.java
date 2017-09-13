package com.anna.duanzi.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.AttentionAdapter;
import com.anna.duanzi.base.BaseFragment;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FolloweeFragment extends BaseFragment {

    @Bind(R.id.recycler_view)
    TflListRecyclerView listRecyclerView;
    AttentionAdapter attentionAdapter;
    List<AVUser> users = new ArrayList<>();

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_followee, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    public void initData() {
        listRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listRecyclerView.setDivider(R.drawable.bottom_line);
        attentionAdapter = new AttentionAdapter(users, getActivity());
        attentionAdapter.changeMode(TflListModel.MODE_LOADING);
        listRecyclerView.setAdapter(attentionAdapter);
        //查询关注者
        AVQuery<AVUser> followeeQuery = AVUser.followeeQuery(AVUser.getCurrentUser().getObjectId(), AVUser.class);
        followeeQuery.include("followee");
        followeeQuery.findInBackground(new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avObjects, AVException avException) {
                if (avException == null) {
                    attentionAdapter.setData(avObjects);
                }
            }
        });
    }
}
