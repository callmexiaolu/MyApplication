package com.example.myapplication.adapter;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.bean.Post;

import java.util.List;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Post> mPostList;

    public MyRecyclerViewAdapter(Context context, List<Post> postList) {
        this.mContext = context;
        this.mPostList = postList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        if (mPostList != null && mPostList.size() > 0) {
            Post post = mPostList.get(i);
            myViewHolder.setPostInfo(post, mContext);
            if (mOnItemClickListener != null) {
                myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(myViewHolder.itemView, myViewHolder.getLayoutPosition());
                    }
                });
                myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemClickListener.onItemLongClick(myViewHolder.itemView, myViewHolder.getLayoutPosition());
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvPostTitle, mTvPostPrice, mTvPostCollect, mTvPostUserName;
        private ImageView mIvPostUserAvatar;
        private ImageView mIvPostCover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvPostTitle = itemView.findViewById(R.id.tv_browse_post_title);
            mTvPostPrice = itemView.findViewById(R.id.tv_browse_post_price);
            mTvPostCollect = itemView.findViewById(R.id.tv_browse_post_collect);
            mTvPostUserName = itemView.findViewById(R.id.tv_browse_post_user_name);
            mIvPostUserAvatar = itemView.findViewById(R.id.iv_browse_post_user_avatar);
            mIvPostCover = itemView.findViewById(R.id.iv_browse_post_cover);
        }

        public void setPostInfo(Post post, Context context) {
            mTvPostTitle.setText(post.getTitle());
            mTvPostPrice.setText("￥" + String.valueOf(post.getPrice()));
            mTvPostCollect.setText(String.valueOf(post.getCollect()) + "收藏");
            mTvPostUserName.setText(post.getAuthor().getUsername());
            if (post.getAuthor().getAvatarFile() == null) {
                mIvPostUserAvatar.setImageResource(R.drawable.ic_action_me);
            } else {
                Glide.with(context).load(post.getAuthor().getAvatarFile().getUrl()).into(mIvPostUserAvatar);
            }
            //设置帖子封面图片
            if (post.getPicturesUrl() != null) {
                Glide.with(context).load(post.getPicturesUrl().get(0)).into(mIvPostCover);
            }
        }


    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * RecyclerView点击时间回调
     *          *点击监听
     *          *长按监听
     */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
