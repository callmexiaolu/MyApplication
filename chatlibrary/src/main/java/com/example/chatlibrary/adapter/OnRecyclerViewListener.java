package com.example.chatlibrary.adapter;

/**
 * Create by LuKaiqi on 2019/3/21.
 * function:RecyclerView添加点击事件
 */
public interface OnRecyclerViewListener {

    /**
     * 单击
     * @param position 点击item数据所处的下标
     */
    void onItemClick(int position);

    /**
     * 长按
     * @param position 点击item数据所处的下标
     * @return
     */
    boolean onItemLongClick(int position);

}
