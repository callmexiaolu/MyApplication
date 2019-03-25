package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.myapplication.db.DBConfig;
import com.example.myapplication.db.UserTable;
import com.example.myapplication.dbutil.TableOperate;
import com.example.myapplication.presenter.MessageHandler;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.FileUtils;
import com.example.myapplication.util.SharedUil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;

public class MyApplication extends Application {

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Bmob后端云
        //Bmob.initialize(this, Contast.APP_KEY);
        sContext = getApplicationContext();
        //初始化SharedPreference
        SharedUil.init(sContext);
        //初始化BmobNewIM SDK 注册消息接收器 用户用户聊天模块
        //该初始化包含了 DataSDk的初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new MessageHandler(this));
        }
    }

    public static Context getAppContext() {
        return sContext;
    }

    /**
     * 获取当前运行的进程名
     * @return 进程名
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String processName = bufferedReader.readLine().trim();
            bufferedReader.close();
            return processName;
        } catch (Exception e) {
            Log.e(Contast.TAG, "MyApplication onCreate():获取当前运行进程名出错", e);
            return null;
        }
    }
}
