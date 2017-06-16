package com.anna.duanzi.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.anna.duanzi.common.Contants;
import com.anna.duanzi.utils.NetUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.cn.tfl.update.StorageUtils;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCMediaManager;

/**
 * Created by tfl on 2016/11/7.
 */
public class BaseActivity extends FragmentActivity {

    protected AVUser currentUser;
    protected int data_skip = 0;//忽略前多少个
    protected int data_limit = 20;//最多加载多少条记录。

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = AVUser.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getString("vip").equals(Contants.MEMBER.MEMBER_LEVEL_0)) {
                JCMediaManager.intance().isVip = false;
                JCMediaManager.intance().NO_VIP_FREE_TIME = Contants.MEMBER.NO_VIP_FREE_TIME;
            }
        }
        upgradePatch();
    }


    private void upgradePatch() {

        AVQuery<AVObject> query = new AVQuery<>("Patch");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && list.size() > 0) {
                    AVFile avFile = list.get(0).getAVFile("patchApk");
                    String url = avFile.getUrl();
                    downLoad(url);
                }
            }
        });
    }

    private void downLoad(String urlStr) {

        InputStream in = null;
        FileOutputStream out = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(false);
            urlConnection.setConnectTimeout(10 * 1000);
            urlConnection.setReadTimeout(10 * 1000);
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Charset", "UTF-8");
            urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            urlConnection.connect();
            long bytetotal = urlConnection.getContentLength();
            long bytesum = 0;
            int byteread = 0;
            in = urlConnection.getInputStream();
            File dir = StorageUtils.getCacheDirectory(this);
            String apkName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
            File apkFile = new File(dir, apkName);
            out = new FileOutputStream(apkFile);
            byte[] buffer = new byte[10 * 1024];
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);
            }
            TinkerInstaller.onReceiveUpgradePatch(this.getApplication(), apkFile.getPath());
        } catch (Exception e) {
            Log.e("BaseActivity", "download apk file error");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    /**
     * 检查网络
     *
     * @param context
     * @return
     */
    protected boolean checkNetWork(Context context) {
        if (NetUtils.getNetType(context) == NetUtils.UNCONNECTED) {
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        if (!checkNetWork(this)) {
            Toast.makeText(this, "无网络，请检查您的网络连接", Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }
}
