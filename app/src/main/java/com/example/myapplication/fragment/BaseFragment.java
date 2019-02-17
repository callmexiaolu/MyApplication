package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.MyApplication;

import java.lang.reflect.Field;

/**
 * Create by LuKaiqi on 2019/1/31.
 * function:封装fragment。
 * 封装创建与推出过程。
 */
public abstract class BaseFragment extends Fragment {

    private View mRootView;//保存根试图，用于复用


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            View view = inflater.inflate(setLayoutId(), container, false);
            initViews(view);
            initData();
            initListener();
            mRootView = view;
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public abstract int setLayoutId();

    public abstract void initViews(View view);

    public abstract void initData();

    public abstract void initListener();

}
