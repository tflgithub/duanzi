package com.anna.duanzi.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.Constants;
import com.anna.duanzi.domain.Duanzi;
import com.anna.duanzi.fragment.PublishTxtFragment;
import com.anna.duanzi.fragment.PublishVedioFragment;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;

import java.io.FileNotFoundException;
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
    public static final int EDIT_VIDEO_REQUEST_CODE = 200;
    private String videoPath;
    private String imagePath;
    private SVProgressHUD mSVProgressHUD;
    boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        mSVProgressHUD = new SVProgressHUD(this);
        mSVProgressHUD.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                if (isSuccess) {
                    hud.showSuccessWithStatus("发布成功");
                    finish();
                } else {
                    hud.showErrorWithStatus("发布失败");
                }
            }
        });
        initView();
    }

    MultiImageSelectorFragment multiImageSelectorFragment;

    PublishVedioFragment publishVedioFragment;

    PublishTxtFragment publishTxtFragment;

    private void initView() {
        category = getIntent().getIntExtra(Constants.CATEGORY, -1);
        Fragment fragment = null;
        switch (category) {
            case Constants.PUBLISH.TXT:
                header_actionbar_title.setText("发段子");
                header_actionbar_action.setText("发布");
                fragment = new PublishTxtFragment();
                publishTxtFragment = (PublishTxtFragment) fragment;
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
                header_actionbar_action.setText("发布");
                videoPath = getIntent().getStringExtra("path");
                imagePath = getIntent().getStringExtra("imagePath");
                fragment = PublishVedioFragment.newInstance(videoPath, imagePath);
                publishVedioFragment = (PublishVedioFragment) fragment;
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
                    case Constants.PUBLISH.VEDIO:
                        publishVideo();
                        break;
                    case Constants.PUBLISH.TXT:
                        publishTxt();
                        break;
                }
                break;
            case R.id.header_actionbar_back:
                if (publishVedioFragment != null) {
                    publishVedioFragment.stop();
                }
                finish();
                break;
        }
    }

    private void publishTxt() {
        String title = publishTxtFragment.et_title.getText().toString();
        String content = publishTxtFragment.et_content.getText().toString();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "请填写标题和内容", Toast.LENGTH_SHORT).show();
            return;
        }
        mSVProgressHUD.showWithStatus("提交中，请稍后...");
        Duanzi duanzi = new Duanzi();
        duanzi.put("title", title);
        duanzi.put("content", content);
        duanzi.put("category", Constants.CATEGORY_TXT);
        duanzi.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    isSuccess = true;
                    mSVProgressHUD.dismiss();
                }
            }
        });
    }

    private void publishVideo() {
        String title = publishVedioFragment.editText.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "请填写标题", Toast.LENGTH_SHORT).show();
            return;
        }
        mSVProgressHUD.showWithStatus("提交中，请稍后...");
        final Duanzi duanzi = new Duanzi();
        duanzi.put("title", title);
        duanzi.put("area", "1");
        duanzi.put("category", Constants.CATEGORY_VIDEO);
        try {
            final AVFile videoFile = AVFile.withAbsoluteLocalPath(title + ".mp4", videoPath);
            final AVFile imageFile = AVFile.withAbsoluteLocalPath(title + ".png", imagePath);
            duanzi.put("image", imageFile);
            duanzi.put("data", videoFile);
            duanzi.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        isSuccess = true;
                        mSVProgressHUD.dismiss();
                    }
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            } else if (requestCode == EDIT_VIDEO_REQUEST_CODE) {
                publishVedioFragment.imageUrl = data.getStringExtra("imageUrl");
                publishVedioFragment.initData();
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
