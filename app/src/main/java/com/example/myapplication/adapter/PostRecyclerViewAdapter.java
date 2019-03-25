package com.example.myapplication.adapter;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:展示帖子列表所用适配器
 */

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Post;
import com.example.myapplication.widget.RoundRectImageView;

import java.util.List;


public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<Post> mPostList;
    private boolean isGridLayout = true;//true默认瀑布流, false为线性

    public PostRecyclerViewAdapter(Context context, List<Post> postList) {
        this.mContext = context;
        this.mPostList = postList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == 0) {
            view = LayoutInflater.from(mContext).inflate(R.layout.rv_fragment1_post_item, viewGroup, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.rv_fragment1_post_item_2, viewGroup, false);
        }
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
        return isGridLayout ? 0 : 1;
    }

    public void setLayoutType(boolean isGridLayout) {
        this.isGridLayout = isGridLayout;
    }

    public boolean getLayoutType() {
        return isGridLayout;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvPostTitle, mTvPostPrice, mTvPostLookCount, mTvPostUserName;
        private ImageView mIvPostUserAvatar;
        private RoundRectImageView mIvPostCover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvPostTitle = itemView.findViewById(R.id.tv_browse_post_title);
            mTvPostPrice = itemView.findViewById(R.id.tv_browse_post_price);
            mTvPostLookCount = itemView.findViewById(R.id.tv_browse_post_look);
            mTvPostUserName = itemView.findViewById(R.id.tv_browse_post_user_name);
            mIvPostUserAvatar = itemView.findViewById(R.id.iv_browse_post_user_avatar);
            mIvPostCover = itemView.findViewById(R.id.iv_browse_post_cover);
        }

        public void setPostInfo(Post post, Context context) {
            mTvPostTitle.setText(post.getTitle());
            mTvPostPrice.setText("¥" + String.valueOf(post.getPrice()));
            mTvPostLookCount.setText(String.valueOf(post.getLookCount()));
            mTvPostUserName.setText(post.getAuthor().getUsername());
            if (post.getAuthor().getAvatarFile() == null) {
                mIvPostUserAvatar.setImageResource(R.drawable.ic_action_me);
            } else {
                Glide.with(context).load(post.getAuthor().getAvatarFile()).into(mIvPostUserAvatar);
            }
            //设置帖子封面图片
            if (post.getPicturesUrl() != null) {
                Glide.with(context).load(post.getPicturesUrl().get(0)).into(mIvPostCover);
            }
        }


    }

    private IOnRecyclerViewListener mOnItemClickListener;

    public void setOnItemClickListener(IOnRecyclerViewListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

}
