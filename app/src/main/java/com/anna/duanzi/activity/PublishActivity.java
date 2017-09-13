package com.anna.duanzi.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.Constants;
import com.anna.duanzi.fragment.PublishTxtFragment;
import com.anna.duanzi.fragment.PublishVedioFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;

public class PublishActivity extends BaseActivity implements MultiImageSelectorFragment.Callback {

    @Bind(R.id.header_actionbar_title)
    TextView header_actionbar_title;
    @Bind(R.id.header_actionbar_action)
    TextView header_actionbar_action;
    private ArrayList<String> resultList = new ArrayList<>();
    int category;
    public static final int EDIT_IMAGE_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        initView();
    }

    MultiImageSelectorFragment multiImageSelectorFragment;

    private void initView() {
        category = getIntent().getIntExtra(Constants.CATEGORY, -1);
        Fragment fragment = null;
        switch (category) {
            case Constants.PUBLISH.TXT:
                header_actionbar_title.setText("发段子");
                header_actionbar_action.setText("发布");
                fragment = new PublishTxtFragment();
                break;
            case Constants.PUBLISH.IMAGE:
                header_actionbar_action.setEnabled(false);
                header_actionbar_title.setText("发图片");
                header_actionbar_action.setText("确定");
                Bundle bundle = new Bundle();
                bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, 50);
                bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, MultiImageSelectorFragment.MODE_MULTI);
                bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, true);
                fragment = Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle);
                multiImageSelectorFragment = (MultiImageSelectorFragment) fragment;
                break;
            case Constants.PUBLISH.VEDIO:
                header_actionbar_title.setText("发视频");
                fragment = new PublishVedioFragment();
                break;
        }
        loadFragment(fragment);
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    @OnClick({R.id.header_actionbar_action, R.id.header_actionbar_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_actionbar_action:
                switch (category) {
                    case Constants.PUBLISH.IMAGE:
                        Intent intent = new Intent(this, PublishEditActivity.class);
                        intent.putExtra("imageList", resultList);
                        startActivityForResult(intent, EDIT_IMAGE_REQUEST_CODE);
                        break;
                }
                break;
            case R.id.header_actionbar_back:
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_IMAGE_REQUEST_CODE) {
                resultList.removeAll(data.getStringArrayListExtra("imageList"));
                if (resultList.isEmpty()) {
                    header_actionbar_action.setEnabled(false);
                    header_actionbar_action.setText("确定");
                } else {
                    header_actionbar_action.setText("确定" + "(" + resultList.size() + ")");
                }
                multiImageSelectorFragment.reSetSelected(resultList, data.getStringArrayListExtra("imageList"));
            }
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    public void onSingleImageSelected(Uri uri) {

    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        header_actionbar_action.setEnabled(true);
        header_actionbar_action.setText("确定" + "(" + resultList.size() + ")");
    }

    @Override
    public void onImageUnselected(String path) {
        resultList.remove(path);
        if (resultList.isEmpty()) {
            header_actionbar_action.setEnabled(false);
            header_actionbar_action.setText("确定");
        } else {
            header_actionbar_action.setText("确定" + "(" + resultList.size() + ")");
        }
    }

    @Override
    public void onCameraShot(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        Intent intent = new Intent(this, PublishEditActivity.class);
        intent.putExtra("imageList", resultList);
        startActivityForResult(intent, EDIT_IMAGE_REQUEST_CODE);
    }
}
