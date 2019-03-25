package com.example.myapplication.model;

import android.content.Context;
import androidx.annotation.Nullable;

import java.io.Serializable;

import cn.bmob.newim.bean.BmobIMConversationType;

/**
 * Create by LuKaiqi on 2019/3/20.
 * function:会话
 */
public abstract class Conversation implements Serializable, Comparable{

    /** 会话id */
    protected String mConversationId;

    /** 会话类型 */
    protected BmobIMConversationType mConversationType;

    /** 会话名称 */
    protected String mConversationName;

    public String getConversationConver() {
        return mConversationConver;
    }

    public void setConversationConver(String conversationConver) {
        mConversationConver = conversationConver;
    }

    /** 会话封面 */
    protected String mConversationConver;

    /** 获取头像 */
    abstract public Object getAvatar();

    /** 获取最后一条消息的时间 */
    abstract public long getLastMessageTime();

    /** 获取最后一条消息的内容 */
    abstract public String getLastMessageContent();

    /** 获取未读会话个数 */
    abstract public int getUnReadCount();

    /** 将所有消息标记为已读 */
    abstract public void readAllMessage();

    abstract public void onClick(Context context);

    abstract public void onLongClick(Context context);

    public String getConversationId() {
        return mConversationId;
    }

    public BmobIMConversationType getConversationType() {
        return mConversationType;
    }

    public String getConversationName() {
        return mConversationName;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Conversation that = (Conversation) obj;
        if (!mConversationId.equals(that.mConversationId)) return false;
        return mConversationType == that.mConversationType;
    }

    @Override
    public int hashCode() {
        int result = mConversationId.hashCode();
        result = 31 * result + mConversationType.hashCode();
        return result;
    }


    @Override
    public int compareTo(Object another) {
        if (another instanceof Conversation){
            Conversation anotherConversation = (Conversation) another;
            long timeGap = anotherConversation.getLastMessageTime() - getLastMessageTime();
            if (timeGap > 0) return  1;
            else if (timeGap < 0) return -1;
            return 0;
        }else{
            throw new ClassCastException();
        }
    }
}
