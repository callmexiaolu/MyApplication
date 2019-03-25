package com.example.myapplication.model;

import androidx.annotation.Nullable;

import com.example.myapplication.util.StringUtil;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * Create by LuKaiqi on 2019/1/26.
 * function:
 */
public class MyBmobUser extends BmobUser implements Serializable {

    private String avatar;//用户头像url

    private String signature;//用户签名

    private String fans;//粉丝数

    private String postCount;//帖子数量

    private String followCount;//关注数

    private String thumbUp;//获赞数

    public String getPostCount() {
        return StringUtil.isEmpty(postCount) ? "0" : postCount;
    }

    public void setPostCount(String postCount) {
        this.postCount = postCount;
    }

    public String getFollowCount() {
        return StringUtil.isEmpty(followCount) ? "0" : followCount;
    }

    public void setFollowCount(String followCount) {
        this.followCount = followCount;
    }

    public String getThumbUp() {
        return StringUtil.isEmpty(thumbUp) ? "0" : thumbUp;
    }

    public void setThumbUp(String thumbUp) {
        this.thumbUp = thumbUp;
    }

    public String getFans() {
        return StringUtil.isEmpty(fans) ? "0" : fans;
    }

    public void setFans(String fans) {
        this.fans = fans;
    }

    public String getSignature() {
        return StringUtil.isEmpty(signature) ? "添加个人签名!" : signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAvatarFile() {
        return avatar;
    }

    public void setAvatar(String avatarfile) {
        this.avatar = avatarfile;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        MyBmobUser other = (MyBmobUser) obj;
        return this.getObjectId().equals(other.getObjectId());
    }

}
