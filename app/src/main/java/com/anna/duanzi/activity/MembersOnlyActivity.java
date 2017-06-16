package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;

public class MembersOnlyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_only);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("会员专区");
    }


    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.header_actionbar_back:
                finish();
                break;
            case R.id.btn_online_area:
                intent.setClass(MembersOnlyActivity.this, OnlineMoviesActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_down_load_area:
                intent.setClass(MembersOnlyActivity.this, DownLoadAreaActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_image_area:
                intent.setClass(MembersOnlyActivity.this, ImageAreaActivity.class);
                startActivity(intent);
                break;
        }
    }
}
