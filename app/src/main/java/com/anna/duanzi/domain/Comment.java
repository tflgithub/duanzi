package com.anna.duanzi.domain;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

import java.util.Date;

/**
 * Created by tfl on 2016/10/19.
 */
@AVClassName("Comment")
public class Comment extends AVObject {

    public String commmentId;

    public String commentContent;

    public String createdAt;

    public String user;
}
