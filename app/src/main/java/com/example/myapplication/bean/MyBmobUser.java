package com.example.myapplication.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Create by LuKaiqi on 2019/1/26.
 * function:
 */
public class MyBmobUser extends BmobUser {

    private BmobFile avatarfile;//用户头像

    public BmobFile getAvatarFile() {
        return avatarfile;
    }

    public void setAvatar(BmobFile avatarfile) {
        this.avatarfile = avatarfile;
    }

}
