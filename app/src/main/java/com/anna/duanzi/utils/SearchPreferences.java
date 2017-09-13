package com.anna.duanzi.utils;

/**
 * Created by tfl on 2016/10/31.
 */
public class SearchPreferences extends BasePreferences {

    public static SearchPreferences getInstance() {
        return SearchPreferencesHolder.sPreferences;
    }

    static class SearchPreferencesHolder {
        static SearchPreferences sPreferences = new SearchPreferences();
    }

    private SearchPreferences() {
        super(SEARCH_JUST_KEY);
    }

    private static final String SEARCH_JUST_KEY = "search_just_key";

    private static final String SEARCH_HISTORY_KEY = "search_history";

    public void setSearchJustKey(String key) {
        getAccessHelper().putString(SEARCH_JUST_KEY, key);
    }

    public String getSearchJustKey() {
        return getAccessHelper().getString(SEARCH_JUST_KEY, "");
    }

    public void setSearchHistoryKey(String key) {
        getAccessHelper().putString(SEARCH_HISTORY_KEY, key);
    }

    public String getSearchHistoryKey() {
        return getAccessHelper().getString(SEARCH_HISTORY_KEY, "");
    }
}
