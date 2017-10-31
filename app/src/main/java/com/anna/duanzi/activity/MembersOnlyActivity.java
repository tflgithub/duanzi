package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.utils.AdPreferences;

import net.youmi.android.nm.cm.ErrorCode;
import net.youmi.android.nm.vdo.VideoAdListener;
import net.youmi.android.nm.vdo.VideoAdManager;
import net.youmi.android.nm.vdo.VideoAdSettings;

public class MembersOnlyActivity extends BaseActivity {

    private static final String TAG = MembersOnlyActivity.class.getSimpleName();

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
                setUpVideoAd();
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

    private void setUpVideoAd() {
        if (AdPreferences.getInstance().getVideoAd()) {
            final VideoAdSettings videoAdSettings = new VideoAdSettings();
            // 展示视频广告
            VideoAdManager.getInstance(mContext)
                    .showVideoAd(mContext, videoAdSettings, new VideoAdListener() {
                        @Override
                        public void onPlayStarted() {
                            Log.d(TAG, "开始播放视频");
                        }

                        @Override
                        public void onPlayInterrupted() {
                            Intent intent = new Intent();
                            intent.setClass(MembersOnlyActivity.this, OnlineMoviesActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onPlayFailed(int errorCode) {
                            Log.e(TAG, "视频播放失败");
                            switch (errorCode) {
                                case ErrorCode.NON_NETWORK:
                                    Log.e(TAG, "网络异常");
                                    break;
                                case ErrorCode.NON_AD:
                                    Log.e(TAG, "视频暂无广告");
                                    break;
                                case ErrorCode.RESOURCE_NOT_READY:
                                    Log.e(TAG, "视频资源还没准备好");
                                    break;
                                case ErrorCode.SHOW_INTERVAL_LIMITED:
                                    Log.e(TAG, "视频展示间隔限制");
                                    break;
                                case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                    Log.e(TAG, "视频控件处在不可见状态");
                                    break;
                                default:
                                    Log.e(TAG, "请稍后再试");
                                    break;
                            }
                            Intent intent = new Intent();
                            intent.setClass(MembersOnlyActivity.this, OnlineMoviesActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onPlayCompleted() {
                            Intent intent = new Intent();
                            intent.setClass(MembersOnlyActivity.this, OnlineMoviesActivity.class);
                            startActivity(intent);
                        }
                    });
        } else {
            Intent intent = new Intent();
            intent.setClass(MembersOnlyActivity.this, OnlineMoviesActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 视频广告（包括普通视频广告、原生视频广告）
        VideoAdManager.getInstance(mContext).onAppExit();
    }
}
