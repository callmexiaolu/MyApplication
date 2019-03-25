package com.example.myapplication.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.bmob.newim.bean.BmobIMConversationType;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.adapter.base.BaseRecyclerAdapter;
import com.example.myapplication.adapter.base.BaseRecyclerHolder;
import com.example.myapplication.adapter.base.ImutlipleItem;
import com.example.myapplication.model.Conversation;
import com.example.myapplication.widget.RoundRectImageView;

import java.util.Collection;
import java.util.List;

/**
 * Create by LuKaiqi on 2019/3/20.
 * function:展示会话列表所用适配器
 */
public class ConversationRecyclerViewAdapter extends BaseRecyclerAdapter<Conversation> {

    public ConversationRecyclerViewAdapter(Context context, ImutlipleItem<Conversation> items, Collection<Conversation> datas) {
        super(context, items, datas);
    }

    /**
     * 获取指定会话类型指定会话id的会话位置
     * @param type
     * @param targetId
     * @return
     */
    public int findPosition(BmobIMConversationType type, String targetId) {
        int index = this.getCount();
        int position = -1;
        while(index-- > 0) {
            if((getItem(index)).getConversationType().equals(type) && (getItem(index)).getConversationId().equals(targetId)) {
                position = index;
                break;
            }
        }
        return position;
    }

    @Override
    public void bindView(BaseRecyclerHolder holder, Conversation conversation, int position) {
        //设置接收到的最后一条信息内容
        holder.setText(R.id.tv_conversation_last_message, conversation.getLastMessageContent());
        //设置接收到的最后一条信息时间
        holder.setText(R.id.tv_conversation_last_time, String.valueOf(conversation.getLastMessageTime()));
        //设置会话图标
        Object object = conversation.getAvatar();
        if (object instanceof String) {
            String avatar = (String) object;
            holder.setImageView(avatar, R.drawable.ic_action_me, R.id.round_iv_conversation_avatar, super.mContext);
        }
        //设置会话标题， 即用户名
        holder.setText(R.id.tv_conversation_user_name, conversation.getConversationName());
        //查询指定未读消息数
        long unReadCount = conversation.getUnReadCount();
        if (unReadCount > 0) {

        } else {
            
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private RoundRectImageView mRectImageViewAvatar, mRectImageViewCover;

        private TextView mTvName, mTvMessage, mTvLastTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mRectImageViewAvatar = itemView.findViewById(R.id.round_iv_conversation_avatar);
            mRectImageViewCover = itemView.findViewById(R.id.round_iv_conversation_product_cover);
            mTvName = itemView.findViewById(R.id.tv_conversation_user_name);
            mTvMessage = itemView.findViewById(R.id.tv_conversation_last_message);
            mTvLastTime = itemView.findViewById(R.id.tv_conversation_last_time);
        }

        public void setConversationItemInfo(Context context, Conversation conversation) {
            if (conversation.getAvatar() != null) {
                Glide.with(context).load(conversation.getAvatar()).into(mRectImageViewAvatar);
            } else {
                Glide.with(context).load(R.drawable.ic_action_me).into(mRectImageViewAvatar);
            }
            mTvName.setText(conversation.getConversationName());
            mTvMessage.setText(conversation.getLastMessageContent());
            mTvLastTime.setText((int) conversation.getLastMessageTime());
        }
    }
}
