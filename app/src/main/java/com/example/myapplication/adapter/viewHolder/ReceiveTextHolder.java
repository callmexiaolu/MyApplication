package com.example.myapplication.adapter.viewHolder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.IOnRecyclerViewListener;
import com.example.myapplication.adapter.base.BaseViewHolder;
import com.example.myapplication.widget.RoundRectImageView;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * Create by LuKaiqi on 2019/3/21.
 * function:接收文字消息
 */
public class ReceiveTextHolder extends BaseViewHolder {

    @BindView(R.id.round_iv_receive_avatar)
    protected RoundRectImageView mRoundIvUserAvatar;

    @BindView(R.id.tv_receive_time)
    protected TextView mTvReceivedTime;

    @BindView(R.id.tv_receive_message)
    protected TextView mTvReceivedMessage;

    public ReceiveTextHolder(Context context, ViewGroup root, IOnRecyclerViewListener listener) {
        super(context, root, R.layout.rv_chat_activity_received_message, listener);
    }

    @OnClick({R.id.round_iv_receive_avatar})
    public void onReceivedAvatar(View view) {

    }

    @Override
    public void bindData(Object o) {
        final BmobIMMessage message = (BmobIMMessage) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = dateFormat.format(message.getCreateTime());
        mTvReceivedTime.setText(time);
        final BmobIMUserInfo info = message.getBmobIMUserInfo();
        String avatar = info.getAvatar();
        if (avatar != null) {
            Glide.with(mContext).load(avatar).into(mRoundIvUserAvatar);
        } else {
            Glide.with(mContext).load(R.drawable.ic_action_me).into(mRoundIvUserAvatar);
        }
        String content = message.getContent();
        mTvReceivedMessage.setText(content);
        mRoundIvUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (info != null) {
                    //点击了聊天头像
                }
            }
        });
    }

    /**
     * 选择是否显示接收信息的时间
     * @param isShow
     */
    public void showTime(boolean isShow) {
        mTvReceivedTime.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
