package com.example.myapplication.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * 帖子,基类
 */
public class Post extends BmobObject {

    private String describe;//帖子描述
    private String title;//标题
    private BmobFile cover;//封面图片
    private Integer lookCount;//帖子浏览量
    private User author;//发帖人
    private BmobRelation relation;//关联， 该帖子关联哪个用户

    //帖子中发帖人的头像务必从user中头像读取，避免user更新头像，发帖人头像不更新
    //可以在Bmob后端云数据库中设置9张图片，第一张图片为帖子封面，用户最多选择九张图片，保存对应图片到对应的数据中

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BmobFile getCover() {
        return cover;
    }

    public void setCover(BmobFile cover) {
        this.cover = cover;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public BmobRelation getRelation() {
        return relation;
    }

    public void setRelation(BmobRelation relation) {
        this.relation = relation;
    }

    public Integer getLookCount() {
        return lookCount;
    }

    public void setLookCount(Integer lookCount) {
        this.lookCount = lookCount;
    }
}
