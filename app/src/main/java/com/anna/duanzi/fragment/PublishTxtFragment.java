package com.anna.duanzi.fragment;

import android.view.View;
import android.widget.EditText;
import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseFragment;

public class PublishTxtFragment extends BaseFragment {


    public EditText et_title, et_content;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_publish_txt, null);
        et_title = (EditText) view.findViewById(R.id.et_title);
        et_content = (EditText) view.findViewById(R.id.et_content);
        return view;
    }

    @Override
    public void initData() {

    }


}
