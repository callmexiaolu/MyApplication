package com.example.myapplication.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.example.myapplication.MyApplication;
import com.example.myapplication.activity.MainActivity;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.bean.User;
import com.example.myapplication.db.UserTable;
import com.example.myapplication.dbutil.TableOperate;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Create by LuKaiqi on 2019/1/24.
 * function:用户工具类
 * 用户登录，注册，注销
 */
public class UserUtil {

    /**
     * 用户登录
     * @param name 用户名字：可以是邮箱，手机号，第三方平台唯一标志
     * @param password
     */
    public static void userLogin(final String name, final String password, final Activity activity) {
        //此处对密码进行加密操作,然后从服务器读取加密后的密码进行对比
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
            final String encryptPassword = EncryptUtil.encryptPassword(password);
            BmobUser.loginByAccount(name, encryptPassword, new LogInListener<MyBmobUser>() {
                @Override
                public void done(MyBmobUser myBmobUser, BmobException e) {
                    if (e == null) {
                        //登录成功
                        SharedUil.saveString(Contast.SHARED_USER_NAME_KEY, name);
                        SharedUil.saveString(Contast.SHARED_USER_PASSWORD_KEY, encryptPassword);
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                        ToastUtil.showToast(MyApplication.getAppContext(), "登录成功", true);
                    } else {
                        //登录失败
                        Log.d(Contast.TAG, "" + e);
                        if (e.getErrorCode() == 101) {
                            ToastUtil.showToast(MyApplication.getAppContext(), "用户名或密码错误", true);
                        } else {
                            ToastUtil.showToast(MyApplication.getAppContext(), "出错了，请稍后再试", true);
                        }
                    }
                }
            });
        } else {
            ToastUtil.showToast(MyApplication.getAppContext(), "不能为空", true);
        }
    }

    /**
     * 用户注册，用户名，密码注册
     * @param name
     * @param password
     * @return
     */
    public static void userRegisteredByName(final String name, final String password, final Activity activity) {
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
            final String encryptPassword = EncryptUtil.encryptPassword(password);
            MyBmobUser bmobUser = new MyBmobUser();
            bmobUser.setUsername(name);
            bmobUser.setPassword(encryptPassword);
            bmobUser.signUp(new SaveListener<MyBmobUser>() {
                @Override
                public void done(MyBmobUser myBmobUser, BmobException e) {
                    if (e == null) {
                        ToastUtil.showToast(MyApplication.getAppContext(), "注册成功!正在登录", true);
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    } else {
                        ToastUtil.showToast(MyApplication.getAppContext(), "出错了，请稍后重试", true);
                    }
                }
            });
        } else {
            ToastUtil.showToast(MyApplication.getAppContext(), "不能为空", true);
        }
    }

    /**
     * 用户手机号注册
     * @param phoneNum
     * @param smsCode 验证码
     * @return
     */
    public static boolean userRegisteredByPhone(String phoneNum, String smsCode) {
        final boolean[] flag = new boolean[1];
        BmobUser.signOrLoginByMobilePhone(phoneNum, smsCode, new LogInListener<MyBmobUser>() {
            @Override
            public void done(MyBmobUser myBmobUser, BmobException e) {
                flag[0] = e == null;
            }
        });
        return flag[0];
    }

    /**
     * 用户邮箱注册
     * @param email
     * @return
     */
    public static boolean userRegisteredByEmail(String email) {
        return true;
    }
}
