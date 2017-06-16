package com.anna.duanzi.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.anna.duanzi.R;

/**
 * Created by tfl on 2016/11/16.
 */
public class DownLoadText extends TextView {

    private Paint paint;
    /**
     * 未下载状态字体颜色
     */
    private int normalColor = getResources().getColor(R.color.gray);
    /**
     * 下载中的字体颜色
     */
    private int downLoadColor = getResources().getColor(R.color.orange);
    /**
     * 下载完的字体颜色
     */
    private int completeColor = getResources().getColor(R.color.green);
    /**
     * 未下载状态
     */
    public final static int STATE_NORMAL = 0;
    /**
     * 下载中
     */
    public final static int STATE_DOWNLOADING = 1;

    /**
     * 暂停下载
     */
    public final static int STATE_STOPDOWNLOADING = 2;

    /**
     * 下载完成
     */
    public final static int STATE_COMPLETE = 3;

    /**
     * 当前状态
     */
    private int curState = 0;
    /**
     * 当前下载进度
     * 百分比
     */
    private int curPrecent = 0;

    private OnDownLoadButtonClickListener onDownLoadButtonClickListener;

    public DownLoadText(Context context) {
        this(context, null);
    }

    public DownLoadText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownLoadText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 设置button本身的文字为透明以免干扰我们自己绘制上去的文字
         */
        setTextColor(getResources().getColor(R.color.color_transparent));
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        paint.setColor(normalColor);
        curState = STATE_NORMAL;
        setGravity(Gravity.CENTER);

        /**
         * 设置点击事件
         *   这个方法行得通，但是我感觉有更好的实现方式。
         */
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDownLoadButtonClickListener != null) {
                    //点击时返回当前的状态
                    onDownLoadButtonClickListener.onClick(v, curState);
                }
            }
        });
    }

    /**
     * 设置当前状态
     *
     * @param state
     */
    public void setState(int state) {
        this.curState = state;
        postInvalidate();
    }

    /**
     * 设置下载进度
     *
     * @param precent 完成进度百分比
     */
    public void setDownLoadProgress(int precent) {
        this.curPrecent = precent;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;
        /**
         * 计算文本显示所需宽高
         */
        Rect textBound = new Rect();
        String tip = getResources().getString(R.string.download_complete);
        paint.getTextBounds("下载完成", 0, tip.length(), textBound);

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize + getPaddingLeft() + getPaddingRight();
        } else {
            width = textBound.width() + getPaddingLeft() + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize + getPaddingTop() + getPaddingBottom();
        } else {
            height = textBound.height() + getPaddingTop() + getPaddingBottom();
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String tip = "";
        switch (curState) {
            case STATE_NORMAL:
                tip = getResources().getString(R.string.download);
                setTextColor(normalColor);
                break;
            case STATE_DOWNLOADING:
                setTextColor(downLoadColor);
                tip = getResources().getString(R.string.downloading);
                break;
            case STATE_STOPDOWNLOADING:
                tip = getResources().getString(R.string.download_stop);
                setTextColor(normalColor);
                break;
            case STATE_COMPLETE:
                tip = getResources().getString(R.string.download_complete);
                setTextColor(completeColor);
        }
        /**
         * 绘制提示文本
         */
        Rect textBound = new Rect();
        paint.getTextBounds(tip, 0, tip.length(), textBound);
        canvas.drawText(tip, (getMeasuredWidth() - textBound.width()) / 2, (getMeasuredHeight() + textBound.height()) / 2, paint);
    }


    public void setOnDownLoadButtonClickListener(OnDownLoadButtonClickListener onDownLoadButtonClickListener) {
        this.onDownLoadButtonClickListener = onDownLoadButtonClickListener;

    }

    public interface OnDownLoadButtonClickListener {
        void onClick(View v, int curState);
    }

}
