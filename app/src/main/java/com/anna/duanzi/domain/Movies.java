package com.anna.duanzi.domain;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by Administrator on 2016/11/21.
 */
@AVClassName("Movies")
public class Movies extends AVObject {

    public String name;

    public String title;

    public String category;
}
