package com.cn.tfl.update;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.VerificationParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.IPackageManager;
/**
 * Created by tfl on 2016/10/24.
 */
public class DownloadService extends IntentService {

    private static final int BUFFER_SIZE = 10 * 1024; // 8k ~ 32K
    private static final String TAG = "DownloadService";

    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private Builder mBuilder;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);

        String appName = getString(getApplicationInfo().labelRes);
        int icon = getApplicationInfo().icon;

        mBuilder.setContentTitle(appName).setSmallIcon(icon);
        String urlStr = intent.getStringExtra(Constants.APK_DOWNLOAD_URL);
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
            byte[] buffer = new byte[BUFFER_SIZE];

            int oldProgress = 0;

            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread);

                int progress = (int) (bytesum * 100L / bytetotal);
                // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                if (progress != oldProgress) {
                    updateProgress(progress);
                }
                oldProgress = progress;
            }
            // 下载完成
            installAPk(apkFile);
            //installSlient(apkFile);
            //autoInstall(apkFile);

            mNotifyManager.cancel(NOTIFICATION_ID);

        } catch (Exception e) {
            Log.e(TAG, "download apk file error");
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


    private void updateProgress(int progress) {
        //"正在下载:" + progress + "%"
        mBuilder.setContentText(this.getString(R.string.android_auto_update_download_progress, progress)).setProgress(100, progress, false);
        //setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
        PendingIntent pendingintent = PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    private void installAPk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //如果没有设置SDCard写权限，或者没有sdcard,apk文件保存在内存中，需要授予权限才能安装
        try {
            String[] command = {"chmod", "777", apkFile.toString()};
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (IOException ignored) {
        }
        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //静默安装
    private void installSlient(File apkFile) {
        String cmd = "pm install -r "+apkFile.toString();
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            //静默安装需要root权限
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            //执行命令
            process.waitFor();
            //获取返回结果
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 无需root实现静默安装
     * @param apkFile
     */
    private void autoInstall(File apkFile)
    {
        try {
            //Uri uri = Uri.fromFile(apkFile);
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, "package");
            IPackageManager ipm = IPackageManager.Stub.asInterface(iBinder);
            @SuppressWarnings("deprecation")
            VerificationParams verificationParams = new VerificationParams(null, null, null, VerificationParams.NO_UID, null);
            ipm.installPackage(apkFile.toString(), new PackageInstallObserver(), 2, null, verificationParams, "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // 用于显示结果
    class PackageInstallObserver extends IPackageInstallObserver2.Stub {

        @Override
        public void onUserActionRequired(Intent intent) throws RemoteException {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) throws RemoteException {
            // TODO Auto-generated method stub

        }
    }
}
