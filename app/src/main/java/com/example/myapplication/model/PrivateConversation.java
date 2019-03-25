package com.example.myapplication.model;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.myapplication.R;
import com.example.myapplication.activity.ChatActivity;
import com.example.myapplication.util.StringUtil;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobConversationType;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMConversationType;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;

/**
 * Create by LuKaiqi on 2019/3/20.
 * function:私聊
 */
public class PrivateConversation extends Conversation {

    private BmobIMConversation mConversation;

    private BmobIMMessage mLastMessage;

    public PrivateConversation(BmobIMConversation conversation) {
        this.mConversation = conversation;
        mConversationType = BmobIMConversationType.setValue(conversation.getConversationType());
        mConversationId = conversation.getConversationId();
        if (mConversationType == BmobIMConversationType.PRIVATE) {
            mConversationName = conversation.getConversationTitle();
            if (TextUtils.isEmpty(mConversationName)) {
                mConversationName = mConversationId;
            }
        } else {
            mConversationName = "未知会话";
        }
        List<BmobIMMessage> messages = conversation.getMessages();
        if (messages != null && messages.size() > 0) {
            mLastMessage = messages.get(0);
        }
    }

    @Override
    public Object getAvatar() {
        if (mConversationType == BmobIMConversationType.PRIVATE){
            String avatar =  mConversation.getConversationIcon();
            if (TextUtils.isEmpty(avatar)){//头像为空，使用默认头像
                return R.drawable.ic_action_me;
            }else{
                return avatar;
            }
        }else{
            return R.drawable.ic_action_me;
        }
    }

    @Override
    public long getLastMessageTime() {
        if (mLastMessage != null) {
            return mLastMessage.getCreateTime();
        } else {
            return 0;
        }
    }

    @Override
    public String getLastMessageContent() {
        if (mLastMessage != null) {
            String content = mLastMessage.getContent();
            if (mLastMessage.getMsgType().equals(BmobIMMessageType.TEXT.getType()) || mLastMessage.getMsgType().equals("agree")) {
                return content;
            }else if(mLastMessage.getMsgType().equals(BmobIMMessageType.IMAGE.getType())){
                return "[图片]";
            }else if(mLastMessage.getMsgType().equals(BmobIMMessageType.VOICE.getType())){
                return "[语音]";
            }else if(mLastMessage.getMsgType().equals(BmobIMMessageType.LOCATION.getType())){
                return"[位置]";
            }else if(mLastMessage.getMsgType().equals(BmobIMMessageType.VIDEO.getType())){
                return "[视频]";
            }else{//开发者自定义的消息类型，需要自行处理
                return "[未知]";
            }
        }else{//防止消息错乱
            return "";
        }
    }

    @Override
    public int getUnReadCount() {
        //查询指定会话下的未读消息数量
        return (int) BmobIM.getInstance().getUnReadCount(mConversation.getConversationId());
    }

    @Override
    public void readAllMessage() {
        mConversation.updateLocalCache();
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.CHAT_KEY, mConversation);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onLongClick(Context context) {
        //删除会话
        BmobIM.getInstance().deleteConversation(mConversation.getConversationId());
    }
}
