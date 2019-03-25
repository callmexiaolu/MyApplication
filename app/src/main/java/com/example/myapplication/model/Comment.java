package com.example.myapplication.model;

import cn.bmob.v3.BmobObject;

/**
 * Create by LuKaiqi on 2019/3/12.
 * function:评论
 */
public class Comment extends BmobObject {

    //评论内容
    private String content;

    //评论的用户
    private MyBmobUser user;

    //所评论的帖子
    private Post post;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MyBmobUser getUser() {
        return user;
    }

    public void setUser(MyBmobUser user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
