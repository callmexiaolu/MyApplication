package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.MyApplication;
import com.example.myapplication.util.Contast;

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
        Log.d(Contast.TAG, "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(Contast.TAG, "onCreateView: ");
        if (mRootView == null) {
            View view = inflater.inflate(setLayoutId(), container, false);
            initViews(view);
            initData();
            initListener();
            mRootView = view;
        }
        return mRootView;
    }

    public abstract int setLayoutId();

    public abstract void initViews(View view);

    public abstract void initData();

    public abstract void initListener();

}
