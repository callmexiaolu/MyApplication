package com.example.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.example.myapplication.adapter.base.BaseViewHolder;
import com.example.myapplication.adapter.viewHolder.ReceiveTextHolder;
import com.example.myapplication.adapter.viewHolder.SendTextHolder;
import com.example.myapplication.model.MyBmobUser;
import com.example.myapplication.util.Contast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.v3.BmobUser;

/**
 * Create by LuKaiqi on 2019/3/23.
 * function:聊天界面适配器
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //文本
    private final int TYPE_RECEIVED_TEXT = 0;
    private final int TYPE_SEND_TEXT = 1;

    /**
     * 显示时间间隔：10分钟
     */
    private final long TIME_INTERVAL = 10 * 60 * 1000;

    private List<BmobIMMessage> mMessageList = new ArrayList<>();

    private BmobIMConversation mConversation;

    private String mCurrentUserId = "";

    public ChatAdapter(Context context, BmobIMConversation conversation) {
        try {
            mCurrentUserId = BmobUser.getCurrentUser(MyBmobUser.class).getObjectId();
        } catch (Exception e) {
            Log.e(Contast.TAG, "ChatAdapter construct has Error:", e);
        }
        this.mConversation = conversation;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SEND_TEXT) {
            return new SendTextHolder(parent.getContext(), parent, mConversation, mOnRecyclerViewListener);
        } else if (viewType == TYPE_RECEIVED_TEXT) {
            return new ReceiveTextHolder(parent.getContext(), parent, mOnRecyclerViewListener);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((BaseViewHolder) holder).bindData(mMessageList.get(position));
        if (holder instanceof ReceiveTextHolder) {
            ((ReceiveTextHolder) holder).showTime(shouldShowTime(position));
        } else if (holder instanceof SendTextHolder) {
            ((SendTextHolder) holder).showTime(shouldShowTime(position));
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        BmobIMMessage message = mMessageList.get(position);
        if (message.getMsgType().equals(BmobIMMessageType.TEXT.getType())) {
            return message.getFromId().equals(mCurrentUserId) ? TYPE_SEND_TEXT: TYPE_RECEIVED_TEXT;
        } else {
            return -1;
        }
    }

    private IOnRecyclerViewListener mOnRecyclerViewListener;

    /**
     * 设置Recycler点击/长按事件
     * @param listener
     */
    public void setOnRecyclerViewListener(IOnRecyclerViewListener listener) {
        this.mOnRecyclerViewListener = listener;
    }

    /**
     * 根据message寻找下标
     * @param message
     * @return
     */
    public int findPosition(BmobIMMessage message) {
        int index = this.getCount();
        int position = -1;
        while (index-- > 0) {
            if (message.equals(this.getItemMessage(index))) {
                position = index;
                break;
            }
        }
        return position;
    }

    /**
     * 根据itemId获取下标
     * @param id
     * @return
     */
    public int findPosition(long id) {
        int index = this.getCount();
        int position = -1;
        while (index-- > 0) {
            if (this.getItemId(index) == id) {
                position = index;
                break;
            }
        }
        return position;
    }

    /**
     * 对话数量
     * @return
     */
    public int getCount() {
        return this.mMessageList == null ? 0 : this.mMessageList.size();
    }

    /**
     * 是否应该显示时间戳
     * @param position 消息下表
     * @return true 显示时间  false 不显示时间
     */
    private boolean shouldShowTime(int position) {
        if (position == 0) {
            return true;
        }
        long lastTime = mMessageList.get(position - 1).getCreateTime();
        long currentTime = mMessageList.get(position).getCreateTime();
        return currentTime - lastTime > TIME_INTERVAL;
    }

    /**
     * 获取第一条消息
     * @return
     */
    public BmobIMMessage getFirstMessage() {
        if (mMessageList != null && mMessageList.size() > 0) {
            return mMessageList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取某条信息
     */
    public BmobIMMessage getItemMessage(int position) {
        if (mMessageList == null || position >= mMessageList.size()) {
            return null;
        } else {
            return mMessageList.get(position);
        }
    }

    /**
     * 添加信息
     * @param messages
     */
    public void addMessages(List<BmobIMMessage> messages) {
        mMessageList.addAll(0, messages);
        notifyDataSetChanged();
    }

    public void addMessage(BmobIMMessage message) {
        mMessageList.addAll(Arrays.asList(message));
        notifyDataSetChanged();
    }

    /**
     * 移除消息
     * @param position
     */
    public void removeMessage(int position) {
        mMessageList.remove(position);
        notifyDataSetChanged();
    }

}
