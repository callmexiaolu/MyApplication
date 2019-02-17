package com.example.myapplication;

import android.app.Application;
import android.content.Context;

import com.example.myapplication.db.DBConfig;
import com.example.myapplication.db.UserTable;
import com.example.myapplication.dbutil.TableOperate;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.SharedUil;

import cn.bmob.v3.Bmob;

public class MyApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Bmob后端云
        Bmob.initialize(this, Contast.APP_KEY);
        sContext = getApplicationContext();
        //初始化SharedPreference
        SharedUil.init(sContext);
    }

    public static Context getAppContext() {
        return sContext;
    }
}
