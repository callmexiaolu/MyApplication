package com.example.myapplication.bean;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Create by LuKaiqi on 2019/1/23.
 * function:二手书本帖子
 */
public class Book extends Post{

    public Book() {
        this.setTableName("Post");
    }

    public Book(String title, String content, Double price, String category) {
        this.setTitle(title);
        this.setContent(content);
        this.setPrice(price);
        this.setCategory(category);
    }

    private Double price;//书本价格

    //帖子中发帖人的头像务必从user中头像读取，避免user更新头像，发帖人头像不更新

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }
}
