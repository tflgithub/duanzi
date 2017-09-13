package com.tfl.recyclerviewcardgallery;

import android.content.Context;

/**
 * Created by tfl on 2017/8/28.
 */

public class ScreenUtil {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources()
                .getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
