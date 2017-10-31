package com.anna.duanzi.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anna.duanzi.R;
import com.anna.duanzi.adapter.CardAdapter;
import com.anna.duanzi.base.BaseFragment;
import com.anna.duanzi.utils.BlurBitmapUtils;
import com.anna.duanzi.utils.ContextUtils;
import com.anna.duanzi.utils.FileUtils;
import com.anna.duanzi.utils.ViewSwitchUtils;
import com.anna.duanzi.utils.ZipUtils;
import com.tfl.recyclerviewcardgallery.CardScaleHelper;

import net.youmi.android.nm.cm.ErrorCode;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageFragment extends BaseFragment {

    private static final String TAG = ImageFragment.class.getSimpleName();
    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.blurView)
    ImageView mBlurView;
    CardScaleHelper mCardScaleHelper;
    private int mLastPos = -1;
    private Runnable mBlurRunnable;
    private List<Bitmap> mList = new ArrayList<>();

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_image, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        String title = getArguments().getString("title");
        ((TextView) getActivity().findViewById(R.id.header_actionbar_title)).setText(title);
        //解压图片包
        String filePath = FileUtils.getInstance().getFilePath(FileUtils.getInstance().getFileCacheRoot(), title + ".zip");
        String targetFolderPath = ContextUtils.getContext().getExternalCacheDir().getAbsolutePath() + "/IMAGE_AREA";
        String folder = ZipUtils.upZipFile(new File(filePath), targetFolderPath, true);
        //读取解压后的目录
        File fileAll = new File(folder);
        File[] files = fileAll.listFiles();
        if (files != null && files.length > 0) {
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                    mList.add(bitmap);
                }
            }
        }
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(new CardAdapter(mList));
        // mRecyclerView绑定scale效果
        mCardScaleHelper = new CardScaleHelper();
        mCardScaleHelper.setCurrentItemPos(0);
        mCardScaleHelper.attachToRecyclerView(mRecyclerView);
        initBlurBackground();
    }


    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    private void initBlurBackground() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange();
                }
            }
        });
        notifyBackgroundChange();
        setupSlideableSpotAd();
    }

    private void notifyBackgroundChange() {
        if (mLastPos == mCardScaleHelper.getCurrentItemPos()) return;
        mLastPos = mCardScaleHelper.getCurrentItemPos();
        if (mList.isEmpty()) {
            return;
        }
        final Bitmap bitmap = mList.get(mCardScaleHelper.getCurrentItemPos());
        mBlurView.removeCallbacks(mBlurRunnable);
        mBlurRunnable = new Runnable() {
            @Override
            public void run() {
                ViewSwitchUtils.startSwitchBackgroundAnim(mBlurView, BlurBitmapUtils.getBlurBitmap(mBlurView.getContext(), bitmap, 15));
            }
        };
        mBlurView.postDelayed(mBlurRunnable, 500);
    }

    /**
     * 设置轮播插屏广告
     */
    private void setupSlideableSpotAd() {
        // 设置插屏图片类型，默认竖图
        //		// 横图
        SpotManager.getInstance(getActivity()).setImageType(SpotManager
                .IMAGE_TYPE_HORIZONTAL);
        // 竖图
        // SpotManager.getInstance(mContext).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);

        // 设置动画类型，默认高级动画
        //		// 无动画
        //		SpotManager.getInstance(mContext).setAnimationType(SpotManager
        //				.ANIMATION_TYPE_NONE);
        //		// 简单动画
        //		SpotManager.getInstance(mContext)
        //		                    .setAnimationType(SpotManager.ANIMATION_TYPE_SIMPLE);
        // 高级动画
        SpotManager.getInstance(getActivity())
                .setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);

        // 展示轮播插屏广告
        SpotManager.getInstance(getActivity())
                .showSlideableSpot(getActivity(), new SpotListener() {

                    @Override
                    public void onShowSuccess() {
                    }

                    @Override
                    public void onShowFailed(int errorCode) {
                        Log.e(TAG, "轮播插屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                Log.e(TAG, "网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                Log.e(TAG, "暂无轮播插屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                Log.e(TAG, "轮播插屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                Log.e(TAG, "请勿频繁展示");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                Log.e(TAG, "请设置插屏为可见状态");
                                break;
                            default:
                                Log.e(TAG, "请稍后再试");
                                break;
                        }
                    }

                    @Override
                    public void onSpotClosed() {

                    }

                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        Log.d(TAG, "轮播插屏被点击");
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(getActivity()).onDestroy();
        // 插屏广告（包括普通插屏广告、轮播插屏广告、原生插屏广告）
        SpotManager.getInstance(getActivity()).onAppExit();
    }
}
