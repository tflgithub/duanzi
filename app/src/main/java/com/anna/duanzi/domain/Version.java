package com.anna.duanzi.domain;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;

/**
 * Created by tfl on 2017/8/25.
 */
@AVClassName("version")
public class Version extends AVObject {

    public AVFile apkFile;

    public String upgradeDesc;

    public Integer versionCode;
}
