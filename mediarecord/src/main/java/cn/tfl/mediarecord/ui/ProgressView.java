package cn.tfl.mediarecord.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Iterator;
import java.util.LinkedList;

import cn.tfl.mediarecord.util.Constants;

public class ProgressView extends View {

    public ProgressView(Context context) {
        super(context);
        init(context);
    }

    public ProgressView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init(paramContext);

    }

    public ProgressView(Context paramContext, AttributeSet paramAttributeSet,
                        int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        init(paramContext);
    }

    private Paint progressPaint, firstPaint, threePaint, breakPaint;
    private float firstWidth = 4f, threeWidth = 1f;
    private float perPixel = 0l;
    private float countRecorderTime = Constants.MAX_RECORD_TIME;
    private float perSecProgress = 0;
    private LinkedList<Integer> timeList = new LinkedList<>();// 每次暂停录制时，将录制时长记录到队列中

    private void init(Context paramContext) {
        progressPaint = new Paint();
        firstPaint = new Paint();
        threePaint = new Paint();
        breakPaint = new Paint();

        setBackgroundColor(Color.parseColor("#19000000"));

        progressPaint.setStyle(Paint.Style.FILL);
        progressPaint.setColor(Color.parseColor("#19e3cf"));

        firstPaint.setStyle(Paint.Style.FILL);
        firstPaint.setColor(Color.parseColor("#ffcc42"));

        threePaint.setStyle(Paint.Style.FILL);
        threePaint.setColor(Color.parseColor("#12a899"));

        breakPaint.setStyle(Paint.Style.FILL);
        breakPaint.setColor(Color.parseColor("#000000"));

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) paramContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        perPixel = dm.widthPixels / countRecorderTime;
        perSecProgress = perPixel;
    }

    private volatile State currentState = State.PAUSE;
    private boolean isVisible = true;// 一闪一闪的黄色区域是否可见
    private float countWidth = 0;// 每次绘制完成后，进度条的长度
    private float perProgress = 0;// 手指按下时，进度条每次增长的长度
    private long initTime;// 绘制完成时的时间戳
    private long drawFlashTime = 0;// 闪动的黄色区域时间戳


    public enum State {
        START(0x1), PAUSE(0x2), ROLLBACK(0x3), DELETE(0x4);

        static State mapIntToValue(final int stateInt) {
            for (State value : State.values()) {
                if (stateInt == value.getIntValue()) {
                    return value;
                }
            }
            return PAUSE;
        }

        private int mIntValue;

        State(int intValue) {
            mIntValue = intValue;
        }

        int getIntValue() {
            return mIntValue;
        }
    }

    public void setCurrentState(State state) {
        currentState = state;
        if (state != State.START)
            perProgress = perSecProgress;
        if (state == State.DELETE) {
            if ((timeList != null) && (!timeList.isEmpty())) {
                timeList.removeLast();
            }
        }
    }

    protected void onDraw(Canvas canvas) {
        long curTime = System.currentTimeMillis();
        countWidth = 0;
        if (!timeList.isEmpty()) {
            float frontTime = 0;
            Iterator<Integer> iterator = timeList.iterator();
            while (iterator.hasNext()) {
                int time = iterator.next();
                float left = countWidth;
                countWidth += (time - frontTime) * perPixel;
                canvas.drawRect(left, 0, countWidth, getMeasuredHeight(), progressPaint);
                canvas.drawRect(countWidth, 0, countWidth + threeWidth, getMeasuredHeight(), breakPaint);
                countWidth += threeWidth;
                frontTime = time;
            }

            if (timeList.getLast() <= 3000)
                canvas.drawRect(perPixel * 3000, 0, perPixel * 3000 + threeWidth, getMeasuredHeight(), threePaint);
        } else
            canvas.drawRect(perPixel * 3000, 0, perPixel * 3000 + threeWidth, getMeasuredHeight(), threePaint);

        if (currentState == State.START) {
            perProgress += perSecProgress * (curTime - initTime);
            if (countWidth + perProgress <= getMeasuredWidth())
                canvas.drawRect(countWidth, 0, countWidth + perProgress, getMeasuredHeight(), progressPaint);
            else
                canvas.drawRect(countWidth, 0, getMeasuredWidth(), getMeasuredHeight(), progressPaint);
        }
        if (drawFlashTime == 0 || curTime - drawFlashTime >= 500) {
            isVisible = !isVisible;
            drawFlashTime = System.currentTimeMillis();
        }
        if (isVisible) {
            if (currentState == State.START)
                canvas.drawRect(countWidth + perProgress, 0, countWidth + firstWidth + perProgress, getMeasuredHeight(), firstPaint);
            else
                canvas.drawRect(countWidth, 0, countWidth + firstWidth, getMeasuredHeight(), firstPaint);
        }
        initTime = System.currentTimeMillis();
        invalidate();
    }

    public void putTimeList(int time) {
        timeList.add(time);
    }

    public int getLastTime() {
        if ((timeList != null) && (!timeList.isEmpty())) {
            return timeList.getLast();
        }
        return 0;
    }

    public boolean isTimeListEmpty() {
        return timeList.isEmpty();
    }
}
