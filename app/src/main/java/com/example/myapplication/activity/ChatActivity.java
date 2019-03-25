package com.example.myapplication.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatAdapter;
import com.example.myapplication.model.MyBmobUser;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.BmobRecordManager;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * Create by LuKaiqi on 2019/3/15.
 * function:私聊模块
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, MessageListHandler {

    private ImageView mIvBack, mIvMike, mIvEmoticon, mIvAdd;

    private TextView mTvUserName;

    private SwipeRefreshLayout mSrlChatContent;

    private RecyclerView mRvChatContent;

    private LinearLayoutManager mLayoutManager;

    private ChatAdapter mChatAdapter;

    private EditText mEtTextMessage;

    private Button mBtnSend;

    private final MyBmobUser mCurrentUser = BmobUser.getCurrentUser(MyBmobUser.class);

    public static final String CHAT_KEY = "chat";

    private BmobIMConversation mConversationManager;

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public void initViews() {
        mIvBack = findViewById(R.id.include_chat_activity_back);
        mTvUserName = findViewById(R.id.tv_chat_activity_user_name);
        mSrlChatContent = findViewById(R.id.srl_chat_activity_content);
        mRvChatContent = findViewById(R.id.rv_chat_activity_content);
        mIvMike = findViewById(R.id.iv_chat_activity_mike);
        mEtTextMessage = findViewById(R.id.et_chat_activity_text_message);
        mIvEmoticon = findViewById(R.id.iv_chat_activity_emoticon);
        mIvAdd = findViewById(R.id.iv_chat_activity_add);
        mBtnSend = findViewById(R.id.btn_chat_activity_send);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        try {
            //获取到会话信息
            BmobIMConversation conversation = (BmobIMConversation) getIntent().getExtras().getSerializable(CHAT_KEY);
            Log.d(Contast.TAG, "ChatActivity initData() conversation = " + conversation);
            //初始化消息管理
            mConversationManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversation);
            mTvUserName.setText(conversation.getConversationTitle());
            initSwipeLayout();
            chat();
        } catch (Exception e) {
            Log.e(Contast.TAG, "initData has Error:", e);
        }

    }

    @Override
    public void initListener() {
        setViewClick(mIvBack);
        setViewClick(mIvMike);
        setViewClick(mIvEmoticon);
        setViewClick(mIvAdd);
        setViewClick(mBtnSend);
        mEtTextMessage.addTextChangedListener(mTextWatcher);
        //下拉刷新监听
        mSrlChatContent.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BmobIMMessage message = mChatAdapter.getFirstMessage();
                queryMessages(message);
            }
        });
    }

    /**
     * 设置控件是否能点击和获取焦点,以及设置点击监听
     * @param view
     */
    private void setViewClick(View view) {
        view.setFocusable(true);
        view.setClickable(true);
        view.setOnClickListener(this);
    }

    /**
     * 初始化下拉布局以及适配器
     */
    private void initSwipeLayout() {
        mSrlChatContent.setEnabled(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRvChatContent.setLayoutManager(mLayoutManager);
        mChatAdapter = new ChatAdapter(this, mConversationManager);
        mRvChatContent.setAdapter(mChatAdapter);
        //自动刷新
        mSrlChatContent.setRefreshing(true);
        queryMessages(null);
    }

    @Override
    protected void onResume() {
        //锁屏时候接收的消息也应该添加到聊天界面中
        //addUnReadMessageToChat();
        //添加页面消息监听器
        BmobIM.getInstance().addMessageListHandler(this);
        //清楚通知栏通知
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();

    }

    @Override
    protected void onPause() {
        //移除页面消息监听器
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //标记此会话的所有消息为已读状态
        if (mConversationManager != null) {
            mConversationManager.updateLocalCache();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_chat_activity_back:
                this.finish();
                break;

            case R.id.iv_chat_activity_mike:
                break;

            case R.id.iv_chat_activity_emoticon:
                break;

            case R.id.iv_chat_activity_add:
                break;

            case R.id.btn_chat_activity_send:
                sendMessage();
                Log.d(Contast.TAG, "点击了发送");
                break;
        }
    }

    /**
     * 输入框监听，监听有无文字输入
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count == 0 && start == 0) {
                mIvAdd.setVisibility(View.VISIBLE);
                mBtnSend.setVisibility(View.GONE);
            } else {
                mIvAdd.setVisibility(View.GONE);
                mBtnSend.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 开始聊天
     */
    private void chat() {

    }

    /**
     * 发送消息
     */
    private void sendMessage() {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            ToastUtil.showToast(this, "未连接到服务器",true);
            return;
        }
        String text = mEtTextMessage.getText().toString();
        BmobIMTextMessage textMessage = new BmobIMTextMessage();
        textMessage.setContent(text);
        mConversationManager.sendMessage(textMessage,mMessageSendListener);
    }

    /**
     * 消息发送监听
     */
    public MessageSendListener mMessageSendListener = new MessageSendListener() {
        @Override
        public void done(BmobIMMessage bmobIMMessage, BmobException e) {
            //消息发送完毕，成功发送。更新聊天界面
            mChatAdapter.notifyDataSetChanged();
            scrollTOBottom();
        }

        @Override
        public void onStart(BmobIMMessage bmobIMMessage) {
            super.onStart(bmobIMMessage);
            mChatAdapter.addMessage(bmobIMMessage);
            mEtTextMessage.setText("");
            scrollTOBottom();
        }

        @Override
        public void onProgress(int i) {
            //文件类型消息才有进度
            //即:图片，视频，音频，文件
            super.onProgress(i);
        }
    };

    /**
     * 滑动到界面底部
     */
    private void scrollTOBottom() {
        mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getItemCount() - 1, 0);
    }

    /**
     * 查询对话消息
     *      tips:首次加载，可以设置message为空，默认取消息表第一个message作为刷新的起始时间点，默认按照消息时间的降序排序
     * @param message
     */
    public void queryMessages(BmobIMMessage message) {
        mConversationManager.queryMessages(message, 10, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                mSrlChatContent.setRefreshing(false);
                if (e == null) {
                    if (list != null && list.size() > 0) {
                        mChatAdapter.addMessages(list);
                        mLayoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                    }
                } else {
                    Log.e(Contast.TAG, "查询对话消息出错: ", e);
                }
            }
        });
    }

    /**
     * 聊天消息接收
     * @param list
     */
    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        for (int i = 0; i < list.size(); i++) {
            addMessageToChat(list.get(i));
        }
    }

    /**
     * 添加信息到聊天界面
     * @param event
     */
    private void addMessageToChat(MessageEvent event) {
        BmobIMMessage message = event.getMessage();
        //判断是否是当前对话
        if (mConversationManager != null &&
                event != null &&
                mConversationManager.getConversationId().equals(event.getConversation().getConversationId())) {
            if (mChatAdapter.findPosition(message) < 0) { //如果没有添加到聊天界面
                mChatAdapter.addMessage(message);
                //更新会话已读状态
                mConversationManager.updateReceiveStatus(message);
            }
            scrollTOBottom();
        }
    }
}
