package com.example.myapplication.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * 帖子,基类
 */
public class Post extends BmobObject {

    private String content;//帖子内容
    private String title;//标题
    private BmobFile cover;//封面图片
    private Integer lookCount;//帖子浏览量
    private Integer thumbUp;//帖子点赞数
    private BmobRelation thumbUpRelation;//点赞关联--关联到哪些用户点赞
    private Integer collect;//帖子收藏数
    private BmobRelation collectRelation;//收藏关联-关联到哪些用户收藏
    private MyBmobUser author;//发帖人
    private BmobRelation relation;//关联， 该帖子关联哪个用户
    private String category;//帖子种类
    private List<String> picturesUrl;//帖子照片链接

    //帖子中发帖人的头像务必从user中头像读取，避免user更新头像，发帖人头像不更新

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public MyBmobUser getAuthor() {
        return author;
    }

    public void setAuthor(MyBmobUser author) {
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getPicturesUrl() {
        return picturesUrl;
    }

    public void setPicturesUrl(List<String> picturesUrl) {
        this.picturesUrl = picturesUrl;
    }

    public Integer getThumbUp() {
        return thumbUp;
    }

    public void setThumbUp(Integer thumbUp) {
        this.thumbUp = thumbUp;
    }

    public Integer getCollect() {
        return collect;
    }

    public void setCollect(Integer collect) {
        this.collect = collect;
    }

    public BmobRelation getThumbUpRelation() {
        return thumbUpRelation;
    }

    public void setThumbUpRelation(BmobRelation thumbUpRelation) {
        this.thumbUpRelation = thumbUpRelation;
    }

    public BmobRelation getCollectRelation() {
        return collectRelation;
    }

    public void setCollectRelation(BmobRelation collectRelation) {
        this.collectRelation = collectRelation;
    }
}
