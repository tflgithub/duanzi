package com.anna.duanzi.utils;

/**
 * Created by tfl on 2016/10/31.
 */
public class VersionPreferences extends BasePreferences {

    public static VersionPreferences getInstance() {
        return VersionPreferencesHolder.sPreferences;
    }

    static class VersionPreferencesHolder {
        static VersionPreferences sPreferences = new VersionPreferences();
    }

    private VersionPreferences() {
        super(APK_URL);
    }

    private static final String APK_URL = "apk_url";


    public void setApkUrl(String userName) {
        getAccessHelper().putString(APK_URL, userName);
    }

    public String getApkUrl() {
        return getAccessHelper().getString(APK_URL, "");
    }
}
