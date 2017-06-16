package com.anna.duanzi.domain;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by tfl on 2016/10/20.
 */
@AVClassName("Fiction")
public class Fiction extends AVObject{

    public String name;

    public String state;

    public String readProcess;

    public String readPermission;
}
