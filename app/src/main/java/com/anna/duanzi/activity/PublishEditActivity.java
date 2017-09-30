package com.anna.duanzi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.common.Constants;
import com.anna.duanzi.domain.Duanzi;
import com.anna.duanzi.domain.Image;
import com.anna.duanzi.fragment.PublishImageFragment;
import com.anna.duanzi.utils.UIUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishEditActivity extends BaseActivity implements UIUtils.DialogListener {

    @Bind(R.id.tv_publish)
    TextView tv_publish;
    @Bind(R.id.tv_cancel)
    TextView tv_cancel;
    @Bind(R.id.et_title)
    EditText et_title;
    public ArrayList<String> imageList = new ArrayList<>();
    PublishImageFragment publishImageFragment;
    private SVProgressHUD mSVProgressHUD;
    boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_edit);
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
        imageList = getIntent().getStringArrayListExtra("imageList");
        publishImageFragment = PublishImageFragment.newInstance(imageList);
        loadFragment(publishImageFragment);
    }

    public void loadFragment(BaseFragment fragment) {
        BaseFragment baseFragment = fragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_container, baseFragment, "default");
        ft.commit();
    }

    @OnClick({R.id.tv_publish, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_publish:
                publish();
                break;
            case R.id.tv_cancel:
                quit();
                break;
        }
    }

    private void publish() {
        final String title = et_title.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "请填写标题", Toast.LENGTH_SHORT).show();
            return;
        }
        AVFile imageFile = null;
        try {
            String firstPath = imageList.get(0);
            imageFile = AVFile.withAbsoluteLocalPath(title + ".png", firstPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final HashMap<String, View> map = publishImageFragment.viewMap;
        mSVProgressHUD.showWithStatus("提交中，请稍后...");
        final Duanzi duanzi = new Duanzi();
        duanzi.put("title", title);
        duanzi.put("area", "1");
        duanzi.put("category", Constants.CATEGORY_IMAGE);
        duanzi.put("image", imageFile);
        duanzi.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    String imageId = duanzi.objectId;
                    for (Map.Entry<String, View> entry : map.entrySet()) {
                        EditText et = (EditText) entry.getValue().findViewById(R.id.image_desc);
                        String desc = et.getText().toString();
                        AVFile imageFile = null;
                        try {
                            imageFile = AVFile.withAbsoluteLocalPath(desc.isEmpty() ? title : desc + ".png", entry.getKey());
                        } catch (FileNotFoundException e1) {
                            e1.printStackTrace();
                        }
                        Image image = new Image();
                        image.put("imageId", imageId);
                        image.put("imageFile", imageFile);
                        image.put("imageDec", desc);
                        image.saveInBackground();
                    }
                    isSuccess = true;
                    mSVProgressHUD.dismiss();
                }
            }
        });
    }

    private void quit() {
        UIUtils quitUtils = new UIUtils(this);
        quitUtils.setDialogListener(this);
        quitUtils.createConfirmDialog("确定退出?", "",
                "退出", "取消", UIUtils.DialogListener.LOGOUT);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                quit();
                break;
        }
        return false;
    }

    @Override
    public void disBack(int action) {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
