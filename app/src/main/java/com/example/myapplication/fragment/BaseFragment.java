package com.example.myapplication.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
            ButterKnife.bind(this, view);
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
