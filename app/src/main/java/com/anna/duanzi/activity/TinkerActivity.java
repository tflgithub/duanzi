package com.anna.duanzi.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

public class TinkerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinker);
        TinkerInstaller.onReceiveUpgradePatch(this.getApplication(), Environment.getExternalStorageDirectory().getAbsolutePath()+"/patch.apk");
        Toast.makeText(this,"我是修改了Bug的Activity",Toast.LENGTH_SHORT).show();
    }
}
