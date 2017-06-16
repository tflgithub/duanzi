package com.anna.duanzi.utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by tfl on 2016/10/22.
 */
public class DateUtils {

    /**
     * 将UTC时间转换为东八区时间
     *
     * @param UTCTime
     * @return
     */
    public static String getLocalTimeFromUTC(String UTCTime) {
        String ts = UTCTime;
        ts = ts.replace("Z", " UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        Date dt = null;
        try {
            dt = sdf.parse(ts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");//小写的mm表示的是分钟
        String str = simpleDateFormat.format(dt);
        return str;
    }

    public static String utc2Local(String utcTime, String utcTimePatten,
                                   String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        //utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));//时区定义并进行时间获取
        utcTime = utcTime.replace("Z", " UTC");
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
        localFormater.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }
}
