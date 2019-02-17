package com.example.myapplication.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    /**
     * 消息弹出提示
     * @param context 当前Activity的context
     * @param message 弹出消息的内容
     * @param isShort true:弹出时间短 false:弹出时间长
     */
    public static void showToast(Context context, String message, boolean isShort) {
        if (isShort) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }

}
