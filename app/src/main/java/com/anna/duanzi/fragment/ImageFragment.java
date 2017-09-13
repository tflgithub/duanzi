package com.anna.duanzi.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageFragment extends BaseFragment {

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
        ((TextView)getActivity().findViewById(R.id.header_actionbar_title)).setText(title);
        //解压图片包
        String filePath = FileUtils.getInstance().getFilePath(FileUtils.getInstance().getFileCacheRoot(), title+".zip");
        String targetFolderPath = ContextUtils.getContext().getExternalCacheDir().getAbsolutePath() + "/IMAGE_AREA";
        String folder=ZipUtils.upZipFile(new File(filePath), targetFolderPath, true);
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
}
