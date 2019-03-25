package com.example.myapplication.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.adapter.IOnRecyclerViewListener;
import com.example.myapplication.adapter.base.BaseRecyclerHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by LuKaiqi on 2019/3/24.
 * function:recyclerView适配器基类
 *      *可扩展多种item布局
 *      *支持长按/点击事件
 *      *可添加自定义头部布局
 */
public abstract class BaseRecyclerAdapter<T>  extends RecyclerView.Adapter<BaseRecyclerHolder> {

    /**
     * 默认布局
     */
    private final int TYPE_DEFAULT = 0;

    /**
     * 当数据为空的时候显示的布局
     */
    private final int TYPE_HEADER = 1;

    /**
     * 多重布局
     */
    private final int TYPE_MUTIPLE = 2;

    /**
     * 带header的多重布局
     */
    private final int TYPE_MUTIPLE_HEADER = 3;

    protected final Context mContext;

    protected List<T> mLists;

    protected ImutlipleItem<T> mItems;

    protected IOnRecyclerViewListener mIOnRecyclerViewListener;

    public BaseRecyclerAdapter(Context context, ImutlipleItem<T> items, Collection<T> datas) {
        this.mContext = context;
        this.mItems = items;
        this.mLists = datas == null ? new ArrayList<>() : new ArrayList<>(datas);
    }

    /**
     * 绑定数据
     * @param datas 数据源
     * @return
     */
    public BaseRecyclerAdapter<T> bindDatas(Collection<T> datas) {
        if (datas != null) {
            mLists = new ArrayList<>(datas);
        } else {
            mLists = new ArrayList<>();
        }
        notifyDataSetChanged();
        return this;
    }

    /**
     * 删除数据
     * @param position
     */
    public void removeData(int position) {
        int more = getItemCount() - mLists.size();
        mLists.remove(position - more);
        notifyDataSetChanged();
    }

    /**
     * 获取指定position的item
     * @param postion
     * @return
     */
    public T getItem(int postion) {
        int more = getItemCount() - mLists.size();
        return mLists.get(postion - more);
    }

    /**
     * 获取item总数
     * @return
     */
    public int getCount() {
        return this.mLists == null ? 0 : this.mLists.size();
    }

    @NonNull
    @Override
    public BaseRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = mItems.getItemLayoutId(viewType);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View rootView = inflater.inflate(layoutId, parent, false);
        return new BaseRecyclerHolder(layoutId, rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolder holder, int position) {
        int type = getViewTypeByPosition(position);
        if (type == TYPE_HEADER) {
            bindView(holder, null, position);
        } else if (type == TYPE_MUTIPLE) {
            bindView(holder, mLists.get(position), position);
        } else if (type == TYPE_MUTIPLE_HEADER) {
            int headerCount = getItemCount() - mLists.size();
            bindView(holder, mLists.get(position - headerCount), position);
        } else {
            bindView(holder, null, position);
        }
        holder.itemView.setOnClickListener(getOnclickListener(position));
        holder.itemView.setOnLongClickListener(getOnLongClickListener(position));
    }

    @Override
    public int getItemCount() {
        //有多重布局则采用多重布局
        if (mItems != null) {
            return mItems.getItemCount(mLists);
        }
        return mLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type = getViewTypeByPosition(position);
        if(type==TYPE_HEADER){
            return mItems.getItemViewType(position, null);
        }else if(type==TYPE_MUTIPLE){
            return mItems.getItemViewType(position, mLists.get(position));
        }else if(type==TYPE_MUTIPLE_HEADER){
            int headerCount = getItemCount() - mLists.size();
            return mItems.getItemViewType(position, mLists.get(position - headerCount));
        }else{
            return 0;
        }
    }

    /**
     * 获取指定position的布局类型
     * @param position 指定position的item
     * @return 布局类型
     */
    private int getViewTypeByPosition(int position) {
        //默认布局
        if (mItems == null) {
            return TYPE_DEFAULT;
        } else { // 多布局
            if (mLists != null && mLists.size() > 0) {
                if (getItemCount() > mLists.size()) { // 是否有自定义的Header
                    int headerCount = getItemCount() - mLists.size();
                    if (position >= headerCount) {//当前位置大于header个数
                        return TYPE_MUTIPLE_HEADER;
                    } else {
                        //当前点击的是header
                        return TYPE_HEADER;
                    }
                } else {
                    return TYPE_MUTIPLE;
                }
            } else {
                //mLists没有数据
                return TYPE_HEADER;
            }
        }
    }

    /**
     * 设置点击/长按事件监听事件
     * @param recyclerViewListener 监听回调接口
     */
    public void setIOnRecyclerViewListener(IOnRecyclerViewListener recyclerViewListener) {
        this.mIOnRecyclerViewListener = recyclerViewListener;
    }

    public View.OnClickListener getOnclickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIOnRecyclerViewListener != null && v != null) {
                    mIOnRecyclerViewListener.onItemClick(v, position);
                }
            }
        };
    }

    public View.OnLongClickListener getOnLongClickListener(final int position) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mIOnRecyclerViewListener != null && v != null) {
                    mIOnRecyclerViewListener.onItemLongClick(v, position);
                }
                return true;
            }
        };
    }

    /**
     * 绑定item
     * @param holder
     * @param item
     * @param position
     */
    public abstract void bindView(BaseRecyclerHolder holder, T item, int position);

}
