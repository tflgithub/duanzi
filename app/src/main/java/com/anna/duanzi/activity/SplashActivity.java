package com.anna.duanzi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.anna.duanzi.MainActivity;
import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.domain.Version;
import com.anna.duanzi.utils.AdPreferences;
import com.anna.duanzi.utils.PermissionHelper;
import com.anna.duanzi.utils.VersionPreferences;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.cn.tfl.update.UpdateChecker;

import net.youmi.android.AdManager;
import net.youmi.android.nm.cm.ErrorCode;
import net.youmi.android.nm.sp.SplashViewSettings;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.sp.SpotRequestListener;
import net.youmi.android.nm.vdo.VideoAdManager;
import net.youmi.android.nm.vdo.VideoAdRequestListener;

/**
 * 开屏广告演示窗口
 *
 * @author Alian Lee
 * @since 2016-11-25
 */
public class SplashActivity extends BaseActivity {

    private PermissionHelper mPermissionHelper;

    public static String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 保存 installation 到服务器
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVInstallation.getCurrentInstallation().saveInBackground();
            }
        });
        AVQuery<Version> query = AVObject.getQuery(Version.class);
        query.getFirstInBackground(new GetCallback<Version>() {
            @Override
            public void done(Version version, AVException e) {
                if (e == null) {
                    //检查版本。
                    AVFile apkFile = version.getAVFile("apkFile");
                    if (apkFile != null) {
                        VersionPreferences.getInstance().setApkUrl(apkFile.getUrl());
                        UpdateChecker.checkForNotification(getApplicationContext(), apkFile.getUrl(), version.upgradeDesc, version.versionCode);
                    }
                }
            }
        });
        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(new PermissionHelper.OnApplyPermissionListener() {
            @Override
            public void onAfterApplyAllPermission() {
                runApp();
            }
        });
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            runApp();
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper.isAllRequestedPermissionGranted()) {
                runApp();
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                mPermissionHelper.applyPermissions();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 跑应用的逻辑
     */
    private void runApp() {
        //初始化SDK
        AdManager.getInstance(this).init("1f99e83c3df1f82d", "e07d107e9bad5414", true);
        preloadAd();
        //setupSplashAd();
    }

    /**
     * 预加载广告
     */
    private void preloadAd() {

        // 注意：不必每次展示插播广告前都请求，只需在应用启动时请求一次
        SpotManager.getInstance(mContext).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                Log.d(TAG, "请求插播广告成功");
                //				// 应用安装后首次展示开屏会因为本地没有数据而跳过
                //              // 如果开发者需要在首次也能展示开屏，可以在请求广告成功之前展示应用的logo，请求成功后再加载开屏
                setupSplashAd();
            }

            @Override
            public void onRequestFailed(int errorCode) {
                Log.d(TAG, "请求插播广告失败，errorCode: %s" + errorCode);
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        showShortToast("网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        Log.e(TAG, "暂无插屏广告");
                        break;
                    default:
                        showShortToast("请稍后再试");
                        break;
                }
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

//        //视频广告
//        // 设置服务器回调 userId，一定要在请求广告之前设置，否则无效
        VideoAdManager.getInstance(mContext).setUserId(AVUser.getCurrentUser() == null ? "" : AVUser.getCurrentUser().getObjectId());
        // 请求视频广告
        // 注意：不必每次展示视频广告前都请求，只需在应用启动时请求一次
        VideoAdManager.getInstance(mContext)
                .requestVideoAd(mContext, new VideoAdRequestListener() {

                    @Override
                    public void onRequestSuccess() {
                        Log.d(TAG, "请求视频广告成功");
                        AdPreferences.getInstance().setVideoAd(true);
                    }

                    @Override
                    public void onRequestFailed(int errorCode) {
                        Log.e(TAG, "请求视频广告失败，errorCode: %s" + errorCode);
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                Log.e(TAG, "网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                Log.e(TAG, "暂无视频广告");
                                break;
                            default:
                                //showShortToast("请稍后再试");
                                break;
                        }
                    }
                });
    }


    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        // 创建开屏容器
        final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ABOVE, R.id.view_divider);

        // 对开屏进行设置
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        //		// 设置是否展示失败自动跳转，默认自动跳转
        //   splashViewSettings.setAutoJumpToTargetWhenShowFailed(true);
        // 设置跳转的窗口类
        splashViewSettings.setTargetClass(MainActivity.class);
        // 设置开屏的容器
        splashViewSettings.setSplashViewContainer(splashLayout);

        // 展示开屏广告
        SpotManager.getInstance(mContext)
                .showSplash(mContext, splashViewSettings, new SpotListener() {

                    @Override
                    public void onShowSuccess() {
                        Log.d(TAG, "开屏展示成功");
                    }

                    @Override
                    public void onShowFailed(int errorCode) {
                        Log.e(TAG, "开屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                Log.e(TAG, "网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                Log.e(TAG, "暂无开屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                Log.e(TAG, "开屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                Log.e(TAG, "开屏展示间隔限制");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                Log.e(TAG, "开屏控件处在不可见状态");
                                break;
                            default:
                                Log.e(TAG, "errorCode: %d" + errorCode);
                                break;
                        }
                    }

                    @Override
                    public void onSpotClosed() {
                        Log.d(TAG, "开屏被关闭");
                    }

                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        Log.d(TAG, "开屏被点击");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(mContext).onDestroy();
    }
}
