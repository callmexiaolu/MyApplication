package com.example.myapplication.bean;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Create by LuKaiqi on 2019/1/23.
 * function:二手书本帖子
 */
public class Book extends Post{

    private Double price;//书本价格

    //帖子中发帖人的头像务必从user中头像读取，避免user更新头像，发帖人头像不更新
    //可以在Bmob后端云数据库中设置9张图片，第一张图片为帖子封面，用户最多选择九张图片，保存对应图片到对应的数据中

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }
}
