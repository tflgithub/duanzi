package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.avos.avoscloud.AVUser;

public class SettingActivity extends BaseActivity {

    TextView tv_vip_level;

    boolean isVip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("会员相关");
        tv_vip_level = (TextView) findViewById(R.id.tv_vip_level);
    }

    @Override
    protected void onResume() {
        String vip_level = AVUser.getCurrentUser().getString("vip");
        if (vip_level.equals("0")) {
            tv_vip_level.setText("普通用户");
        } else {
            tv_vip_level.setText(vip_level + "级会员");
            isVip = true;
        }
        super.onResume();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ry_member_area:
                if (isVip) {
                    Intent intent1 = new Intent(SettingActivity.this, MembersOnlyActivity.class);
                    startActivity(intent1);
                } else {
                    Toast.makeText(SettingActivity.this, "你还不是会员，不能进入专区", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ry_update_level:
                Intent intent2 = new Intent(SettingActivity.this, MemberActivity.class);
                startActivity(intent2);
                break;
            case R.id.header_actionbar_back:
                finish();
                break;
        }
    }
}
