package com.example.myapplication.presenter;

import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.exception.BmobException;

/**
 * Create by LuKaiqi on 2019/3/20.
 * function:
 */
public abstract class UpdateCacheListener extends BmobListener1 {

    public abstract void done(BmobException e);

    @Override
    protected void postDone(Object o, BmobException e) {
        done(e);
    }
}
