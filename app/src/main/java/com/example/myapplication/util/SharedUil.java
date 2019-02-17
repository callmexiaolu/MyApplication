package com.example.myapplication.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:SharePerference的使用包装
 */
public class SharedUil {

    private static Context sContext;

    private static SharedPreferences sSharedPreferences;

    private static SharedPreferences.Editor sEditor;

    public static void init(Context context) {
        sContext = context;
        sSharedPreferences = sContext.getSharedPreferences(Contast.SHARED_NAME, Context.MODE_PRIVATE);
        sEditor = sSharedPreferences.edit();
    }

    public static void saveString(String key, String value) {
        sEditor.putString(key, value).apply();
    }

    public static String getString(String key, String normal) {
        return sSharedPreferences.getString(key, normal);
    }


    public static String getString(String key) {
        return sSharedPreferences.getString(key, "");
    }


    public static void saveInteger(String key, int value) {
        sEditor.putInt(key, value).apply();
    }

    public static int getInteger(String key, int normal) {
        return sSharedPreferences.getInt(key, normal);
    }

    public static int getInteger(String key) {
        return sSharedPreferences.getInt(key, 0);
    }


    public static Boolean getBoolean(String key) {
        return sSharedPreferences.getBoolean(key, true);
    }

    public static Boolean getBoolean(String key, boolean b) {
        return sSharedPreferences.getBoolean(key, b);
    }

    public static void setBoolean(String key, boolean value) {
        sEditor.putBoolean(key, value).apply();
    }

    /**
     * 移除key
     * @param key
     */
    public static void remove(String key) {
        sEditor.remove(key).apply();
    }

}
