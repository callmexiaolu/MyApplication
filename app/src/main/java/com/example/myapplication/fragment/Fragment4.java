package com.example.myapplication.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.activity.LoginOrSignActivity;
import com.example.myapplication.activity.SettingsActivity;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.bean.Post;
import com.example.myapplication.service.UserService;
import com.example.myapplication.service.UserServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.NetWorkUtils;
import com.example.myapplication.util.StringUtil;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function: 模块-我
 * 包括功能：我的收藏，我的评论，我的点赞，浏览历史
 *          帖子(数量)，系统设置(编辑资料(头像，用户名，介绍，性别)，账号与绑定(手机号，修改密码，微信绑定，账号删除)，夜间模式，检查版本，关于应用，退出登录)
 */
public class Fragment4 extends BaseFragment implements View.OnClickListener {

    private static final String DEFAULT_NUMBER = "0";

    private RelativeLayout mRlFragment4Top;

    private ImageView mIvUserAvatar;

    private TextView mTvUsername, mTvUserSignature;//用户名，用户签名

    private TextView mTvPostCount, mTvFollow, mTvFansNumber, mTvThumbUp;

    private LinearLayout mLlAppSetting, mLlAppAbout, mLlAppMyCollection;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_4;
    }

    @Override
    public void initViews(View view) {
        mRlFragment4Top     = view.findViewById(R.id.rl_fragment4_top_root);
        mIvUserAvatar       = view.findViewById(R.id.iv_fragment4_user_avatar);
        mTvUsername         = view.findViewById(R.id.tv_fragment4_user_name);
        mTvUserSignature    = view.findViewById(R.id.tv_fragment4_user_signature);

        mTvPostCount        = view.findViewById(R.id.tv_fragment4_post_number);
        mTvFollow           = view.findViewById(R.id.tv_fragment4_follow_number);
        mTvFansNumber       = view.findViewById(R.id.tv_fragment4_fans_number);
        mTvThumbUp          = view.findViewById(R.id.tv_fragment4_thumb_up_number);

        mLlAppSetting       = view.findViewById(R.id.ll_app_settings);
        mLlAppAbout         = view.findViewById(R.id.ll_app_about);
        mLlAppMyCollection  = view.findViewById(R.id.ll_app_my_collection);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        mRlFragment4Top.setClickable(true);//设置可以点击
        mRlFragment4Top.setFocusable(true);//设置可以获得焦点
        mRlFragment4Top.setOnClickListener(this);

        mLlAppSetting.setFocusable(true);
        mLlAppSetting.setClickable(true);
        mLlAppSetting.setOnClickListener(this);

        mLlAppAbout.setFocusable(true);
        mLlAppAbout.setClickable(true);
        mLlAppAbout.setOnClickListener(this);

        mLlAppMyCollection.setFocusable(true);
        mLlAppMyCollection.setClickable(true);
        mLlAppMyCollection.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_fragment4_top_root:
                if (!BmobUser.isLogin()) {
                    startActivity(new Intent(getActivity(), LoginOrSignActivity.class));
                } else {

                }
                break;

            case R.id.ll_app_settings:
                startActivityForResult(new Intent(getActivity(), SettingsActivity.class), 1);
                break;
        }
    }

    @Override
    public void onResume() {
        updateUserInfo();
        super.onResume();
    }

    /**
     * 信息的获取会先从缓存中获取，因此应该进入页面即从后台获取最新数据更新缓存。
     * 更新本地缓存
     *      **有网络时  从后台加载
     *      **无网络时  从缓存加载
     */
    private  void updateUserInfo() {
        if (BmobUser.isLogin()) {
            if (NetWorkUtils.isNetworkConnected()) {
                UserService userService = new UserServiceImpl();
                userService.userInfoUpdate();
            }
            loadUserInfo(BmobUser.getCurrentUser(MyBmobUser.class));
        } else {
            loadDefaultInfo();
        }
    }

    /**
     * 加载用户信息(从本地缓存中加载)
     * 判断：BmobUser.isLogin() true 有用户   false 无用户
     * @param currentUser 更新后的用户信息
     * @return true 有当前用户，点击顶部区域则跳转用户详细信息
     *         false 无当前用户，点击顶部区域则跳转登陆界面
     *
     *用户信息：头像，用户名，签名，帖子数，关注，粉丝，获赞
     */
    private void loadUserInfo(MyBmobUser currentUser) {
        if (BmobUser.isLogin()) {
            String avatarUrl = currentUser.getAvatarFile() == null ? "" : currentUser.getAvatarFile();
            if (!StringUtil.isEmpty(avatarUrl)) {
                Glide.with(MyApplication.getAppContext()).load(avatarUrl).into(mIvUserAvatar);
            } else {
                mIvUserAvatar.setImageResource(R.drawable.ic_action_login_me);
            }
            mTvUsername.setText(currentUser.getUsername());
            mTvUserSignature.setText(currentUser.getSignature());
            mTvPostCount.setText(currentUser.getPostCount());
            mTvFollow.setText(currentUser.getFollowCount());
            mTvFansNumber.setText(currentUser.getFans());
            mTvThumbUp.setText(currentUser.getThumbUp());
        }
    }

    /**
     * 加载默认的用户信息
     * 用于当前没有用户登录状态
     */
    private void loadDefaultInfo() {
        mIvUserAvatar.setImageResource(R.drawable.ic_action_login_me);
        mTvUsername.setText(MyApplication.getAppContext().getResources().getString(R.string.tv_fragment4_login_registered));
        mTvUserSignature.setText(MyApplication.getAppContext().getResources().getString(R.string.tv_fragment4_introduction));
        mTvPostCount.setText(DEFAULT_NUMBER);
        mTvFollow.setText(DEFAULT_NUMBER);
        mTvFansNumber.setText(DEFAULT_NUMBER);
        mTvThumbUp.setText(DEFAULT_NUMBER);

    }

    /**
     * 当前fragment显示的时候刷新用户数据
     * @param hidden 当前fragment是否被隐藏
     *              true被隐藏
     *              false 显示
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
           updateUserInfo();
        }
    }

    /**
     * 打开SettingsActivity，如果退出登录，那么就重置页面数据
     * @param requestCode 请求码，调用startActivityForResult()传递过去的值
     * @param resultCode 结果码，标识返回数据来自哪个Activity
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (resultCode == SettingsActivity.LOGIN_OUT_SUCCEED_CODE) {
           try {
               if (data.getExtras().getInt(SettingsActivity.LOGIN_OUT_KEY) == SettingsActivity.LOGIN_OUT_SUCCEED_CODE) {
                   updateUserInfo();
               }
           } catch (Exception e) {
               Log.e(Contast.TAG, "Fragment4 has Exception: " + e);
           }
       }
    }
}
