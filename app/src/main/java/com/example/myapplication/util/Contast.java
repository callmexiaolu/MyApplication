package com.example.myapplication.util;


public class Contast {

    /**应用Application_Key*/
    public static final String APP_KEY = "7620d754d414c483527eb9108d60c75f";

    /**日志打印TAG*/
    public static final String TAG = "MR_LU";

    /**SharedPreference名字 */
    public static final String SHARED_NAME = "setting";

    /**Shared 保存的用户名 key*/
    public static final String SHARED_USER_NAME_KEY = "userName";

    /**Shared 保存的用户名密码 key*/
    public static final String SHARED_USER_PASSWORD_KEY = "password";

    /**Shared 保存的用户头像本地路径 key*/
    public static final String SHARED_USER_AVATAR_KEY = "avatarPath";

    /** 发帖时，选择照片最大数量 */
    public static final int MAX_SELECTED_NUMBER = 9;

    /** 帖子类别 */
    public static final String[] POST_CATEGORY = new String[]{"二手书", "服装", "电子产品", "彩妆"};

    /** 帖子类别key */
    public static final String POST_CATEGORY_KEY = "category";

    /** 帖子布局切换  true 瀑布流 false 列表 */
    public static volatile boolean isGridLayout = true;

}
