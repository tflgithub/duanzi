package com.anna.duanzi.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anna.duanzi.R;
import com.anna.duanzi.domain.ApkItem;
import com.anna.duanzi.widget.DownLoadButton;
import com.cn.tfl.update.StorageUtils;
import com.morgoo.droidplugin.pm.PluginManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_FAILED_NOT_SUPPORT_ABI;
import static com.morgoo.helper.compat.PackageManagerCompat.INSTALL_SUCCEEDED;

public class MyGamesFragment extends ListFragment implements ServiceConnection {
    private ArrayAdapter<ApkItem> adapter;
    private DownLoadButton downLoadButton;
    LocalBroadcastManager broadcastManager;
    BroadcastReceiver mItemViewListClickReceiver;

    public MyGamesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyBroadcastReceiver.registerReceiver(getActivity().getApplication());
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.DOWN_LOAD_COMPLETE_BROADCAST");
        mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startLoad();
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver, intentFilter);

        adapter = new ArrayAdapter<ApkItem>(getActivity(), 0) {
            @Override
            public View getView(final int position, View convertView, final ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.apk_item, null);
                }
                final ApkItem item = getItem(position);
                ImageView icon = (ImageView) convertView.findViewById(R.id.imageView);
                icon.setImageDrawable(item.icon);
                TextView title = (TextView) convertView.findViewById(R.id.textView1);
                title.setText(item.title);
                Button uninstallBtn = (Button) convertView.findViewById(R.id.btn_uninstall);
                uninstallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doUninstall(item);
                    }
                });
                downLoadButton = (DownLoadButton) convertView.findViewById(R.id.button2);
                try {
                    if (PluginManager.getInstance().getPackageInfo(item.packageInfo.packageName, 0) != null) {
                        downLoadButton.setState(DownLoadButton.STATE_INSTALLED);
                        uninstallBtn.setVisibility(View.VISIBLE);
                    } else {
                        downLoadButton.setState(DownLoadButton.STATE_WAIT_INSTALL);
                        uninstallBtn.setVisibility(View.GONE);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                downLoadButton.setOnDownLoadButtonClickListener(new DownLoadButton.OnDownLoadButtonClickListener() {
                    @Override
                    public void onClick(View view, int curState) {
                        switch (curState) {
                            case DownLoadButton.STATE_WAIT_INSTALL:
                                doInstall(item);
                                break;
                            case DownLoadButton.STATE_INSTALLED:
                                PackageManager pm = getActivity().getPackageManager();
                                Intent intent = pm.getLaunchIntentForPackage(item.packageInfo.packageName);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                return convertView;
            }
        };
    }


    boolean isViewCreated = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        setEmptyText("你还没有下载游戏");
        setListAdapter(adapter);
        setListShown(false);
        getListView().setOnItemClickListener(null);
        if (PluginManager.getInstance().isConnected()) {
            startLoad();
        } else {
            PluginManager.getInstance().addServiceConnection(this);
        }
    }

    @Override
    public void onDestroyView() {
        isViewCreated = false;
        super.onDestroyView();
    }

    @Override
    public void setListShown(boolean shown) {
        if (isViewCreated) {
            super.setListShown(shown);
        }
    }

    private void startLoad() {
        setListShown(true);
        if (!isViewCreated) {
            return;
        }
        if (adapter.getCount() > 0) {
            adapter.clear();
        }
        new Thread("ApkScanner") {
            @Override
            public void run() {
                File file = StorageUtils.getCacheDirectory(getActivity());
                List<File> apks = new ArrayList<>();
                File[] files = file.listFiles();
                if (files != null) {
                    for (File apk : files) {
                        if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                            apks.add(apk);
                        }
                    }
                }
                PackageManager pm = getActivity().getPackageManager();
                for (final File apk : apks) {
                    try {
                        if (apk.exists() && apk.getPath().toLowerCase().endsWith(".apk")) {
                            final PackageInfo info = pm.getPackageArchiveInfo(apk.getPath(), 0);
                            if (info != null && isViewCreated) {
                                try {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ApkItem apkItem = new ApkItem(getActivity(), info, apk.getPath());
                                            adapter.add(apkItem);
                                        }
                                    });
                                } catch (Exception e) {
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }.start();
    }


    final Handler handler = new Handler();


    private synchronized void doInstall(ApkItem item) {
        item.installing = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        try {
            final int re = PluginManager.getInstance().installPackage(item.apkfile, 0);
            item.installing = false;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (re) {
                        case PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION:
                            Toast.makeText(getActivity(), "安装失败，游戏版本太低", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_FAILED_NOT_SUPPORT_ABI:
                            Toast.makeText(getActivity(), "安装失败，游戏版本太低", Toast.LENGTH_SHORT).show();
                            break;
                        case INSTALL_SUCCEEDED:
                            Toast.makeText(getActivity(), "安装成功", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void doUninstall(final ApkItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("你确定要卸载么？");
        builder.setMessage("你确定要卸载" + item.title + "么？");
        builder.setNegativeButton("卸载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!PluginManager.getInstance().isConnected()) {
                    Toast.makeText(getActivity(), "服务未连接", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        PluginManager.getInstance().deletePackage(item.packageInfo.packageName, 0);
                        Toast.makeText(getActivity(), "卸载完成", Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNeutralButton("取消", null);
        builder.show();
    }

    @Override
    public void onDestroy() {
        PluginManager.getInstance().removeServiceConnection(this);
        mMyBroadcastReceiver.unregisterReceiver(getActivity().getApplication());
        broadcastManager.unregisterReceiver(mItemViewListClickReceiver);
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        startLoad();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    private MyBroadcastReceiver mMyBroadcastReceiver = new MyBroadcastReceiver();

    private class MyBroadcastReceiver extends BroadcastReceiver {

        void registerReceiver(Context con) {
            IntentFilter f = new IntentFilter();
            f.addAction(PluginManager.ACTION_DROIDPLUGIN_INIT);
            f.addAction(PluginManager.ACTION_PACKAGE_ADDED);
            f.addAction(PluginManager.ACTION_PACKAGE_REMOVED);
            f.addDataScheme("package");
            con.registerReceiver(this, f);
        }

        void unregisterReceiver(Context con) {
            con.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.notifyDataSetChanged();
//            if (PluginManager.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
//                try {
//                    PackageManager pm = getActivity().getPackageManager();
//                    String pkg = intent.getData().getAuthority();
//                    PackageInfo info = PluginManager.getInstance().getPackageInfo(pkg, 0);
//                    adapter.add(new ApkItem(pm, info, info.applicationInfo.publicSourceDir));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (PluginManager.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
//                String pkg = intent.getData().getAuthority();
//                int N = adapter.getCount();
//                ApkItem iremovedItem = null;
//                for (int i = 0; i < N; i++) {
//                    ApkItem item = adapter.getItem(i);
//                    if (TextUtils.equals(item.packageInfo.packageName, pkg)) {
//                        iremovedItem = item;
//                        break;
//                    }
//                }
//                if (iremovedItem != null) {
//                    adapter.notifyDataSetChanged();
//                }
//            }
        }
    }

}