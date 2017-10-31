package com.cn.tfl.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by tfl on 2016/10/24.
 */
public class UpdateDialog {

    public static void show(final Context context, String content, final String downloadUrl) {
        if (isContextValid(context)) {
            final MaterialDialog materialDialog=new MaterialDialog(context);
            materialDialog.setTitle(R.string.android_auto_update_dialog_title)
                    .setMessage(content)
                    .setPositiveButton(R.string.android_auto_update_dialog_btn_download, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToDownload(context, downloadUrl);
                            materialDialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.android_auto_update_dialog_btn_cancel,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                }
                            });
            materialDialog.show();
        }
    }

    private static boolean isContextValid(Context context) {
        return context instanceof Activity && !((Activity) context).isFinishing();
    }


    private static void goToDownload(Context context, String downloadUrl) {
        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        intent.putExtra(Constants.APK_DOWNLOAD_URL, downloadUrl);
        context.startService(intent);
    }
}
