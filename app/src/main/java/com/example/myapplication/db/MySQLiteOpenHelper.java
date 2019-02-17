package com.example.myapplication.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.util.Contast;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static MySQLiteOpenHelper sHelper;

    private MySQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private MySQLiteOpenHelper(Context context, String name) {
        this(context, name, null, 1);
    }

    /**
     * 多线程单例模式
     * @param context
     * @return
     */
    public static synchronized MySQLiteOpenHelper getInstance(Context context) {
        if (sHelper == null) {
            sHelper = new MySQLiteOpenHelper(context, DBConfig.DB_NAME);
        }
        return sHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建用户表
        db.execSQL(DBConfig.CREATE_TABLE_LOGIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
