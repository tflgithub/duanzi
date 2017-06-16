package com.anna.duanzi.utils;

import android.content.Context;

/**
 * Created by Administrator on 2016/10/13.
 */
public class ContextUtils {

    private static Context sContext;

    public static void setContext(Context context) {
        sContext = context;
    }

    public static Context getContext() {
        return sContext;
    }
}
