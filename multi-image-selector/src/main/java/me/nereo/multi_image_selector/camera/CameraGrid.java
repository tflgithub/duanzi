package me.nereo.multi_image_selector.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import me.nereo.multi_image_selector.R;

/**
 * Created by Administrator on 2015/7/31.
 */
public class CameraGrid extends View {

    private int topBannerWidth = 0;
    private Paint mPaint;
    Context mContext;
    private int type = 1;

    public CameraGrid(Context context) {
        this(context, null);
        mContext = context;
    }

    public CameraGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setType(int type) {
        this.type = type;
        invalidate();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(120);
        mPaint.setStrokeWidth(1f);
    }


    //画一个井字,上下画两条灰边，中间为正方形
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();
        if (width < height) {
            topBannerWidth = height - width;
        }
        if (showGrid) {
            canvas.drawLine(width / 3, 0, width / 3, height, mPaint);
            canvas.drawLine(width * 2 / 3, 0, width * 2 / 3, height, mPaint);
            canvas.drawLine(0, height / 3, width, height / 3, mPaint);
            canvas.drawLine(0, height * 2 / 3, width, height * 2 / 3, mPaint);
        }
//        if (type == CameraActivity.CAMERA_TYPE_1) {
//            Bitmap mBitmap = BitmapFactory.decodeResource(((Activity) mContext).getResources(), R.drawable.hold_id_card_ref);
//            canvas.drawBitmap(mBitmap, (width - mBitmap.getWidth()) / 2, dip2px(mContext, 50), mPaint);
//        } else if (type == CameraActivity.CAMERA_TYPE_2) {
//            Bitmap mBitmap = BitmapFactory.decodeResource(((Activity) mContext).getResources(), R.drawable.frame_id_card02);
//            canvas.drawBitmap(mBitmap, (width - mBitmap.getWidth()) / 2, (height - mBitmap.getHeight()) / 2, mPaint);
//        }
    }

    private boolean showGrid = false;

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public int getTopWidth() {
        return topBannerWidth;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
