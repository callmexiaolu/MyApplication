package com.example.myapplication.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.example.myapplication.activity.LoginOrRegisteredActivity;
import com.example.myapplication.util.UserUtil;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function: 模块-我
 * 包括功能：我的收藏，我的评论，我的点赞，浏览历史
 *          帖子(数量)，系统设置(编辑资料(头像，用户名，介绍，性别)，账号与绑定(手机号，修改密码，微信绑定，账号删除)，夜间模式，检查版本，关于应用，退出登录)
 */
public class Fragment4 extends BaseFragment implements View.OnClickListener {

    private RelativeLayout mRlFragment4Top;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_4;
    }

    @Override
    public void initViews(View view) {
        mRlFragment4Top = view.findViewById(R.id.rl_fragment4_top_root);
    }

    @Override
    public void initData() {

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
                startActivityForResult(new Intent(getActivity(), LoginOrRegisteredActivity.class), 1);
                break;
            }
        }
    }
}
