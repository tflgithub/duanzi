package com.anna.duanzi.common;

/**
 * Created by tfl on 2016/10/10.
 */
public class Constants {

    public final static String CATEGORY = "category";
    public final static String CATEGORY_TXT = "1";
    public final static String CATEGORY_IMAGE = "2";
    public final static String CATEGORY_VIDEO = "3";

    public final static int TYPE_DUANZI = 1;
    public final static int TYPE_FICTION = 2;

    public final static int SHARE_COUNT_NUM_TEMP = 10;//活动期间使用，分享10次就可以升级为1级会员。


    //发布
    public interface PUBLISH {
        int TXT = 1;
        int VEDIO = 2;
        int IMAGE = 3;
    }

    /**
     * 修改账户信息类型
     */
    public interface UPDATE_USERINFO {
        int NICK_NAME = 1;
        int EMAIL = 2;
        int MOBILE = 3;
        int PASSWORD = 4;
    }

    public final static String UPDATE_USERINFO_TYPE = "update_userInfo_type";
    public final static String UPDATE_USERINFO_CONTENT = "update_userInfo_content";

    /**
     * 会员
     */
    public interface MEMBER {

        int NO_VIP_FREE_TIME = 1;//非会员免费播放时间。单位：分钟

        String MEMBER_LEVEL_0 = "0";//普通用户（0级会员）

        String MEMBER_LEVEL_1 = "1";//一级会员

        String MEMBER_LEVEL_2 = "2";//二级会员

        String MEMBER_LEVEL_3 = "3";//三级会员（最高级别）
    }

    /**
     * 会员专区
     */
    public interface MEMBER_AREA {

        String CATEGORY_ONLINE = "1";//在线专区

        String CATEGORY_DOWNLOAD = "2";//下载专区
    }
}
