package com.example.myapplication.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.myapplication.R;
import com.example.myapplication.activity.MainActivity;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;

/**
 * Create by LuKaiqi on 2019/3/15.
 * function:消息接收器
 *          处理服务器发来的消息和离线消息
 *          应用于用户聊天模块
 */
public class MessageHandler extends BmobIMMessageHandler {

    private Context mContext;

    public MessageHandler(Context context) {
        mContext = context;
    }

    /**
     * 接收处理在线消息
     * @param messageEvent
     */
    @Override
    public void onMessageReceive(MessageEvent messageEvent) {
        executeMessage(messageEvent);
    }

    /**
     * 接收处理离线消息
     * @param offlineMessageEvent
     */
    @Override
    public void onOfflineReceive(OfflineMessageEvent offlineMessageEvent) {
        super.onOfflineReceive(offlineMessageEvent);
    }

    /**
     * 处理消息
     *
     * @param event
     */
    private void executeMessage(final MessageEvent event) {
        //检测用户信息是否需要更新
        ChatService chatService = new ChatServiceImpl();
        chatService.updateChatAndInfo(event, new IDoCallBack() {
            @Override
            public void done() {
                BmobIMMessage message = event.getMessage();
                if (BmobIMMessageType.getMessageTypeValue(message.getMsgType()) == 0) {
                    //自定义消息类型处理

                } else {
                    //Bmob SDK内部支持的消息类型
                    processSDKMessage(message, event);
                }
            }

            @Override
            public void doing(int totalProgress) {

            }

            @Override
            public void doFailed() {

            }
        });
    }

    /**
     * 处理SDK支持的消息
     *
     * @param msg
     * @param event
     */
    private void processSDKMessage(BmobIMMessage msg, MessageEvent event) {
        if (BmobNotificationManager.getInstance(mContext).isShowNotification()) {
            //如果需要显示通知栏，SDK提供以下两种显示方式：
            Intent pendingIntent = new Intent(mContext, MainActivity.class);
            pendingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


            //TODO 消息接收：8.5、多个用户的多条消息合并成一条通知：有XX个联系人发来了XX条消息
            //BmobNotificationManager.getInstance(context).showNotification(event, pendingIntent);

            //TODO 消息接收：8.6、自定义通知消息：始终只有一条通知，新消息覆盖旧消息
            BmobIMUserInfo info = event.getFromUserInfo();
            //这里可以是应用图标，也可以将聊天头像转成bitmap
            Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher);
            BmobNotificationManager.getInstance(mContext).showNotification(largeIcon,
                    info.getName(), msg.getContent(), "您有一条新消息", pendingIntent);
        } else {
            //直接发送消息事件
            EventBus.getDefault().post(event);
        }
    }


}
