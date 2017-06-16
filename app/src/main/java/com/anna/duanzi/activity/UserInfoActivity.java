package com.anna.duanzi.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.common.Contants;
import com.anna.duanzi.utils.StringUtils;
import com.anna.duanzi.utils.UIUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;

import me.drakeet.materialdialog.MaterialDialog;
import me.nereo.multi_image_selector.camera.utils.BitmapUtils;

public class UserInfoActivity extends BaseActivity implements UIUtils.DialogListener {

    private ImageView iv_head;
    private TextView tv_nick_name, tv_email, tv_sex, tv_mobile;
    MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.header_actionbar_title)).setText("账号管理");
        iv_head = (ImageView) findViewById(R.id.iv_head);
        tv_nick_name = (TextView) findViewById(R.id.tv_nick_name);
        tv_nick_name.setText(AVUser.getCurrentUser().getString("nickName"));
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_email.setText(AVUser.getCurrentUser().getEmail());
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_sex.setText(AVUser.getCurrentUser().getString("sex"));
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_mobile.setText(StringUtils.getMobile(AVUser.getCurrentUser().getMobilePhoneNumber()));
        Glide.with(this).load(AVUser.getCurrentUser().getString("headImage")).placeholder(R.drawable.default_round_head).into(iv_head);
    }


    private static final int REQUEST_IMAGE = 2;

    public static final int REQUEST_NICK_NAME = 100;
    public static final int REQUEST_EMAIL = 200;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.header_actionbar_back: {
                finish();
            }
            break;
            case R.id.tv_quit:
                UIUtils quitUtils = new UIUtils(this);
                quitUtils.setDialogListener(this);
                quitUtils.createConfirmDialog("退出登录", "退出登录将不能观看视频和浏览图片以及发表评论",
                        "确定退出", "取消", UIUtils.DialogListener.LOGOUT);
                break;
            case R.id.iv_head:
                Intent intent = new Intent(this, SingleImageSelectorActivity.class);
                // whether show camera
                intent.putExtra(SingleImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // select mode (SingleImageSelectorActivity.MODE_SINGLE OR SingleImageSelectorActivity.MODE_MULTI)
                intent.putExtra(SingleImageSelectorActivity.EXTRA_SELECT_MODE, SingleImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
                break;
            case R.id.ry_update_nick_name:
                Intent nIntent = new Intent(UserInfoActivity.this, EditUserInfoActivity.class);
                nIntent.putExtra(Contants.UPDATE_USERINFO_TYPE, Contants.UPDATE_USERINFO.NICK_NAME);
                nIntent.putExtra(Contants.UPDATE_USERINFO_CONTENT, tv_nick_name.getText().toString());
                startActivityForResult(nIntent, REQUEST_NICK_NAME);
                break;
            case R.id.ry_update_email:
                Intent eIntent = new Intent(UserInfoActivity.this, EditUserInfoActivity.class);
                eIntent.putExtra(Contants.UPDATE_USERINFO_TYPE, Contants.UPDATE_USERINFO.EMAIL);
                eIntent.putExtra(Contants.UPDATE_USERINFO_CONTENT, tv_email.getText().toString());
                startActivityForResult(eIntent, REQUEST_EMAIL);
                break;
            case R.id.ry_update_sex:
                setSex();
                break;
            case R.id.tv_update_password:
                UIUtils uIUtils = new UIUtils(this);
                uIUtils.setDialogListener(this);
                uIUtils.createConfirmDialog("修改登录密码", "将给手机" + tv_mobile.getText().toString()
                        + "发送验证码", "确定", "取消", UIUtils.DialogListener.UPDATE_PASSWORD);
                break;
            case R.id.ry_update_mobile:
                Intent mobileIntent = new Intent(UserInfoActivity.this, EditUserInfoActivity.class);
                mobileIntent.putExtra(Contants.UPDATE_USERINFO_TYPE, Contants.UPDATE_USERINFO.MOBILE);
                startActivity(mobileIntent);
                break;
        }
    }


    private void setSex() {
        materialDialog = new MaterialDialog(this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.radio_sex,
                        null);
        RadioGroup genderGroup = (RadioGroup) view.findViewById(R.id.sex_rg);
        RadioButton man = (RadioButton) view.findViewById(R.id.rb_man);
        RadioButton woman = (RadioButton) view.findViewById(R.id.rb_woman);
        if (tv_sex.getText().toString().equals("男")) {
            man.setChecked(true);
        } else {
            woman.setChecked(true);
        }
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_man) {
                    tv_sex.setText("男");
                } else if (checkedId == R.id.rb_woman) {
                    tv_sex.setText("女");
                }
                AVUser.getCurrentUser().put("sex", tv_sex.getText().toString());
                AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        materialDialog.dismiss();
                    }
                });
            }
        });
        materialDialog.setTitle(
                "性别").setContentView(view);
        materialDialog.setPositiveButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_IMAGE:
                final Uri uri = data.getData();
                if (uri == null) {
                    return;
                }
                upLoadUserHeadImage(uri);
                break;
            case REQUEST_NICK_NAME:
                tv_nick_name.setText(data.getStringExtra("update_content"));
                break;
            case REQUEST_EMAIL:
                tv_email.setText(data.getStringExtra("update_content"));
                break;
        }
    }

    //上传用户头像
    private void upLoadUserHeadImage(final Uri uri) {
        AVFile avFile = null;
        final String path = BitmapUtils.getRealFilePathFromUri(getApplicationContext(), uri);
        try {
            avFile = AVFile.withFile(AVUser.getCurrentUser().getUsername() + ".png", new File(path));
            final AVFile finalAvFile = avFile;
            avFile.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    //Log.d(TAG, finalAvFile.getUrl());//返回一个唯一的 Url 地址
                    AVUser.getCurrentUser().put("headImage", finalAvFile.getUrl());
                    AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            upLoadOver(path);
                        }
                    });
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void upLoadOver(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        iv_head.setImageBitmap(bitmap);
    }

    @Override
    public void disBack(int action) {
        switch (action) {
            case UIUtils.DialogListener.UPDATE_PASSWORD:
                Intent mIntent = new Intent(UserInfoActivity.this, EditUserInfoActivity.class);
                mIntent.putExtra(Contants.UPDATE_USERINFO_TYPE, Contants.UPDATE_USERINFO.PASSWORD);
                mIntent.putExtra(Contants.UPDATE_USERINFO_CONTENT, AVUser.getCurrentUser().getMobilePhoneNumber());
                startActivity(mIntent);
                break;
            case UIUtils.DialogListener.LOGOUT:
                AVUser.logOut();
                finish();
                break;
        }
    }
}
