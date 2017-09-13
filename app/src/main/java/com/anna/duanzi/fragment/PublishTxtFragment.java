package com.anna.duanzi.fragment;
import android.view.View;
import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseFragment;
import butterknife.ButterKnife;

public class PublishTxtFragment extends BaseFragment {

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_publish_txt, null);
        ButterKnife.bind(view);
        return view;
    }

    @Override
    public void initData() {

    }


}
