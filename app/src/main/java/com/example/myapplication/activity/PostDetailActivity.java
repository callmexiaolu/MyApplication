package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.adapter.DiscussListViewAdapter;
import com.example.myapplication.bean.Comment;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.bean.Post;
import com.example.myapplication.service.PostService;
import com.example.myapplication.service.PostServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.StringUtil;
import com.example.myapplication.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

import cn.bmob.v3.datatype.BmobRelation;


/**
 * Create by LuKaiqi on 2019/3/7.
 * function:帖子详情页
 */
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String POST_ID = "post";//相对应帖子，由intent传递过来

    /**
     *  0表示点赞，1表示添加评论，2表示添加收藏, 3表示咨询交易, 4表示查询评论
    */
    public static final int USER_ACTION_THUMBUP = 0;

    public static final int USER_ACTION_DISCUSS = 1;

    public static final int USER_ACTION_COLLECT =2;

    public static final int USER_ACTION_BUSINESS = 3;

    public static final int USER_ACTION_QUERY_DISCUSS = 4;

    private String postId;

    private PostService mPostService = new PostServiceImpl();

    private boolean isThumbUp = false;
    private Post mPost;

    private MyBmobUser mCurrentUser;

    private InputMethodManager inputMethodManager;//输入法管理

    private ImageView
            mIvBack,
            mIvPostDetailUserAvatar,
            mIvPostDetailThumb,
            mIvPostDetailCollect;

    private ScrollView mSvPostDetailContent;

    private ListView mLvPostDetailDiscussShow;

    private DiscussListViewAdapter mListViewAdapter;

    private RelativeLayout mRlPostDetailUser;

    private LinearLayout
            mLlPostDetailThumb,
            mLlPostDetailCollect,
            mLlPostDetailPictures,
            mLlPostDetailDiscuss,
            mLlPostDetailBottom,
            mLlPostDetailDiscussInput;

    private TextView
            mTvPostDetailUserName,
            mTvPostDetailCreateDate,
            mTvPostDetailPrice,
            mTvPostDetailBusiness,
            mTvPostDetailContent;

    private EditText mEtPostDetailDiscuss;

    private Button mBtnDiscussSubmit;

    private List<Comment> mCommentList = new ArrayList<>();

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

        mLlPostDetailDiscussInput = findViewById(R.id.ll_post_detail_discuss_input);
        mEtPostDetailDiscuss = findViewById(R.id.et_post_detail_discuss_content);
        mBtnDiscussSubmit = findViewById(R.id.btn_post_detail_discuss_submit);

        mLvPostDetailDiscussShow = findViewById(R.id.lv_post_detail_discuss_show);

        mLlPostDetailBottom = findViewById(R.id.ll_post_detail_bottom);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            mCurrentUser = BmobUser.getCurrentUser(MyBmobUser.class);
            mPost = (Post) getIntent().getExtras().getSerializable(POST_ID);

            //帖子浏览量+1
            mPost.setLookCount(mPost.getLookCount() + 1);
            mPostService.postUpdate(mPost);

            postId = mPost.getObjectId();

            mPostService.postDiscussQuery(postId, mActionListener);
            mListViewAdapter = new DiscussListViewAdapter(this, mCommentList);
            mLvPostDetailDiscussShow.setAdapter(mListViewAdapter);

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
        setView(mLlPostDetailDiscussInput, true);
        mBtnDiscussSubmit.setOnClickListener(this);
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
        mTvPostDetailPrice.setText("¥" + mPost.getPrice());
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

            case R.id.ll_post_detail_discuss:   //弹出留言，评论区域
                showDiscussInput();
                break;

            case R.id.btn_post_detail_discuss_submit: //提交评论
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
        if ((mCurrentUser = BmobUser.getCurrentUser(MyBmobUser.class)) != null) {
            mPostService.postThumbUpCheck(mCurrentUser, postId, mActionListener);
        }
    }

    /**
     * 帖子点赞
     */
    private void setThumbUp() {
        //checkThumbUp();
        if (BmobUser.isLogin()) {
            if (isThumbUp) {
                ToastUtil.showToast(PostDetailActivity.this, "点了就别取消嘛,老铁", true);
                return;
            }
            isThumbUp = true;
            BmobRelation relation = mPost.getThumbUpRelation() == null ? new BmobRelation() : mPost.getThumbUpRelation();
            mPostService.postThumbUp(mCurrentUser, relation, mPost, mActionListener);
        } else {
            startActivity(new Intent(PostDetailActivity.this, LoginOrSignActivity.class));
        }
    }

    /**
     * 显示帖子留言输入框
     */
    private void showDiscussInput() {
        if (BmobUser.isLogin()) {
            inputMethodManager.toggleSoftInput(0, inputMethodManager.HIDE_NOT_ALWAYS);
            mLlPostDetailBottom.setVisibility(View.GONE);
            mLlPostDetailDiscussInput.setVisibility(View.VISIBLE);
            mEtPostDetailDiscuss.setText("");
            mEtPostDetailDiscuss.setFocusable(true);
            mEtPostDetailDiscuss.setFocusableInTouchMode(true);
            mEtPostDetailDiscuss.requestFocus();
            inputMethodManager.showSoftInput(mEtPostDetailDiscuss, 0);
        } else {
            startActivity(new Intent(PostDetailActivity.this, LoginOrSignActivity.class));
        }

    }

    /**
     * 隐藏帖子留言输入框
     */
    private void hideDiscussInput() {
        //评论框消失
        mLlPostDetailDiscussInput.setVisibility(View.GONE);
        mLlPostDetailBottom.setVisibility(View.VISIBLE);
        //隐藏输入法，键盘
        inputMethodManager.hideSoftInputFromWindow(
                mEtPostDetailDiscuss.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 帖子评论留言
     */
    private void discuss() {
        String content = mEtPostDetailDiscuss.getText().toString();
        if (!StringUtil.isEmptyContainsSpace(content)) {
            mPostService.publicDiscuss(postId, content, mCurrentUser, mActionListener);
        } else {
            ToastUtil.showToast(this, "评论内容不能为空", true);
        }

    }

    /**
     * 帖子收藏
     */
    private void collect() {
        if ((mCurrentUser = BmobUser.getCurrentUser(MyBmobUser.class)) != null) {
            BmobRelation relation = mPost.getCollectRelation() == null ? new BmobRelation() : mPost.getCollectRelation();
            relation.add(mCurrentUser);
            mPost.setCollectRelation(relation);
            mPost.setCollect(mPost.getCollect() + 1);
        }
    }

    /**
     * 咨询交易
     */
    private void business() {

    }

    /**
     * 帖子操作回调
     */
    private PostService.IPostActionListener mActionListener = new PostService.IPostActionListener() {
        @Override
        public void doSucceed(int actionType, List<? extends Object> list) {
            switch (actionType) {
                case USER_ACTION_THUMBUP:
                    isThumbUp = true;
                    mIvPostDetailThumb.setImageResource(R.drawable.ic_action_thumb_up_select);
                    break;

                case USER_ACTION_DISCUSS:
                    hideDiscussInput();
                    ToastUtil.showToast(MyApplication.getAppContext(), "评论帖子成功", true);
                    Comment comment = new Comment();
                    comment.setUser(mCurrentUser);
                    comment.setPost(mPost);
                    comment.setContent((String) list.get(0));
                    mCommentList.add(comment);
                    mListViewAdapter.notifyDataSetChanged();
                    break;

                case USER_ACTION_COLLECT:
                    break;

                case USER_ACTION_BUSINESS:
                    break;

                case USER_ACTION_QUERY_DISCUSS:
                    mCommentList.addAll((List<Comment>) list);
                    if (mListViewAdapter != null) {
                        Log.d(Contast.TAG, mCommentList.get(0).getContent() + "");
                        mListViewAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }

        @Override
        public void doFailed(int actionType, Exception e) {
            switch (actionType) {
                case USER_ACTION_THUMBUP:
                    ToastUtil.showToast(MyApplication.getAppContext(), "点赞失败,稍后再试" , true);
                    Log.e(Contast.TAG, "帖子点赞出错:" + e);
                    break;

                case USER_ACTION_DISCUSS:
                    ToastUtil.showToast(MyApplication.getAppContext(), "评论失败,稍后再试" , true);
                    Log.e(Contast.TAG, "评论帖子失败", e);
                    break;

                case USER_ACTION_COLLECT:
                    break;

                case USER_ACTION_BUSINESS:
                    break;

                case USER_ACTION_QUERY_DISCUSS:

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        this.finish();
    }

    /**
     * 重写触摸事件分发方法，用于检测评论输入焦点检测
     *          *如果弹出了评论区域，那么进行焦点检测，检测到触摸点位于区域外，则隐藏评论区域
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mLlPostDetailDiscussInput.getVisibility() == View.VISIBLE) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                //得到获取焦点的view
                View currentFocus = getCurrentFocus();
                if (currentFocus instanceof EditText || currentFocus instanceof Button) {
                    int[] l = {0, 0};
                    currentFocus.getLocationInWindow(l);
                    int top = l[1], bottom = top + currentFocus.getHeight();
                    if (ev.getY() < bottom && ev.getY() > top) {
                        // 点击评论区域的事件，忽略它。
                    } else {
                        hideDiscussInput();
                    }
                }
            }
        }
        if (MotionEvent.ACTION_MOVE == ev.getAction()) {
            View child = mSvPostDetailContent.getChildAt(0);
            //scrollview滑倒底
            if (child != null &&
                    child.getMeasuredHeight() <= mSvPostDetailContent.getScrollY() + mSvPostDetailContent.getHeight()) {
                mLvPostDetailDiscussShow.getParent().getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
