package com.anna.duanzi.domain;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

/**
 * Created by TFL on 2016/1/22.
 */
@AVClassName("Duanzi")
public class Duanzi extends AVObject {

    public int type;

    public String title;

    public String content;

    public String category;

    public String createdAt;

    public String area;

    public AVFile data;

    public AVFile image;

    public String objectId;

}
