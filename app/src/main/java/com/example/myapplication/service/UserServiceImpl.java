package com.example.myapplication.service;

import android.util.Log;

import com.example.myapplication.MyApplication;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.SharedUil;
import com.example.myapplication.util.StringUtil;
import com.example.myapplication.util.ToastUtil;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class UserServiceImpl implements UserService {

    /**
     * 用户登录
     * @param name 用户名字：可以是邮箱，手机号，邮箱任意一个
     * @param password
     * @param done 接口回调关闭activity或者跳转
     */
    @Override
    public void userLogin(final String name, final String password, final IDoCallBack done) {
        //此处对密码进行加密操作,然后从服务器读取加密后的密码进行对比
        if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(password)) {
            final String encryptPassword = StringUtil.encryptPassword(password);
            BmobUser.loginByAccount(name, encryptPassword, new LogInListener<MyBmobUser>() {
                @Override
                public void done(MyBmobUser myBmobUser, BmobException e) {
                    if (e == null) {
                        //登录成功
                        SharedUil.saveString(Contast.SHARED_USER_NAME_KEY, name);
                        SharedUil.saveString(Contast.SHARED_USER_PASSWORD_KEY, encryptPassword);
                        ToastUtil.showToast(MyApplication.getAppContext(), "登录成功", true);
                        if (done != null) {
                            done.done();
                        }
                    } else {
                        //登录失败
                        Log.d(Contast.TAG, "" + e);
                        if (e.getErrorCode() == 101) {
                            ToastUtil.showToast(MyApplication.getAppContext(), "用户名或密码错误", true);
                        } else {
                            if (done != null) {
                                done.doFailed();
                            }
                        }
                    }
                }
            });
        } else {
            ToastUtil.showToast(MyApplication.getAppContext(), "用户名或密码不能为空", true);
        }
    }

    /**
     * 用户注册
     * @param name 用户名
     * @param password 密码
     * @param done
     */
    @Override
    public void userSignByName(final String name, final String password, final IDoCallBack done) {
        if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(password)) {
            final String encryptPassword = StringUtil.encryptPassword(password);
            MyBmobUser bmobUser = new MyBmobUser();
            bmobUser.setUsername(name);
            bmobUser.setPassword(encryptPassword);
            bmobUser.signUp(new SaveListener<MyBmobUser>() {
                @Override
                public void done(MyBmobUser myBmobUser, BmobException e) {
                    if (e == null) {
                        ToastUtil.showToast(MyApplication.getAppContext(), "注册成功!正在登录", true);
                        if (done != null) {
                            done.done();
                        }
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
     * 通过手机一键登录或者注册
     * @param phoneNum
     * @param smsCode 验证码
     * @param done
     */
    @Override
    public void userLoginOrSignByPhone(String phoneNum, String smsCode, final IDoCallBack done) {
        if (!StringUtil.isEmpty(phoneNum) && !StringUtil.isEmpty(smsCode)) {
            MyBmobUser user = new MyBmobUser();
            user.setMobilePhoneNumber(phoneNum);
            user.setUsername(phoneNum);
            user.setPassword(StringUtil.encryptPassword("666666"));
            BmobUser.signOrLoginByMobilePhone(phoneNum, smsCode, new LogInListener<MyBmobUser>() {
                @Override
                public void done(MyBmobUser myBmobUser, BmobException e) {
                    if (e == null) {
                        if (done != null) {
                            done.done();
                        }
                    } else {
                        if (done != null) {
                            done.doFailed();
                        }
                    }
                }
            });
        }

    }

    /**
     * 用户手机号合法验证
     * @param phone 用户手机号
     * @param countryCode 手机号国家代码
     * @return
     */
    @Override
    public boolean userPhoneNumberIsValid(final String phone, final String countryCode) {
        //免密登录
        //1.先判断是否有手机号
        //2.手机号是否合法
        if (!StringUtil.isEmpty(phone)) {
            PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber phoneNumber = numberUtil.parse(countryCode + phone, countryCode);
                return numberUtil.isValidNumber(phoneNumber);
            } catch (NumberParseException e) {
                Log.e(Contast.TAG, "" + e);
                return false;
            }
        }
        return false;
    }

    /**
     * 发送验证码给用户
     * @param phoneNumber
     */
    @Override
    public void userGetPhoneCode(String phoneNumber) {
        if (userPhoneNumberIsValid(phoneNumber, "+86")) {
            BmobSMS.requestSMSCode(phoneNumber, "GBook", new QueryListener<Integer>() {
                @Override
                public void done(Integer smsId, BmobException e) {
                    if (e == null) {
                        ToastUtil.showToast(MyApplication.getAppContext(), "验证码已发送", true);
                    } else {
                        Log.e(Contast.TAG, "" + e);
                        ToastUtil.showToast(MyApplication.getAppContext(), "出错了,请稍后再试", true);
                    }
                }
            });
        } else {
            ToastUtil.showToast(MyApplication.getAppContext(), "手机号码格式错误", true);
        }
    }

    /**
     * 微信登录
     */
    @Override
    public void userLoginByWeChat() {

    }

    /**
     * QQ登录
     */
    @Override
    public void userLoginByQQ() {

    }

}
