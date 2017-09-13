package com.anna.duanzi;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.anna.duanzi.domain.Area;
import com.anna.duanzi.domain.Comment;
import com.anna.duanzi.domain.Duanzi;
import com.anna.duanzi.domain.Fiction;
import com.anna.duanzi.domain.Games;
import com.anna.duanzi.domain.Image;
import com.anna.duanzi.domain.ImageArea;
import com.anna.duanzi.domain.Movies;
import com.anna.duanzi.domain.ShareLog;
import com.anna.duanzi.domain.Version;
import com.anna.duanzi.domain.WebMovie;
import com.anna.duanzi.utils.ContextUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.cn.tfl.update.UpdateChecker;
import com.morgoo.droidplugin.PluginHelper;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.File;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by tfl on 2016/1/22.
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.setContext(getApplicationContext());

        AVOSCloud.initialize(this, "ETHUeY9kDrawgWu7Ik7JqqHl-gzGzoHsz", "3b4k1ciWrj6D3cY5bHjV0Yi6");
        registerSubclass();
        //初始化ImageLoader
         initUniversalImageLoader();
        // 初始化ShareSDK
        ShareSDK.initSDK(this);

        // PluginHelper.getInstance().applicationOnCreate(getBaseContext());

        PushService.setDefaultPushCallback(this, MainActivity.class);

        // 保存 installation 到服务器
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                AVInstallation.getCurrentInstallation().saveInBackground();
            }
        });
        AVQuery<Version> query= AVObject.getQuery(Version.class);
        query.getFirstInBackground(new GetCallback<Version>() {
            @Override
            public void done(Version version, AVException e) {
                if(e==null)
                {
                    //检查版本。
                    AVFile apkFile = version.getAVFile("apkFile");
                    UpdateChecker.checkForNotification(getApplicationContext(),apkFile.getUrl(),version.upgradeDesc,version.versionCode);
                }
            }
        });

    }
    /**
     * 子类化
     */
    private void registerSubclass() {
        AVObject.registerSubclass(Duanzi.class);
        AVObject.registerSubclass(Area.class);
        AVObject.registerSubclass(Image.class);
        AVObject.registerSubclass(Comment.class);
        AVObject.registerSubclass(Fiction.class);
        AVObject.registerSubclass(Games.class);
        AVObject.registerSubclass(Movies.class);
        AVObject.registerSubclass(ImageArea.class);
        AVObject.registerSubclass(ShareLog.class);
        AVObject.registerSubclass(Version.class);
        AVObject.registerSubclass(WebMovie.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(base);
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }

    private void initUniversalImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(new ColorDrawable(Color.parseColor("#f0f0f0")))
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        int memCacheSize = 1024 * 1024 * memClass / 8;

        File cacheDir = new File(Environment.getExternalStorageDirectory().getPath() + "/jiecao/cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(new UsingFreqLimitedMemoryCache(memCacheSize))
                .memoryCacheSize(memCacheSize)
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .defaultDisplayImageOptions(options)
                .build();
        ImageLoader.getInstance().init(config);
    }

}
