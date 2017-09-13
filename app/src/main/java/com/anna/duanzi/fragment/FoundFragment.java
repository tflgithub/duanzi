package com.anna.duanzi.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tfl on 2016/1/22.
 */
public class FoundFragment extends BaseFragment {

    @Bind(R.id.header_actionbar_title)
    TextView title;
    @Bind(R.id.header_actionbar_back)
    ImageView back;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_found, null);
        ButterKnife.bind(this, view);
        back.setVisibility(View.GONE);
        title.setText("发现");
        return view;
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tv_trends,R.id.tv_transport})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_transport:
                break;
            case R.id.tv_trends:
                break;
        }
    }
}
