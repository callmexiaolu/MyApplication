package com.example.myapplication.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.myapplication.R;

import cn.bmob.v3.BmobUser;

/**
 * Create By LuKaiqi 2019/02/26
 * Describe:应用设置
 *
 *          *我的收藏
 *          *关于
 *          *设置
 *              *推送通知
 *              *夜间模式
 *              *退出登录
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    public static final int LOGIN_OUT_SUCCEED_CODE = 1;

    public static final String LOGIN_OUT_KEY = "login_out";

    private ImageView mIvBackToFragment;

    private RelativeLayout mRlLoginOut;

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initViews() {
        mIvBackToFragment = findViewById(R.id.iv_back_to_fragment);
        mRlLoginOut = findViewById(R.id.rl_settings_login_out);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initListener() {
        mIvBackToFragment.setFocusable(true);
        mIvBackToFragment.setClickable(true);
        mIvBackToFragment.setOnClickListener(this);

        if (BmobUser.isLogin()) {
            mRlLoginOut.setFocusable(true);
            mRlLoginOut.setClickable(true);
            mRlLoginOut.setVisibility(View.VISIBLE);
            mRlLoginOut.setOnClickListener(this);
        } else {
            mRlLoginOut.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_to_fragment:
                this.finish();
                break;

            case R.id.rl_settings_login_out:
                BmobUser.logOut();
                Intent intent = new Intent();
                intent.putExtra(LOGIN_OUT_KEY, LOGIN_OUT_SUCCEED_CODE);
                this.setResult(LOGIN_OUT_SUCCEED_CODE, intent);
                this.finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
