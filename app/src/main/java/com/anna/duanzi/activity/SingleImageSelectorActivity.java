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

import java.io.File;

import me.nereo.multi_image_selector.MultiImageSelectorFragment;

/**
 * 选择头像
 * Created by tfl on 2015/4/7.
 */
public class SingleImageSelectorActivity extends BaseActivity implements MultiImageSelectorFragment.Callback {

    MultiImageSelectorFragment multiImageSelectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_seletor);
        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, MultiImageSelectorFragment.MODE_SINGLE);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, true);
        multiImageSelectorFragment = (MultiImageSelectorFragment) Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle);
        getSupportFragmentManager().beginTransaction()
                .add(me.nereo.multi_image_selector.R.id.image_grid, multiImageSelectorFragment)
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
        intent.setData(uri);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onImageSelected(String path) {

    }

    @Override
    public void onImageUnselected(String path) {
    }

    @Override
    public void onCameraShot(String path) {
        Uri uri = Uri.fromFile(new File(path));
        multiImageSelectorFragment.gotoClipActivity(uri);
    }
}
