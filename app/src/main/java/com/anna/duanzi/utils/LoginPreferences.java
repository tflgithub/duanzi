package com.anna.duanzi.utils;

/**
 * Created by tfl on 2016/10/31.
 */
public class LoginPreferences extends BasePreferences {

    public static LoginPreferences getInstance() {
        return SearchPreferencesHolder.sPreferences;
    }

    static class SearchPreferencesHolder {
        static LoginPreferences sPreferences = new LoginPreferences();
    }

    private LoginPreferences() {
        super(LOGIN);
    }

    private static final String LOGIN = "login";

    private static final String LOGIN_STATUS = "login_status";

    public void setLoginStatus(boolean value) {
        getAccessHelper().putBoolean(LOGIN_STATUS, value);
    }

    public boolean getLoginStatus() {
        return getAccessHelper().getBoolean(LOGIN_STATUS, false);
    }
}
