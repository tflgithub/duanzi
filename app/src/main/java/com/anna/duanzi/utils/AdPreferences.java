package com.anna.duanzi.utils;

/**
 * Created by tfl on 2016/10/31.
 */
public class AdPreferences extends BasePreferences {

    public static AdPreferences getInstance() {
        return SearchPreferencesHolder.sPreferences;
    }

    static class SearchPreferencesHolder {
        static AdPreferences sPreferences = new AdPreferences();
    }

    private AdPreferences() {
        super(AD_KEY);
    }

    private static final String AD_KEY = "ad";

    private static final String VIDEO_AD_KEY = "video_ad";

    public void setVideoAd(boolean value) {
        getAccessHelper().putBoolean(VIDEO_AD_KEY, value);
    }

    public boolean getVideoAd() {
        return getAccessHelper().getBoolean(VIDEO_AD_KEY, false);
    }
}
