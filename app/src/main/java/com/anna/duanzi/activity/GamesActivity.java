package com.anna.duanzi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;

import butterknife.ButterKnife;
public class GamesActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        ButterKnife.bind(this);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("游戏");
        findViewById(R.id.header_actionbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
