package com.example.myapplication.bean;


public class User{

    //用户名，密码，邮箱，手机号，邮箱验证状态，手机号验证状态都内置在BmobUser中
    //MyBmobUser集成了BmobUser

    private String avatarpath;//用户头像本地路径

    private String username;

    private String password;

    public String getAvatarPath() {
        return avatarpath;
    }

    public void setAvatarPath(String avatarpath) {
        this.avatarpath = avatarpath;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getUsername() {
        return username;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
