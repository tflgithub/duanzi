package com.anna.duanzi.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.utils.ContextUtils;
import com.anna.duanzi.utils.FileUtils;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;
import com.morgoo.droidplugin.pm.PluginManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tfl on 2016/1/22.
 */
public class FoundFragment extends BaseFragment implements OnDismissListener {

    @Bind(R.id.header_actionbar_title)
    TextView title;
    @Bind(R.id.header_actionbar_back)
    ImageView back;

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_found, null);
        ButterKnife.bind(this, view);
        back.setVisibility(View.GONE);
        mSVProgressHUD.setOnDismissListener(this);
        title.setText("发现");
        return view;
    }

    @Override
    public void initData() {
    }

    @OnClick({R.id.tv_trends, R.id.tv_transport})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_transport:
                AVQuery<AVObject> avQuery = new AVQuery<>("Transport");
                FileUtils.getInstance().setFileCacheRoot(ContextUtils.getContext().getExternalCacheDir().getAbsolutePath() + "/apk");
                avQuery.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject avObject, AVException e) {
                        if (e == null) {
                            AVFile file = avObject.getAVFile("apk");
                            mSVProgressHUD.showWithProgress("加载中...", SVProgressHUD.SVProgressHUDMaskType.Black);
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, AVException e) {
                                    // bytes 就是文件的数据流
                                    FileUtils.getInstance().write2SD(FileUtils.getInstance().getFileCacheRoot(), "transport.apk", bytes);
                                }
                            }, new ProgressCallback() {
                                @Override
                                public void done(Integer integer) {
                                    // 上传进度数据，integer 介于 0 和 100。
                                    if (integer == 100) {
                                        mSVProgressHUD.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
            case R.id.tv_trends:
                break;
        }
    }

    @Override
    public void onDismiss(SVProgressHUD hud) {
        try {
            String filePath = FileUtils.getInstance().getFilePath(FileUtils.getInstance().getFileCacheRoot(), "transport.apk");
            if (PluginManager.getInstance().getPackageInfo("com.cn.tfl.kuaichuan", 0)== null) {
                PluginManager.getInstance().installPackage(filePath, 0);
            }
            PackageManager pm = getActivity().getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage("com.cn.tfl.kuaichuan");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
