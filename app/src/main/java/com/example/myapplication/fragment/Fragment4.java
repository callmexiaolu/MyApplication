package com.example.myapplication.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.activity.LoginOrSignActivity;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.util.StringUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function: 模块-我
 * 包括功能：我的收藏，我的评论，我的点赞，浏览历史
 *          帖子(数量)，系统设置(编辑资料(头像，用户名，介绍，性别)，账号与绑定(手机号，修改密码，微信绑定，账号删除)，夜间模式，检查版本，关于应用，退出登录)
 */
public class Fragment4 extends BaseFragment implements View.OnClickListener {

    private RelativeLayout mRlFragment4Top;

    private ImageView mIvUserAvatar;

    private TextView mTvUsername, mTvUserSignature;//用户名，用户签名

    private TextView mTvFansNumber;

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
        mTvFansNumber       = view.findViewById(R.id.tv_fragment4_fans_number);
    }

    @Override
    public void initData() {
        updateUserInfo();
    }

    @Override
    public void initListener() {
        mRlFragment4Top.setClickable(true);//设置可以点击
        mRlFragment4Top.setFocusable(true);//设置可以获得焦点
        mRlFragment4Top.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_fragment4_top_root: {
                if (!BmobUser.isLogin()) {
                    startActivity(new Intent(getActivity(), LoginOrSignActivity.class));
                } else {

                }
                break;
            }
        }
    }

    /**
     * 信息的获取会先从缓存中获取，因此应该进入页面即更新缓存。
     * 更新本地缓存
     */
    private void updateUserInfo() {
        if (BmobUser.isLogin()) {
            BmobUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if (e == null) {
                        final MyBmobUser currentUser = BmobUser.getCurrentUser(MyBmobUser.class);
                        loadUserInfo(currentUser);
                    }
                }
            });
        }
    }

    /**
     * 加载用户信息
     * 判断：BmobUser.isLogin() true 有用户   false 无用户
     * @param currentUser 更新后的用户信息
     * @return true 有当前用户，点击顶部区域则跳转用户详细信息
     *         false 无当前用户，点击顶部区域则跳转登陆界面
     *
     *用户信息：头像，用户名，签名，帖子数，关注，粉丝，获赞
     */
    private boolean loadUserInfo(MyBmobUser currentUser) {
        if (BmobUser.isLogin()) {
            String avatarUrl = currentUser.getAvatarFile() == null ? "" : currentUser.getAvatarFile().getFileUrl();
            if (!StringUtil.isEmpty(avatarUrl)) {
                Glide.with(MyApplication.getAppContext()).load(avatarUrl).into(mIvUserAvatar);
            } else {
                mIvUserAvatar.setImageResource(R.drawable.ic_action_login_me);
            }
            mTvUsername.setText(currentUser.getUsername());
            mTvUsername.setTextColor(Color.WHITE);
            mTvUserSignature.setText(currentUser.getSignature());
            mTvUserSignature.setTextColor(Color.WHITE);
            mTvFansNumber.setText(currentUser.getFans());
        }
        return false;
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

}
