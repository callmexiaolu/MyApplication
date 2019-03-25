package com.example.myapplication.adapter.base;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.adapter.IOnRecyclerViewListener;

import butterknife.ButterKnife;

/**
 * Create by LuKaiqi on 2019/3/21.
 * function:聊天界面消息view基类
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    IOnRecyclerViewListener mRecyclerViewListener;

    protected Context mContext;

    public BaseViewHolder(Context context, ViewGroup root, int layoutResource, IOnRecyclerViewListener listener) {
        super(LayoutInflater.from(context).inflate(layoutResource, root, false));
        this.mContext = context;
        ButterKnife.bind(this, itemView);
        this.mRecyclerViewListener = listener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public abstract void bindData(T t);

    @Override
    public void onClick(View v) {
        if (mRecyclerViewListener != null) {
           // mRecyclerViewListener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mRecyclerViewListener != null) {
            //mRecyclerViewListener.onItemLongClick(getAdapterPosition());
        }
        return true;
    }
}
