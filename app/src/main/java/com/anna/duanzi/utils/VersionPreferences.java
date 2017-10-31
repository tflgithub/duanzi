package com.anna.duanzi.utils;

/**
 * Created by tfl on 2016/10/31.
 */
public class VersionPreferences extends BasePreferences {

    public static VersionPreferences getInstance() {
        return SearchPreferencesHolder.sPreferences;
    }

    static class SearchPreferencesHolder {
        static VersionPreferences sPreferences = new VersionPreferences();
    }

    private VersionPreferences() {
        super(VERSION_KEY);
    }

    private static final String VERSION_KEY = "version";

    private static final String APK_URL = "apk_url";

    public void setApkUrl(String value) {
        getAccessHelper().putString(APK_URL, value);
    }

    public String getApkUrl() {
        return getAccessHelper().getString(APK_URL, "");
    }
}
