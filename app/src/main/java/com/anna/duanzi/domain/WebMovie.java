package com.anna.duanzi.domain;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by Administrator on 2017/8/28.
 */
@AVClassName("WebMovies")
public class WebMovie  extends AVObject {

    public String url;

    public String title;

    public String objectId;
}
