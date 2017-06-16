package com.anna.duanzi.utils;

import android.text.TextUtils;

import com.avos.avoscloud.AVUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tfl on 2016/10/28.
 */
public class StringUtils {
    /**
     * 验证手机号是否符合大陆的标准格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNumberValid(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断邮箱是否合法
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (null == email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 登录手机号码中间*代替
     * @return
     */
    public static String getMobile(String mobile) {
        StringBuilder sb = null;
        if (!TextUtils.isEmpty(mobile) && mobile.length() > 6) {
            sb = new StringBuilder();
            for (int i = 0; i < mobile.length(); i++) {
                char c = mobile.charAt(i);
                if (i >= 3 && i <= 6) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }
}
