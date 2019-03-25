package com.example.chatlibrary.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;

import androidx.fragment.app.FragmentActivity;
import android.view.Window;

/**
 * Create By LuKaiqi
 * Describe：Activity基类
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//只允许竖屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(setRootLayoutId());
        initViews();
        initData(savedInstanceState);
        initListener();
    }

    /**
     * 设置根布局id
     * @return 根布局id
     */
    public abstract int setRootLayoutId();

    /**
     * 绑定布局view
     */
    public abstract void initViews();

    /**
     * 初始化数据
     */
    public abstract void initData(@Nullable Bundle savedInstanceState);

    /**
     * 初始化监听器
     */
    public abstract void initListener();

}

