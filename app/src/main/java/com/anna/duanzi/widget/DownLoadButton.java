package com.anna.duanzi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.anna.duanzi.R;

/**
 * Created by tfl on 2016/11/16.
 */
public class DownLoadButton extends Button {

    private Paint paint;
    /**
     * 文本颜色
     */
    private int textColor;
    /**
     * 未下载状态背景
     */
    private Drawable normalBackground;
    /**
     * 已下载进度背景
     */
    private Drawable downLoadBackground;
    /**
     * 下载完成背景
     */
    private Drawable completeBackground;
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
     * 待安装
     */
    public final static int STATE_WAIT_INSTALL = 4;

    /**
     * 已安装
     */
    public final static int STATE_INSTALLED = 5;

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

    public DownLoadButton(Context context) {
        this(context, null);
    }

    public DownLoadButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownLoadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DownLoadButton, defStyleAttr, 0);
        normalBackground = getResources().getDrawable(R.drawable.rect_normal_bg);
        downLoadBackground = getResources().getDrawable(R.drawable.rect_downloaded_bg);
        completeBackground = getResources().getDrawable(R.drawable.rect_complete_bg);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.DownLoadButton_normalBackground:
                    normalBackground = a.getDrawable(attr);
                    break;
                case R.styleable.DownLoadButton_downLoadedBackground:
                    downLoadBackground = a.getDrawable(attr);
                    break;
                case R.styleable.DownLoadButton_downLoadCompleteBackground:
                    completeBackground = a.getDrawable(attr);
                    break;
                case R.styleable.DownLoadButton_textColor:
                    textColor = a.getColor(attr, getResources().getColor(R.color.color_downloaded));
                    break;
            }
        }
        /**
         * 设置button本身的文字为透明以免干扰我们自己绘制上去的文字
         */
        setTextColor(getResources().getColor(R.color.color_transparent));
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        paint.setColor(textColor);

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
                curPrecent = 0;
                setBackgroundDrawable(normalBackground);
                break;
            case STATE_DOWNLOADING:
                tip = curPrecent + "%";
                //计算当前进度所需宽度
                int downLoadedWidth = (int) (getMeasuredWidth() * ((double) curPrecent / 100));
                Rect rect = new Rect(0, 0, downLoadedWidth, getMeasuredHeight());
                downLoadBackground.setBounds(rect);
                downLoadBackground.draw(canvas);
                break;
            case STATE_STOPDOWNLOADING:
                tip = getResources().getString(R.string.download_stop);
                setBackgroundDrawable(normalBackground);
                break;
            case STATE_COMPLETE:
                tip = getResources().getString(R.string.download_complete);
                setBackgroundDrawable(completeBackground);
                break;
            case STATE_WAIT_INSTALL:
                tip = getResources().getString(R.string.install);
                setBackgroundDrawable(completeBackground);
                break;
            case STATE_INSTALLED:
                tip = getResources().getString(R.string.stalled);
                setBackgroundDrawable(normalBackground);
                break;
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
