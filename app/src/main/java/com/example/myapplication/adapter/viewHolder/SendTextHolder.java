package com.example.myapplication.adapter.viewHolder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.IOnRecyclerViewListener;
import com.example.myapplication.adapter.base.BaseViewHolder;
import com.example.myapplication.widget.RoundRectImageView;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Create by LuKaiqi on 2019/3/21.
 * function:发送消息为文字类型的布局
 */
public class SendTextHolder extends BaseViewHolder implements View.OnClickListener, View.OnLongClickListener{

    @BindView(R.id.round_iv_send_avatar)
    protected RoundRectImageView mRoundIvAvatar;

    @BindView(R.id.iv_send_fail_resend)
    protected ImageView mIvFailResend;

    @BindView(R.id.tv_send_time)
    protected TextView mTvSendTime;

    @BindView(R.id.tv_send_message)
    protected TextView mTvSendMessage;

    @BindView(R.id.tv_send_status)
    protected TextView mTvSendStatus;

    @BindView(R.id.progress_send)
    protected ProgressBar mProgressBarSend;

    private BmobIMConversation mConversation;

    public SendTextHolder(Context context, ViewGroup root, BmobIMConversation conversation, IOnRecyclerViewListener listener) {
        super(context, root, R.layout.rv_chat_activity_send_message_item, listener);
        this.mConversation = conversation;
    }

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        String avatar = info.getAvatar();
        String time = dateFormat.format(message.getCreateTime());
        String content = message.getContent();
        if (avatar != null) {
            Glide.with(mContext).load(avatar).into(mRoundIvAvatar);
        } else {
            Glide.with(mContext).load(R.drawable.ic_action_me).into(mRoundIvAvatar);
        }
        mTvSendTime.setText(time);
        mTvSendMessage.setText(content);

        //发送状态以及控件展示
        int status = message.getSendStatus();
        if (status == BmobIMSendStatus.SEND_FAILED.getStatus()) {
            mIvFailResend.setVisibility(View.VISIBLE);
            mProgressBarSend.setVisibility(View.GONE);
        } else if (status == BmobIMSendStatus.SENDING.getStatus()) {
            mIvFailResend.setVisibility(View.GONE);
            mProgressBarSend.setVisibility(View.VISIBLE);
        } else {
            mIvFailResend.setVisibility(View.GONE);
            mProgressBarSend.setVisibility(View.GONE);
        }

        mRoundIvAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击了发送方头像
            }
        });

        //重发消息
        mIvFailResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mConversation.resendMessage(message, new MessageSendListener() {

                    @Override
                    public void onStart(BmobIMMessage bmobIMMessage) {
                        mProgressBarSend.setVisibility(View.VISIBLE);
                        mIvFailResend.setVisibility(View.GONE);
                        mTvSendStatus.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                        if (e == null) {
                            mTvSendStatus.setVisibility(View.VISIBLE);
                            mTvSendStatus.setText("已发送");
                            mIvFailResend.setVisibility(View.GONE);
                            mProgressBarSend.setVisibility(View.GONE);
                        } else {
                            mIvFailResend.setVisibility(View.VISIBLE);
                            mProgressBarSend.setVisibility(View.GONE);
                            mTvSendStatus.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    /**
     * 是否显示发送时间
     * @param isShow
     */
    public void showTime(boolean isShow) {
        mTvSendTime.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

}
