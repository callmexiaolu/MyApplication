package com.example.myapplication.presenter;

import java.util.List;

import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageSendListener;

/**
 * Create by LuKaiqi on 2019/3/15.
 * function:聊天功能
 */
public interface ChatService {

    /**
     * 发送文本消息
     * @param textMessage 需要发送的消息
     */
    void chatSendTextMessage(String textMessage, BmobIMConversation conversation, MessageSendListener listener);

    /**
     * 发送图片信息
     * @param picturePath 图片的路径
     */
    void chatSendPictureMessage(List<String> picturePath, BmobIMConversation conversation, MessageSendListener listener);

    /**
     * 发送语音信息
     */
    void chatSendAudioMessage();

    /**
     * 发送视频消息
     */
    void chatSendVideoMessage();

    /**
     * 更新会话资料和用户资料
     * @param event
     * @param callBack
     */
    void updateChatAndInfo(MessageEvent event, IDoCallBack callBack);
}
