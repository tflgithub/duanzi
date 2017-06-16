package com.anna.duanzi.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.anna.duanzi.R;

/**
 * Created by Administrator on 2016/10/13.
 */
public class TabTextView extends TextView {

    protected boolean mTabSelectedTextColorSet;
    protected int mTabSelectedTextColor;

    public TabTextView(Context context) {
        super(context);
    }

    public TabTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public  TabTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        getAttributes(context, attrs, defStyle);
    }

    private void getAttributes(Context context, AttributeSet attrs, int defStyle) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.tabTextView,
                defStyle, R.style.tabTextView);
        if (a.hasValue(R.styleable.tabTextView_tabSelectedTextColor)) {
            mTabSelectedTextColor = a
                    .getColor(R.styleable.tabTextView_tabSelectedTextColor, 0);
            mTabSelectedTextColorSet = true;
        }
        a.recycle();
    }

    public ColorStateList createColorStateList(int defaultColor, int selectedColor) {
        final int[][] states = new int[2][];
        final int[] colors = new int[2];
        states[0] = SELECTED_STATE_SET;
        colors[0] = selectedColor;
        // Default enabled state
        states[1] = EMPTY_STATE_SET;
        colors[1] = defaultColor;
        return new ColorStateList(states, colors);
    }
}
