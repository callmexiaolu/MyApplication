package com.example.myapplication.presenter;

import android.util.Log;

import com.example.myapplication.model.MyBmobUser;
import com.example.myapplication.util.Contast;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMImageMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Create by LuKaiqi on 2019/3/15.
 * function: 聊天功能
 */
public class ChatServiceImpl implements ChatService {

    /**
     * 发送文本消息
     * @param textMessage 需要发送的消息
     * @param conversation 消息管理器
     * @param listener 消息发送回调监听
     */
    @Override
    public void chatSendTextMessage(String textMessage, BmobIMConversation conversation, MessageSendListener listener) {
        BmobIMTextMessage message = new BmobIMTextMessage();
        conversation.sendMessage(message, listener);
    }

    /**
     * 发送图片信息
     * @param picturePath 图片的路径
     * @param conversation 消息管理器
     * @param listener 消息发送回调监听
     */
    @Override
    public void chatSendPictureMessage(List<String> picturePath, BmobIMConversation conversation, MessageSendListener listener) {
        for (String path : picturePath) {
            BmobIMImageMessage imImageMessage = new BmobIMImageMessage(path);
            conversation.sendMessage(imImageMessage, listener);
        }

    }

    /**
     * 发送语音信息
     */
    @Override
    public void chatSendAudioMessage() {

    }

    /**
     * 发送视频信息
     */
    @Override
    public void chatSendVideoMessage() {

    }

    /**
     * 更新会话资料和用户信息
     * @param event
     * @param callBack
     */
    @Override
    public void updateChatAndInfo(MessageEvent event, IDoCallBack callBack) {
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo userInfo = event.getFromUserInfo();
        final BmobIMMessage message = event.getMessage();
        String userName = userInfo.getName();
        String avatar = userInfo.getAvatar();
        String title = conversation.getConversationTitle();
        String icon = conversation.getConversationIcon();
        if (!userName.equals(title) || (avatar != null && !avatar.equals(icon))) {
            UserService userService = new UserServiceImpl();
            userService.queryUserInfo(userInfo.getUserId(), new UserService.IQueryUserListener() {
                @Override
                public void succeed(MyBmobUser user) {
                    if (user != null) {
                        String name = user.getUsername();
                        String avatar = user.getAvatarFile();
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        userInfo.setName(name);
                        userInfo.setAvatar(avatar);
                        //用户管理:更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().updateUserInfo(userInfo);
                        //会话:更新会话资料
                        BmobIM.getInstance().updateConversation(conversation);
                    }
                }

                @Override
                public void failed(BmobException e) {
                    Log.e(Contast.TAG, "ChatServiceImpl updateChatAndInfo has Error:", e);
                }
            });
        }
    }
}
