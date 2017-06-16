package com.anna.duanzi.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;

/**
 * 选择头像
 * Created by tfl on 2015/4/7.
 */
public class SingleImageSelectorActivity extends BaseActivity implements MultiImageSelectorFragment.Callback {
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;

    private ArrayList<String> resultList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_seletor);
        Intent intent = getIntent();
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_SINGLE);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(me.nereo.multi_image_selector.R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

        // 返回按钮
        findViewById(R.id.header_actionbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("设置头像");
    }

    @Override
    public void onSingleImageSelected(Uri uri) {
        Intent intent = new Intent();
        //resultList.add(path);
        intent.setData(uri);
        //intent.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
    }

    @Override
    public void onImageUnselected(String path) {
    }

    @Override
    public void onCameraShot(Uri uri) {
//        if (imageFile != null) {
//            Intent data = new Intent();
//            resultList.add(imageFile.getAbsolutePath());
//            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
//            setResult(RESULT_OK, data);
//            finish();
//        }

        Intent intent = new Intent();
        //resultList.add(path);
        intent.setData(uri);
        //intent.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, intent);
        finish();
    }
}
