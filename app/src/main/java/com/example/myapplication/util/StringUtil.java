package com.example.myapplication.util;


import android.text.TextUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Create by LuKaiqi on 2019/1/23.
 * function:加密工具类
 */
public class StringUtil {

    /**
     * MD5 加盐加密 盐值：lukaiqi  因此加密字符串为：password+lukaiqi
     * @param password 用户输入的密码
     * @return 加密后密码
     */
    public static String encryptPassword(String password) {
        String slat = "lukaiqi";
        if (TextUtils.isEmpty(password))
        {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest((password + slat).getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes)
            {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1)
                {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 字符串检测：非空,不能为非法字符"null"
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        if (string == null || string.trim().length() == 0 ||
                string.trim().equalsIgnoreCase("null") || string.contains(" ")) {
            return true;
        }
        return false;
    }


}
