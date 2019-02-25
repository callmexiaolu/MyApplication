package com.example.myapplication.service;

import android.app.Activity;

/**
 * Create By LuKaiqi 2019/02/25
 * Describe:登录，注册服务
 */
public abstract class LoginService {


    /**
     * 用户登录
     * @param name 用户名字：可以是邮箱，手机号，邮箱任意一个
     * @param password
     */
    public abstract void userLogin(final String name, final String password, final Activity activity);

    /**
     * 用户注册，用户名，密码注册
     * @param name
     * @param password
     * @return
     */
    public abstract void userSignByName(final String name, final String password, final Activity activity);

    /**
     * 用户手机号免密登录
     * 登录成功后会自动注册
     * @param phoneNum
     * @param smsCode 验证码
     * @return
     */
    public abstract void userLoginOrSignByPhone(final String phoneNum, final String smsCode, final Activity activity);


    /**
     * 用户手机号合法验证
     * @param phone 用户手机号
     * @param countryCode 手机号国家代码
     * @return
     */
    public abstract boolean userPhoneNumberIsValid(final String phone, final String countryCode);


    /**
     * 发送验证码给用户
     * @param phoneNumber
     */
    public abstract void userGetPhoneCode(String phoneNumber);

    /**
     * 用户使用微信登录
     */
    public abstract void userLoginByWeChat();

    /**
     * 用户使用QQ登录
     */
    public abstract void userLoginByQQ();

}
