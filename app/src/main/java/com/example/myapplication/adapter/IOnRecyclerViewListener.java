package com.example.myapplication.adapter;

import android.view.View;

/**
 * Create by LuKaiqi on 2019/3/21.
 * function:RecyclerView点击事件回调
 *           *点击监听
 *           *长按监听
 */
public interface IOnRecyclerViewListener {

    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}
