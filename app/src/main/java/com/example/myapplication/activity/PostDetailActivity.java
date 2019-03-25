package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
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
import com.example.myapplication.model.Comment;
import com.example.myapplication.model.MyBmobUser;
import com.example.myapplication.model.Post;
import com.example.myapplication.presenter.PostService;
import com.example.myapplication.presenter.PostServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.NetWorkUtils;
import com.example.myapplication.util.StringUtil;
import com.example.myapplication.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;


/**
 * Create by LuKaiqi on 2019/3/7.
 * function:帖子详情页
 */
public class PostDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String POST_ID = "post";//相对应帖子，由intent传递过来

    /**
     *  0表示点赞，1表示添加评论，2表示添加收藏, 3表示咨询交易, 4表示查询评论
    */
    public static final int USER_ACTION_THUMB_UP = 0;

    public static final int USER_ACTION_DISCUSS = 1;

    public static final int USER_ACTION_COLLECT =2;

    public static final int USER_ACTION_BUSINESS = 3;

    public static final int USER_ACTION_QUERY_DISCUSS = 4;

    private String postId;

    private PostService mPostService = new PostServiceImpl();

    private boolean isThumbUp = false;

    private boolean isCollected = false;



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
            postId = mPost.getObjectId();
            mPostService.getPostDataFromServerById(new PostService.IGetPostDataListener() {
                @Override
                public void getSucceed(List<Post> postList) {
                    mPost = postList.get(0);
                    Log.d(Contast.TAG, "获取到帖子数据");
                }

                @Override
                public void getFailed(BmobException e) {
                    //获取不到最新的帖子详情，不进入该页面
                    ToastUtil.showToast(PostDetailActivity.this, "无法连接网络,请检查网络", true);
                    Log.e(Contast.TAG, "获取特定帖子异常", e);
                }
            }, postId);
            //帖子浏览量+1
            mPost.setLookCount(mPost.getLookCount() + 1);
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
        checkPostStatus();
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
     * 检查收藏点赞状态。即查询点赞用户列表中是否包含当前用户
     *          *如果用户没有登录，那么就不会触发该方法，因此用户登录成功后点赞的时候应该重新再检查一次
     */
    private void checkPostStatus() {
        if ((mCurrentUser = BmobUser.getCurrentUser(MyBmobUser.class)) != null) {
            mPostService.postStatusCheck(mCurrentUser, USER_ACTION_THUMB_UP, postId, mActionListener);
            mPostService.postStatusCheck(mCurrentUser, USER_ACTION_COLLECT, postId, mActionListener);
        }
    }

    /**
     * 帖子点赞
     */
    private void setThumbUp() {
        if (NetWorkUtils.isNetworkConnected()) {
            if (BmobUser.isLogin()) {
                if (isThumbUp) {
                    //取消点赞
                    isThumbUp = false;
                    mPost.setThumbUp(mPost.getThumbUp() - 1);
                    Log.d(Contast.TAG, mPost.getThumbUp() + "点赞数");
                    BmobRelation relation = new BmobRelation();
                    relation.remove(mCurrentUser);
                    mPost.setThumbUpRelation(relation);
                    mIvPostDetailThumb.setImageResource(R.drawable.ic_action_thumb_up);
                } else {
                    //增加点赞
                    isThumbUp = true;
                    mPost.setThumbUp(mPost.getThumbUp() + 1);
                    Log.d(Contast.TAG, mPost.getThumbUp() + "点赞数");
                    BmobRelation relation = new BmobRelation();
                    relation.add(mCurrentUser);
                    mPost.setThumbUpRelation(relation);
                    mIvPostDetailThumb.setImageResource(R.drawable.ic_action_thumb_up_select);
                }
            } else {
                startActivity(new Intent(PostDetailActivity.this, LoginOrSignActivity.class));
            }
        } else {
            ToastUtil.showToast(this, "无法连接网络,请检查网络", true);
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
        if (NetWorkUtils.isNetworkConnected()) {
            if (BmobUser.isLogin()) {
                if (isCollected) {
                    //取消收藏
                    isCollected = false;
                    mPost.setCollect(mPost.getCollect() -1);
                    BmobRelation relation = new BmobRelation();
                    relation.remove(mCurrentUser);
                    mPost.setCollectRelation(relation);
                    mIvPostDetailCollect.setImageResource(R.drawable.ic_action_collect);
                } else {
                    //增加收藏
                    isCollected = true;
                    mPost.setCollect(mPost.getCollect() + 1);
                    BmobRelation relation = new BmobRelation();
                    relation.add(mCurrentUser);
                    mPost.setCollectRelation(relation);
                    mIvPostDetailCollect.setImageResource(R.drawable.ic_action_my_collection);
                }
            } else {
                ToastUtil.showToast(this, "请先登录", true);
            }
        } else {
            ToastUtil.showToast(this, "无法连接网络,请检查网络", true);
        }
    }

    /**
     * 咨询交易
     */
    private void business() {
        if (NetWorkUtils.isNetworkConnected()) {
            if (BmobUser.isLogin()) {
                chatReady();
            } else {
                ToastUtil.showToast(this, "请先登录", true);
            }
        } else {
            ToastUtil.showToast(this, "无法连接网络,请检查网络", true);
        }
    }

    /**
     * 为进入聊天模块做准备
     *      *初始化会话信息
     *      *连接服务器为会话准备
     *      *传递聊天用户信息
     */
    private void chatReady() {
        //开始聊天前检测是否连接聊天服务器
        if (!checkIsConnectServer()) {
            connectServer();
            if (!checkIsConnectServer()) {
                connectServer();
                ToastUtil.showToast(this, "未连接到IM服务器,无法聊天", true);
                if (!checkIsConnectServer())
                    return;
            }
        }
        //用户信息为帖主信息
        BmobIMUserInfo userInfo = new BmobIMUserInfo(
                mPost.getAuthor().getObjectId(),
                mPost.getAuthor().getUsername(),
                mPost.getAuthor().getAvatarFile());
        BmobIMConversation conversation = BmobIM.getInstance().startPrivateConversation(userInfo, null);
        conversation.setConversationTitle(mPost.getAuthor().getUsername());
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ChatActivity.CHAT_KEY, conversation);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 连接聊天服务器
     */
    private void connectServer() {
        final MyBmobUser currentUser = BmobUser.getCurrentUser(MyBmobUser.class);
        //判断用户是否登录，并且连接状态不是已连接，则进行连接服务器操作
        if (!TextUtils.isEmpty(currentUser.getObjectId()) &&
                BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            BmobIM.connect(currentUser.getObjectId(), new ConnectListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        //更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().
                                updateUserInfo(
                                        new BmobIMUserInfo(currentUser.getObjectId(),
                                                currentUser.getUsername(), currentUser.getAvatarFile()));
                        //EventBus.getDefault().post(new RefreshEvent());
                    } else {
                        Log.e(Contast.TAG, "ChatActivity initData has Error:", e);
                    }
                }
            });
            //监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    Log.d(Contast.TAG, "当前状态:" + status.getMsg());
                    Log.d(Contast.TAG, "连接状态:" + BmobIM.getInstance().getCurrentStatus().getMsg());
                }
            });
        }
    }

    /**
     * 检查是否连接到聊天服务器
     * @return false 未连接服务器  true  连接到了服务器
     */
    private boolean checkIsConnectServer() {
        return BmobIM.getInstance().getCurrentStatus().getCode() == ConnectionStatus.CONNECTED.getCode();
    }

    /**
     * 帖子操作回调
     */
    private PostService.IPostActionListener mActionListener = new PostService.IPostActionListener() {
        @Override
        public void doSucceed(int actionType, List<? extends Object> list) {
            switch (actionType) {
                case USER_ACTION_THUMB_UP:
                    mIvPostDetailThumb.setImageResource(R.drawable.ic_action_thumb_up_select);
                    isThumbUp = true;
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
                    mIvPostDetailCollect.setImageResource(R.drawable.ic_action_my_collection);
                    isCollected = true;
                    break;

                case USER_ACTION_BUSINESS:
                    break;

                case USER_ACTION_QUERY_DISCUSS:
                    if (list != null && list.size() > 0) {
                        mCommentList.addAll((List<Comment>) list);
                        if (mListViewAdapter != null) {
                            Log.d(Contast.TAG, mCommentList.get(0).getContent() + "");
                            mListViewAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d(Contast.TAG, "当前帖子没有留言");
                    }
                    break;
            }
        }

        @Override
        public void doFailed(int actionType, BmobException e) {
            switch (actionType) {
                case USER_ACTION_THUMB_UP:
                    isThumbUp = false;
                    Log.e(Contast.TAG, "检查帖子点赞异常", e);
                    break;

                case USER_ACTION_DISCUSS:
                    hideDiscussInput();
                    if (e.getErrorCode() == 9016) {
                        ToastUtil.showToast(PostDetailActivity.this, "无法连接网络,请检查网络", true);
                    }
                    Log.e(Contast.TAG, "评论帖子失败", e);
                    break;

                case USER_ACTION_COLLECT:
                    isCollected = false;
                    Log.e(Contast.TAG, "检查帖子收藏异常", e);
                    break;

                case USER_ACTION_BUSINESS:
                    Log.e(Contast.TAG, "咨询交易出错", e);
                    break;

                case USER_ACTION_QUERY_DISCUSS:
                    Log.e(Contast.TAG, "查询评论出错", e);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onStop() {
        if (mCurrentUser != null) {
            mPostService.postUpdate(mPost);
        }
        super.onStop();
    }

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
