package com.example.myapplication.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Create by LuKaiqi on 2019/1/26.
 * function:
 */
public class MyBmobUser extends BmobUser {

    private BmobFile avatar;//用户头像

    private String signature;//用户签名

    private String fans;//粉丝数

    private String postCount;//帖子数量

    private String followCount;//关注数

    private String thumbUp;//获赞数

    public String getPostCount() {
        return postCount;
    }

    public void setPostCount(String postCount) {
        this.postCount = postCount;
    }

    public String getFollowCount() {
        return followCount;
    }

    public void setFollowCount(String followCount) {
        this.followCount = followCount;
    }

    public String getThumbUp() {
        return thumbUp;
    }

    public void setThumbUp(String thumbUp) {
        this.thumbUp = thumbUp;
    }



    public String getFans() {
        return fans == null ? "0" : fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getSignature() {
        return signature == null ? "添加个人签名!" : signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public BmobFile getAvatarFile() {
        return avatar;
    }

    public void setAvatar(BmobFile avatarfile) {
        this.avatar = avatarfile;
    }

}
