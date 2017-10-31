package com.anna.duanzi.activity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.anna.duanzi.utils.BookPageFactory;
import com.anna.duanzi.base.BaseActivity;
import com.anna.duanzi.utils.FileUtils;
import com.anna.duanzi.widget.PageWidget;

import net.youmi.android.nm.cm.ErrorCode;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;

import java.io.IOException;

public class TurnBookActivity extends BaseActivity {

    private static final String TAG = TurnBookActivity.class.getSimpleName();
    /**
     * Called when the activity is first created.
     */
    private PageWidget mPageWidget;
    Bitmap mCurPageBitmap, mNextPageBitmap;
    Canvas mCurPageCanvas, mNextPageCanvas;
    BookPageFactory pagefactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mPageWidget = new PageWidget(this);
        setContentView(mPageWidget);

        Display mDisplay = getWindowManager().getDefaultDisplay();
        int W = mDisplay.getWidth();
        int H = mDisplay.getHeight();
        mPageWidget.setScreen(W, H);
        mCurPageBitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap
                .createBitmap(W, H, Bitmap.Config.ARGB_8888);

        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
        pagefactory = new BookPageFactory(W, H);//设置分辨率为480*800

//        pagefactory.setBgBitmap(BitmapFactory.decodeResource(
//                this.getResources(), R.drawable.txt_bg));//设置背景图片

        String name = getIntent().getExtras().getString("name");
        String filePath = FileUtils.getInstance().getFilePath(FileUtils.getInstance().getFileCacheRoot(), name);
        try {
            // "/sdcard/test.txt"
            pagefactory.openbook(filePath);//打开文件
            pagefactory.onDraw(mCurPageCanvas);//将文字绘于手机屏幕
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            Toast.makeText(this, "电子书不存在,请将《test.txt》放在SD卡根目录下",
                    Toast.LENGTH_SHORT).show();
        }

        mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

        mPageWidget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                // TODO Auto-generated method stub

                boolean ret = false;
                if (v == mPageWidget) {
                    if (e.getAction() == MotionEvent.ACTION_DOWN) {
                        //停止动画。与forceFinished(boolean)相反，Scroller滚动到最终x与y位置时中止动画。
                        mPageWidget.abortAnimation();
                        //计算拖拽点对应的拖拽角
                        mPageWidget.calcCornerXY(e.getX(), e.getY());
                        //将文字绘于当前页
                        pagefactory.onDraw(mCurPageCanvas);
                        if (mPageWidget.dragToRight()) {
                            //是否从左边翻向右边
                            try {
                                //true，显示上一页
                                pagefactory.prePage();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            if (pagefactory.isfirstPage()) {
                                Toast.makeText(TurnBookActivity.this, "这已经是第一页了", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            pagefactory.onDraw(mNextPageCanvas);
                        } else {
                            try {
                                //false，显示下一页
                                pagefactory.nextPage();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            if (pagefactory.islastPage()) {
                                Toast.makeText(TurnBookActivity.this, "这已经是最后页了", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            pagefactory.onDraw(mNextPageCanvas);
                        }
                        mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
                    }

                    ret = mPageWidget.doTouchEvent(e);
                    return ret;
                }
                return false;
            }

        });
        setupSlideableSpotAd();
    }


    /**
     * 设置轮播插屏广告
     */
    private void setupSlideableSpotAd() {
        // 设置插屏图片类型，默认竖图
        //		// 横图
        SpotManager.getInstance(mContext).setImageType(SpotManager
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
        SpotManager.getInstance(mContext)
                .setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);

        // 展示轮播插屏广告
        SpotManager.getInstance(mContext)
                .showSlideableSpot(mContext, new SpotListener() {

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
    protected void onDestroy() {
        super.onDestroy();
        // 开屏展示界面的 onDestroy() 回调方法中调用
        SpotManager.getInstance(mContext).onDestroy();
        // 插屏广告（包括普通插屏广告、轮播插屏广告、原生插屏广告）
        SpotManager.getInstance(mContext).onAppExit();
    }
}
