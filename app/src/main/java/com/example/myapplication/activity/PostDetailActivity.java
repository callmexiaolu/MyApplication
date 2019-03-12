package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.bean.Post;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.ToastUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Create by LuKaiqi on 2019/3/7.
 * function:帖子详情页
 */
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String POST_ID = "post";//相对应帖子，由intent传递过来

    private static int USER_ACTION = -1; // 0表示点赞，1表示评论，2表示收藏

    private String postId;

    private static boolean isThumbUp = false;
    private Post mPost;
    private BmobRelation mBmobRelation;
    private MyBmobUser mCurrentUser;

    private ImageView mIvBack, mIvPostDetailUserAvatar, mIvPostDetailThumb, mIvPostDetailCollect;
    private ScrollView mSvPostDetailContent;
    private RelativeLayout mRlPostDetailUser;
    private LinearLayout  mLlPostDetailThumb, mLlPostDetailDiscuss, mLlPostDetailCollect, mLlPostDetailPictures;
    private TextView mTvPostDetailUserName, mTvPostDetailCreateDate, mTvPostDetailPrice, mTvPostDetailBusiness, mTvPostDetailContent;



    @Override
    public int setRootLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    public void initViews() {
        mIvBack = findViewById(R.id.include_post_detail_back);

        mRlPostDetailUser = findViewById(R.id.rl_post_detail_user);
        mTvPostDetailUserName = findViewById(R.id.tv_post_detail_user_name);
        mIvPostDetailUserAvatar = findViewById(R.id.iv_post_detail_user_avatar);
        mTvPostDetailCreateDate = findViewById(R.id.tv_post_detail_create_data);

        mTvPostDetailPrice = findViewById(R.id.tv_post_detail_price);
        mTvPostDetailContent = findViewById(R.id.tv_post_detail_post_content);

        mSvPostDetailContent = findViewById(R.id.sv_post_detail_content);
        mLlPostDetailPictures = findViewById(R.id.ll_post_detail_post_pictures);

        mLlPostDetailThumb = findViewById(R.id.ll_post_detail_thumb);
        mIvPostDetailThumb = findViewById(R.id.iv_post_detail_thumb);

        mLlPostDetailDiscuss = findViewById(R.id.ll_post_detail_discuss);

        mLlPostDetailCollect = findViewById(R.id.ll_post_detail_collect);
        mIvPostDetailCollect = findViewById(R.id.iv_post_detail_collect);

        mTvPostDetailBusiness = findViewById(R.id.tv_post_detail_business);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        try {
            mPost = (Post) getIntent().getExtras().getSerializable(POST_ID);
            postId = mPost.getObjectId();
            mBmobRelation = mPost.getThumbUpRelation() == null ? new BmobRelation() : mPost.getThumbUpRelation();
            initPostDetailsLayout();//设置帖子详情页面布局
        } catch (Exception e) {
            Log.e(Contast.TAG, "帖子详情initData()出错:" + e);
        }
    }

    @Override
    public void initListener() {
        setView(mIvBack, true);
        setView(mRlPostDetailUser, true);
        setView(mLlPostDetailThumb, true);
        setView(mLlPostDetailDiscuss, true);
        setView(mLlPostDetailCollect, true);
        setView(mTvPostDetailBusiness, true);
    }

    @Override
    protected void onResume() {
        checkThumbUp();
        super.onResume();
    }

    private void setView(View view, boolean isUsed) {
        view.setFocusable(isUsed);
        view.setClickable(isUsed);
        view.setOnClickListener(this);
    }

    /**
     * 设置帖子详情，图片，用户信息等
     */
    private void initPostDetailsLayout() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 10;
        MyBmobUser user = mPost.getAuthor();
        //加载用户头像
        if (user.getAvatarFile() == null) {
            mIvPostDetailUserAvatar.setImageResource(R.drawable.ic_action_me);
        } else {
            Glide.with(this).load(user.getAvatarFile()).into(mIvPostDetailUserAvatar);
        }
        mTvPostDetailUserName.setText(user.getUsername());
        mTvPostDetailCreateDate.setText("更新于" + mPost.getUpdatedAt());
        mTvPostDetailPrice.setText("￥" + mPost.getPrice());
        mTvPostDetailContent.setText(mPost.getContent());
        for (int i = 0; i < mPost.getPicturesUrl().size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            Glide.with(this).load(mPost.getPicturesUrl().get(i)).into(imageView);
            mLlPostDetailPictures.addView(imageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_post_detail_back:  //点击返回键
                this.finish();
                break;

            case R.id.rl_post_detail_user:  //点击用户，跳转到用户详情页
                break;

            case R.id.ll_post_detail_thumb: //点赞，点了赞就不能取消
                setThumbUp();
                break;

            case R.id.ll_post_detail_discuss:   //留言，评论
                discuss();
                break;

            case R.id.ll_post_detail_collect:   //收藏
                collect();
                break;

            case R.id.tv_post_detail_business: //咨询交易
                business();
                break;

        }
    }

    /**
     * 检查点赞状态。即查询点赞用户列表中是否包含当前用户
     *          *如果用户没有登录，那么就不会触发该方法，因此用户登录成功后点赞的时候应该重新再检查一次
     */
    private void checkThumbUp() {
        if (BmobUser.isLogin()) {
            mCurrentUser = BmobUser.getCurrentUser(MyBmobUser.class);
            BmobQuery<MyBmobUser> query = new BmobQuery<>();
            Post post = new Post();
            post.setObjectId(postId);
            query.addWhereRelatedTo("thumbUpRelation", new BmobPointer(post));
            Log.d(Contast.TAG, "检查点赞状态");
            query.findObjects(new FindListener<MyBmobUser>() {
                @Override
                public void done(List<MyBmobUser> list, BmobException e) {
                    if (e == null) {
                        //先检查该用户是否已经点过赞
                        Log.d(Contast.TAG, "检查点赞状态2" + "点赞用户数量:" + list.size() + "用户:" + list);
                        if (list.contains(mCurrentUser) ) {//用户点过了赞
                            Log.d(Contast.TAG, "用户点过了赞");
                            isThumbUp = true;
                            mIvPostDetailThumb.setImageResource(R.drawable.ic_action_thumb_up_select);
                        } else {
                            isThumbUp = false;
                        }
                        Log.d(Contast.TAG, "" + isThumbUp);
                    }
                }
            });
        }
    }

    /**
     * 帖子点赞
     */
    private void setThumbUp() {
        checkThumbUp();
        if (BmobUser.isLogin()) {
            if (isThumbUp) {
                ToastUtil.showToast(PostDetailActivity.this, "点了就别取消嘛,老铁", true);
                return;
            }
            mBmobRelation.add(mCurrentUser);
            mPost.setThumbUpRelation(mBmobRelation);
            mPost.setLookCount(mPost.getLookCount() + 1);
            mPost.setThumbUp(mPost.getThumbUp() + 1);
            USER_ACTION = 0;
            isThumbUp = true;
            mPost.update(mUpdateListener);
        } else {
            startActivity(new Intent(PostDetailActivity.this, LoginOrSignActivity.class));
        }
    }

    /**
     * 帖子评论留言
     */
    private void discuss() {

    }

    /**
     * 帖子收藏
     */
    private void collect() {

    }

    /**
     * 咨询交易
     */
    private void business() {

    }

    /**
     * 帖子更新回调
     */
    private UpdateListener mUpdateListener = new UpdateListener() {
        @Override
        public void done(BmobException e) {
            if (e == null) {
                if (USER_ACTION == 0) {
                    mIvPostDetailThumb.setImageResource(R.drawable.ic_action_thumb_up_select);
                } else if (USER_ACTION == 1) {

                } else if (USER_ACTION == 2) {

                }
            } else {
                Log.e(Contast.TAG, "帖子详情操作出错:" + e);
                ToastUtil.showToast(PostDetailActivity.this, "出错了,稍后再试", true);
            }
        }
    };

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
