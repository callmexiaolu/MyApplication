package com.example.chatlibrary.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Create by LuKaiqi on 2019/3/20.
 * function:用作RecyclerView适配器的ViewHolder
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    OnRecyclerViewListener mRecyclerViewListener;

    protected Context mContext;

    public BaseViewHolder(Context context, ViewGroup root, int layoutResource, OnRecyclerViewListener listener) {
        super(LayoutInflater.from(context).inflate(layoutResource, root, false));
        this.mContext = context;

    }
}
