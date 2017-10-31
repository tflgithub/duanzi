package com.cn.tfl.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by tfl on 2016/10/24.
 */
public class UpdateChecker {

    public static void checkForDialog(Context context,String url,String upgradeDesc,int versionCode) {
        if(versionCode>AppUtils.getVersionCode(context)) {
            UpdateDialog.show(context, upgradeDesc, url);
        }
        else{
            Toast.makeText(context, context.getString(R.string.android_auto_update_toast_no_new_update), Toast.LENGTH_SHORT).show();
        }
    }
    public static void checkForNotification(Context context,String url,String upgradeDesc,int versionCode) {
        if(versionCode>AppUtils.getVersionCode(context)) {
            Intent myIntent = new Intent(context, DownloadService.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            myIntent.putExtra(Constants.APK_DOWNLOAD_URL, url);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            int smallIcon = context.getApplicationInfo().icon;
            Notification notify = new NotificationCompat.Builder(context)
                    .setTicker(context.getString(R.string.android_auto_update_notify_ticker))
                    .setContentTitle(context.getString(R.string.android_auto_update_notify_content))
                    .setContentText(upgradeDesc)
                    .setSmallIcon(smallIcon)
                    .setContentIntent(pendingIntent).build();

            notify.flags = android.app.Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notify);
        }
    }
}
