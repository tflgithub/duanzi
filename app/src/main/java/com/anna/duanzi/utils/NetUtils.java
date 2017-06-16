package com.anna.duanzi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by tfl on 2016/11/23.
 */
public class NetUtils {
    /** 没有网络 */
    public static final int UNCONNECTED = -1;
    /** WIFI网络 */
    public static final int TYPE_WIFI = 0;
    /** 2G网络 */
    public static final int TYPE_2G = 1;
    /** 3G网络 */
    public static final int TYPE_3G = 2;
    /** 未知网络类型 */
    public static final int TYPE_UNKNOW = 3;

    /**
     * 获取网络类型
     * @param context
     * @return
     */
    public static int getNetType(Context context)
    {
        int netType = UNCONNECTED;
        // 获取网络管理Manager对象
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取手机管理Manager对象
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 获取活动的网络状态信息
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo == null)
        {// 没有网络
            netType = UNCONNECTED;
        }
        else
        {
            // 网络类型代码
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI)
            {
                return TYPE_WIFI;
            }
            else if (type == ConnectivityManager.TYPE_MOBILE)
            {
                // 手机网络
                switch (tm.getNetworkType())
                {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:// ~50-100 Kbps 2G网络
                        netType = TYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_CDMA:// ~14-64 Kbps 2G网络
                        netType = TYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EDGE:// ~50-100 Kbps 2G网络
                        netType = TYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_GPRS:// ~100 Kbps 2G网络
                        netType = TYPE_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:// ~ 400-1000 Kbps
                        // 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:// ~ 600-1400 Kbps
                        // 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSDPA:// ~ 2-14 Mbps 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSPA:// ~ 700-1700 kbps 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 Kbps 3G网络
                        netType = TYPE_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:// 未知网络
                        netType = TYPE_UNKNOW;
                        break;
                    default:
                        netType = TYPE_2G;
                }
            }
        }
        return netType;

    }
}
