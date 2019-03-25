package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Comment;
import com.example.myapplication.util.StringUtil;

import java.util.List;

/**
 * Create by LuKaiqi on 2019/3/14.
 * function:帖子详情页评论列表适配器
 */
public class DiscussListViewAdapter extends BaseAdapter {

    private Context mContext;

    private List<Comment> mCommentList;

    public DiscussListViewAdapter(Context context, List<Comment> commentList) {
        mContext = context;
        mCommentList = commentList;
    }

    @Override
    public int getCount() {
        return mCommentList == null ? 0 : mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList != null ? mCommentList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyListViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lv_post_detail_discuss_item, parent, false);
            viewHolder = new MyListViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MyListViewHolder) convertView.getTag();
            viewHolder.setDiscussInfo(mCommentList.get(position), mContext);
        }
        return convertView;
    }

    static class MyListViewHolder{

        private ImageView mIvUserAvatar;
        private TextView mTvContent, mTvUserName, mTvTime;

        public MyListViewHolder(){ }

        public MyListViewHolder(View view) {
            mIvUserAvatar = view.findViewById(R.id.iv_post_detail_discuss_user_avatar);
            mTvContent = view.findViewById(R.id.tv_post_detail_discuss_content);
            mTvUserName = view.findViewById(R.id.tv_post_detail_discuss_user_name);
            mTvTime = view.findViewById(R.id.tv_post_detail_discuss_time);
        }

        public void setDiscussInfo(Comment comment, Context context) {
            if (StringUtil.isEmptyContainsSpace(comment.getUser().getAvatarFile())) {
                mIvUserAvatar.setImageResource(R.drawable.ic_action_me);
            } else {
                Glide.with(context).load(comment.getUser().getAvatarFile()).into(mIvUserAvatar);
            }
            mTvContent.setText(comment.getContent());
            mTvUserName.setText(comment.getUser().getUsername());
            mTvTime.setText(comment.getCreatedAt());
        }
    }
}
