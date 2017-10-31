package com.anna.duanzi.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.activity.LoginActivity;
import com.anna.duanzi.activity.MemberActivity;
import com.anna.duanzi.activity.TurnBookActivity;
import com.anna.duanzi.adapter.FicitonAdapter;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.common.Constants;
import com.anna.duanzi.domain.Fiction;
import com.anna.duanzi.utils.ContextUtils;
import com.anna.duanzi.utils.FileUtils;
import com.anna.duanzi.utils.UIUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;
import com.cn.fodel.tfl_list_recycler_view.TflListInterface;
import com.cn.fodel.tfl_list_recycler_view.TflListModel;
import com.cn.fodel.tfl_list_recycler_view.TflListRecyclerView;
import com.cn.fodel.tfl_list_recycler_view.TflLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FictionFragment extends BaseFragment implements UIUtils.DialogListener,TflLoadMoreListener{

    @Bind(R.id.recycler_view)
    TflListRecyclerView recyclerView;
    private TflListAdapter<Fiction> tflListAdapter;
    private AVQuery<Fiction> query;
    private List<Fiction> dataList = new ArrayList<>();

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_txt, null);
        ButterKnife.bind(this, view);
        GridLayoutManager mgr = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mgr);
        recyclerView.addItemDecoration(new SpaceItemDecoration(22));
        return view;
    }

    @Override
    public void initData() {
        data_skip = 0;
        tflListAdapter = new FicitonAdapter(dataList, getActivity());
        //tflListAdapter.changeMode(TflListModel.MODE_LOADING);
        recyclerView.setAdapter(tflListAdapter);
        recyclerView.setDivider(R.drawable.bottom_line);
        recyclerView.enableAutoLoadMore(this);
        query = AVObject.getQuery(Fiction.class);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.limit(data_limit);
        mSVProgressHUD.showWithProgress(getString(R.string.loading), SVProgressHUD.SVProgressHUDMaskType.Black);
        query.findInBackground(new FindCallback<Fiction>() {
            @Override
            public void done(List<Fiction> list, AVException e) {
                mSVProgressHUD.dismiss();
                tflListAdapter.changeMode(TflListModel.MODE_DATA);
                tflListAdapter.setData(list);
            }
        });
        tflListAdapter.setOnItemClickListener(new TflListInterface.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object o) {
                final Fiction fiction = (Fiction) o;
                if (!isLogin) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    return;
                }
                if (Constants.MEMBER.MEMBER_LEVEL_0.equals(AVUser.getCurrentUser().getString("vip"))) {
                    UIUtils uiUtils = new UIUtils(getActivity());
                    uiUtils.setDialogListener(FictionFragment.this);
                    uiUtils.createConfirmDialog("温馨提示", "您现在还不是会员，去升级为会员", "立马就去", "我再想想", 0);
                    return;
                }
                final Intent intent = new Intent(getActivity(), TurnBookActivity.class);
                intent.putExtra("name", fiction.name);
                FileUtils.getInstance().setFileCacheRoot(ContextUtils.getContext().getExternalCacheDir().getAbsolutePath() + "/TXT_FILE");
                if (FileUtils.getInstance().isFileExist(FileUtils.getInstance().getFileCacheRoot(), fiction.name)) {
                    startActivity(intent);
                } else {
                    AVFile file = fiction.getAVFile("bookFile");
                    if (file == null) {
                        Toast.makeText(getActivity(), "服务端无数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSVProgressHUD.showWithProgress("加载中...", SVProgressHUD.SVProgressHUDMaskType.Black);
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, AVException e) {
                            // bytes 就是文件的数据流
                            FileUtils.getInstance().write2SD(FileUtils.getInstance().getFileCacheRoot(), fiction.name, bytes);
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer integer) {
                            // 上传进度数据，integer 介于 0 和 100。
                            if (integer == 100) {
                                mSVProgressHUD.dismiss();
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void disBack(int action) {
        Intent intent = new Intent(getActivity(), MemberActivity.class);
        startActivity(intent);
    }

    @Override
    public void loadMore() {
        data_skip = data_skip + data_limit;
        query.skip(data_skip);
        query.limit(data_limit);
        query.findInBackground(new FindCallback<Fiction>() {
            @Override
            public void done(List<Fiction> list, AVException e) {
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        tflListAdapter.addData(list);
                    }
                }
            }
        });
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
