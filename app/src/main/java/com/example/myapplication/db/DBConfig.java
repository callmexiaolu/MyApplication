package com.example.myapplication.db;

/**
 * 数据库配置
 */
public class DBConfig {

    public static final String DB_NAME = "transaction.db";

    //创建用户表
    public static final String CREATE_TABLE_LOGIN =
            "CREATE TABLE IF NOT EXISTS " + UserTable.TABLE_USER + "("
                    + UserTable.USER_ID + " integer not null primary key autoincrement,"
                    + UserTable.USER_NAME + " varchar(20),"
                    + UserTable.USER_PASSWORD + " text,"
                    + UserTable.USER_AVATAR + " text)";

}
