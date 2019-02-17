package com.example.myapplication.db;

import android.database.sqlite.SQLiteDatabase;
import com.example.myapplication.MyApplication;

/**
 * 数据库管理，获取数据库对象
 */
public class DBManager {

    private static DBManager sDBManager;
    private MySQLiteOpenHelper mMySQLiteOpenHelper;
    private SQLiteDatabase db;

    private DBManager() {
        //创建数据库
        mMySQLiteOpenHelper = MySQLiteOpenHelper.getInstance(MyApplication.getAppContext());
        if (db == null) {
            db = mMySQLiteOpenHelper.getWritableDatabase();
        }
    }

    /**
     * 单例模式获取DBManager
     * @return
     */
    public static DBManager newInstance() {
        if (sDBManager == null) {
            sDBManager = new DBManager();
        }
        return sDBManager;
    }

    /**
     * 获取数据库对象
     * @return
     */
    public SQLiteDatabase getDataBase() {
        return db;
    }
}
