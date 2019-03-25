package com.example.myapplication.adapter.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by LuKaiqi on 2019/3/24.
 * function:搭配BaseRecyclerAdapter一起使用
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    /**
     * SparseArray比HashMap更省内存，在某些条件下性能更好
     * 具体详细可以网络查看
     */
    private final SparseArray<View> mViews;

    public int mLayoutId;

    public BaseRecyclerHolder(int layoutId, View itemView) {
        super(itemView);
        this.mLayoutId = layoutId;
        this.mViews = new SparseArray<>(8);
    }

    public SparseArray<View> getAllView() {
        return mViews;
    }

    /**
     * 根据id获取view实例
     * @param viewId view的id
     * @return
     */
    protected <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置控件展示的文字
     * @param viewId 相关控件的id
     * @param text 需要显示的文字
     * @return
     */
    public BaseRecyclerHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 设置View能否响应事件
     * @param viewId 控件id
     * @param enable
     * @return
     */
    public BaseRecyclerHolder setEnabled(int viewId,boolean enable){
        View v = getView(viewId);
        v.setEnabled(enable);
        return this;
    }

    /**
     * 点击事件
     * @param viewId
     * @param listener
     * @return
     */
    public BaseRecyclerHolder setOnClickListener(int viewId, View.OnClickListener listener){
        View v = getView(viewId);
        v.setOnClickListener(listener);
        return this;
    }

    /**
     * 设置控件是否可见
     * @param viewId 控件id
     * @param visibility 是否可见
     * @return
     */
    public BaseRecyclerHolder setVisible(int viewId,int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    /**
     * 设置imageView图片展示
     * @param viewId 相关控件id
     * @param drawableId 图片资源id
     * @return
     */
    public BaseRecyclerHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);
        return this;
    }

    /**
     * @param viewId 相关控件id
     * @param bm 需要展示的bitmap对象
     * @return
     */
    public BaseRecyclerHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * @param avatar 图片的url
     * @param defaultRes 图片url无法展示，将使用该默认图片展示，即默认图片资源id
     * @param viewId 需要展示图片的控件id
     * @return
     */
    public BaseRecyclerHolder setImageView(String avatar, int defaultRes, int viewId, Context context) {
        ImageView iv = getView(viewId);
        if (!TextUtils.isEmpty(avatar)) {
            Glide.with(context).load(avatar).into(iv);
        } else {
            iv.setImageResource(defaultRes);
        }
        return this;
    }
}
