package com.anna.duanzi.utils;

/**
 * Created by Administrator on 2016/10/13.
 */
public class AreaPreferences extends BasePreferences {

    public static AreaPreferences getInstance() {
        return AreaPreferencesHolder.sPreferences;
    }

    static class AreaPreferencesHolder {
        static AreaPreferences sPreferences = new AreaPreferences();
    }

    private AreaPreferences() {
        super(USER_NAME);
    }

    private static final String USER_NAME = "area";

    /**
     * set user name in preference file named "user"
     *
     * @param userName user name
     */
    public void setAreaList(String userName) {
        getAccessHelper().putString(USER_NAME, userName);
    }

    /**
     * get user name from preference file named "user"
     *
     * @return user name
     */
    public String getAreaList() {
        return getAccessHelper().getString(USER_NAME, "");
    }
}
