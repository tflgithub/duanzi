package com.anna.duanzi.common;

import com.anna.duanzi.domain.Area;
import com.anna.duanzi.domain.Duanzi;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.List;

/**
 * Created by Administrator on 2016/10/11.
 */
public class HttpHelper {

    public static final String TAG = "HttpHelper";

    public static HttpHelper httpHelper = null;

    public static HttpHelper getInstance() {
        if (httpHelper == null) {
            httpHelper = new HttpHelper();
        }
        return httpHelper;
    }


    public interface Callback {
        void onSuccess(List<?> responseInfo);

        void onFailure();
    }

    /**
     * 获取区域
     */
    public void getAreas(final Callback callback) {
        AVQuery<Area> query = AVObject.getQuery(Area.class);
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<Area>() {
            @Override
            public void done(List<Area> list, AVException e) {
                if (list != null && list.size() != 0) {
                    callback.onSuccess(list);
                }
            }
        });
    }

    /**
     * 获取主界面小说\图片\视频数据
     *
     * @param query
     * @param callback
     */
    public void getData(AVQuery<Duanzi> query, final Callback callback) {
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new FindCallback<Duanzi>() {
            @Override
            public void done(List<Duanzi> list, AVException e) {
                if (list != null && list.size() != 0) {
                    callback.onSuccess(list);
                } else {
                    callback.onFailure();
                }
            }
        });
    }

    /**
     * 点击统计
     */
    public void clickStatistics(String id) {
        AVObject avObject = new AVObject("Click_Statistics");
        avObject.put("clickId", id);
        avObject.saveInBackground();
    }

}
