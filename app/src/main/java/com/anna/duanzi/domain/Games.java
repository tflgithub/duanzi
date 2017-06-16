package com.anna.duanzi.domain;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

/**
 * Created by tfl on 2016/11/9.
 */
@AVClassName("Games")
public class Games extends AVObject {

    public static int STAND_ALONE = 1;//单机

    public static int ONLINE = 2;//网游

    public String name;

    public String desc;

    public AVFile imageFile;

    public AVFile gameFile;

    public int type;
}
