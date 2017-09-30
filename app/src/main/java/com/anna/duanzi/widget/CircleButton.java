package com.anna.duanzi.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.anna.duanzi.R;

/**
 * Created by Administrator on 2017/9/6.
 */

public class CircleButton extends android.support.v7.widget.AppCompatImageView {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
    private static final int PRESSED_RING_ALPHA = 75;
    private int centerY;
    private int centerX;
    private int outerRadius;
    private int pressedRingRadius;
    private float borderRadius;
    private final RectF borderRect = new RectF();
    private int pressedRingWidth;
    private int borderWidth = 0;
    private Paint circlePaint, focusPaint, centerPaint, borderPaint;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
    private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;
    private int defaultColor = Color.BLACK;
    private int pressedColor;
    private ObjectAnimator pressedAnimator;
    private float animationProgress;

    public CircleButton(Context context) {
        super(context);
        init(context, null);
    }

    public CircleButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attributeSet) {
        this.setFocusable(true);
        this.setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);

        focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        focusPaint.setStyle(Paint.Style.STROKE);

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.STROKE);
        centerPaint.setStrokeWidth(5);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);//空心
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(borderWidth);//空心宽度
        pressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());

        int cb_color = defaultColor;
        int center_color = defaultColor;
        int cb_border_color = defaultColor;
        if (attributeSet != null) {
            final TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.CircleButton);
            cb_color = a.getColor(R.styleable.CircleButton_cb_color, cb_color);
            cb_border_color = a.getColor(R.styleable.CircleButton_cb_border_color, cb_border_color);
            center_color = a.getColor(R.styleable.CircleButton_cb_center_color, center_color);
            pressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, pressedRingWidth);
            borderWidth = a.getDimensionPixelSize(R.styleable.CircleButton_cb_border_width, borderWidth);
            a.recycle();
        }

        setColor(cb_color, center_color, cb_border_color);
        focusPaint.setStrokeWidth(pressedRingWidth);
        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 1f);
        pressedAnimator.setDuration(pressedAnimationTime);
    }

    public void setColor(int color, int centerColor, int borderColor) {
        this.pressedColor = getHighlightColor(color, PRESSED_COLOR_LIGHTUP);
        defaultColor=color;
        centerPaint.setColor(centerColor);
        circlePaint.setColor(color);
        borderPaint.setColor(borderColor);
        focusPaint.setColor(color);
        focusPaint.setAlpha(PRESSED_RING_ALPHA);
        this.invalidate();
    }

    private int getHighlightColor(int color, int amount) {
        return Color.argb(Math.min(255, Color.alpha(color)), Math.min(255, Color.red(color) + amount),
                Math.min(255, Color.green(color) + amount), Math.min(255, Color.blue(color) + amount));
    }

    public void setAnimationProgress(float animationProgress) {
        this.animationProgress = animationProgress;
        this.invalidate();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (circlePaint != null) {
            circlePaint.setColor(pressed ? pressedColor : defaultColor);
        }

        if (pressed) {
            showPressedRing();
        } else {
            hidePressedRing();
        }
    }

    private void hidePressedRing() {
        pressedAnimator.setFloatValues(pressedRingWidth, 0f);
        pressedAnimator.start();
    }

    private void showPressedRing() {
        pressedAnimator.setFloatValues(animationProgress, pressedRingWidth);
        pressedAnimator.start();
    }

    int with, height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.with = w;
        this.height = h;
        centerX = w / 2;
        centerY = h / 2;
        outerRadius = Math.min(w, h) / 2;
        pressedRingRadius = outerRadius - pressedRingWidth - pressedRingWidth / 2;
        borderRect.set(0, 0,w, h);
        borderRadius = Math.min((borderRect.height() - borderWidth) / 2, (borderRect.width() - borderWidth) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(centerX, centerY, pressedRingRadius + animationProgress, focusPaint);
        canvas.drawCircle(centerX, centerY, outerRadius - pressedRingWidth, circlePaint);
        if (borderWidth != 0) {
            canvas.drawCircle(centerX, centerY, borderRadius, borderPaint);
        }
//        canvas.drawLine(with / 3, centerY,4 * with / 6, centerY, centerPaint);
//        canvas.drawLine(centerX, 4 * height / 6, centerX, height / 3, centerPaint);
        super.onDraw(canvas);
    }

}
