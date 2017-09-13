package com.anna.duanzi.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.FollowerAdapter;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.domain.User;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFriendship;
import com.avos.avoscloud.AVFriendshipQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.callback.AVFriendshipCallback;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FollowerFragment extends BaseFragment {
    @Bind(R.id.recycler_view)
    TflListRecyclerView listRecyclerView;
    FollowerAdapter attentionAdapter;
    List<User> users = new ArrayList<>();

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_follower, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        listRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listRecyclerView.setDivider(R.drawable.bottom_line);
        attentionAdapter = new FollowerAdapter(users, getActivity());
        attentionAdapter.changeMode(TflListModel.MODE_LOADING);
        listRecyclerView.setAdapter(attentionAdapter);
        AVFriendshipQuery query = AVUser.friendshipQuery(AVUser.getCurrentUser().getObjectId(), User.class);
        query.include("followee");
        query.include("follower");
        query.getInBackground(new AVFriendshipCallback() {
            @Override
            public void done(AVFriendship friendship, AVException e) {
                List<User> followers = friendship.getFollowers(); //获取粉丝
                List<User> followees = friendship.getFollowees(); //获取关注列表
                for (User follower : followers) {
                    for (User followee : followees) {
                        if (follower.getObjectId().equals(followee.getObjectId())) {
                            follower.isAttentioned = true;
                        }
                    }
                }
                attentionAdapter.setData(followers);
            }
        });
    }
}
