package com.anna.duanzi.fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.domain.Games;
import com.avos.avoscloud.AVQuery;
import com.cn.fodel.tfl_list_recycler_view.TflListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StandAloneFragment extends BaseFragment {
    @Bind(R.id.list)
    RecyclerView recyclerView;
    private TflListAdapter<Games> tflListAdapter;
    private AVQuery<Games> query;
    private List<Games> dataList = new ArrayList<>();



    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_item_list, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {

    }
}
