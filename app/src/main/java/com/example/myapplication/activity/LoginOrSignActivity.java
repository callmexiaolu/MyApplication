package com.example.myapplication.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.service.LoginService;
import com.example.myapplication.service.LoginServiceImpl;
import com.example.myapplication.util.StringUtil;
import com.example.myapplication.util.ToastUtil;

/**
 * Create By LuKaiqi
 * Describe:登录
 */
public class LoginOrSignActivity extends BaseActivity implements View.OnClickListener{

    private ImageView mIvBackToFragment;
    private TextView     mTvLoginWithoutPassword, mTvLoginWithAccount;
    private LinearLayout mLlLoginWithAccount, mLlLoginWithoutPassword;

    private EditText     mEtUserAccount, mEtUserPassword, mEtUserPhoneNumber, mEtUserPhoneCode;
    private TextView     mTvUserGetPhoneCode;

    private Button       mBtnUserSureLogin;
    private TextView     mTvUserForgetPassword;
    private LinearLayout mLlUserLoginWithWeChat, mLlUserLoginWithQQ;

    private static boolean CURRENT_LOGIN_MODE = true;// true为使用账号密码登录， false为免密登录

    private LoginService mLoginService = new LoginServiceImpl();

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_login_sign;
    }

    @Override
    public void initViews() {
        mIvBackToFragment = findViewById(R.id.iv_back_to_fragment);
        mTvLoginWithoutPassword = findViewById(R.id.tv_login_without_password);
        mTvLoginWithAccount     = findViewById(R.id.tv_login_with_account);
        mLlLoginWithAccount     = findViewById(R.id.ll_user_login_with_account);
        mLlLoginWithoutPassword = findViewById(R.id.ll_user_login_without_password);

        mEtUserAccount          = findViewById(R.id.et_user_account);
        mEtUserPassword         = findViewById(R.id.et_user_password);
        mEtUserPhoneNumber      = findViewById(R.id.et_user_phone_number);
        mEtUserPhoneCode        = findViewById(R.id.et_user_phone_code);
        mTvUserGetPhoneCode     = findViewById(R.id.tv_get_phone_code);

        mBtnUserSureLogin       = findViewById(R.id.btn_user_sure_login);
        mTvUserForgetPassword   = findViewById(R.id.tv_user_forget_password);

        mLlUserLoginWithWeChat = findViewById(R.id.ll_user_login_with_wechat);
        mLlUserLoginWithQQ      = findViewById(R.id.ll_user_login_with_qq);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initListener() {
        mIvBackToFragment.setClickable(true);
        mIvBackToFragment.setFocusable(true);
        mIvBackToFragment.setOnClickListener(this);

        mLlLoginWithAccount.setVisibility(View.VISIBLE);
        mLlLoginWithoutPassword.setVisibility(View.GONE);

        mTvLoginWithoutPassword.setClickable(true);
        mTvLoginWithoutPassword.setFocusable(true);
        mTvLoginWithoutPassword.setVisibility(View.VISIBLE);
        mTvLoginWithoutPassword.setOnClickListener(this);

        mTvLoginWithAccount.setClickable(true);
        mTvLoginWithAccount.setFocusable(true);
        mTvLoginWithAccount.setVisibility(View.GONE);
        mTvLoginWithAccount.setOnClickListener(this);

        mBtnUserSureLogin.setOnClickListener(this);

        mTvUserGetPhoneCode.setClickable(true);
        mTvUserGetPhoneCode.setFocusable(true);
        mTvUserGetPhoneCode.setOnClickListener(this);

        mTvUserForgetPassword.setClickable(true);
        mTvUserForgetPassword.setFocusable(true);
        mTvUserForgetPassword.setOnClickListener(this);

        mLlUserLoginWithWeChat.setClickable(true);
        mLlUserLoginWithWeChat.setFocusable(true);
        mLlUserLoginWithWeChat.setOnClickListener(this);

        mLlUserLoginWithQQ.setClickable(true);
        mLlUserLoginWithQQ.setFocusable(true);
        mLlUserLoginWithQQ.setOnClickListener(this);
    }



    /**
     * 返回键，退出当前activity，回到MainActivity
     */
    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back_to_fragment://返回
                this.finish();
                break;

            case R.id.tv_login_without_password://免密登录
                userAccountToLogin(false);
                break;

            case R.id.tv_login_with_account://账号登录
                userAccountToLogin(true);
                break;

            case R.id.btn_user_sure_login://确定登录
                if (CURRENT_LOGIN_MODE) {
                    //使用账号密码登录
                    mLoginService.userLogin(mEtUserAccount.getText().toString(), mEtUserPassword.getText().toString(), this);
                } else {
                    //免密登录
                    if (!StringUtil.isEmpty(mEtUserPhoneCode.getText().toString())) {
                        mLoginService.userLoginOrSignByPhone(
                                mEtUserPhoneNumber.getText().toString(),
                                mEtUserPhoneCode.getText().toString(),
                                this);
                    } else {
                        ToastUtil.showToast(this, "验证码不能为空", true);
                    }
                }
                break;

            case R.id.tv_get_phone_code://获取验证码
                if (phoneCodeCountTime()) {
                    mLoginService.userGetPhoneCode(mEtUserPhoneNumber.getText().toString());
                }
                break;

            case R.id.tv_user_forget_password://忘记密码
                break;

            case R.id.ll_user_login_with_wechat://微信登陆
                break;

            case R.id.ll_user_login_with_qq://qq登陆
                break;
        }
    }

    /**
     * 选择是否使用账号密码登录
     * @param flag true 使用账号密码登录 false 使用免密登录
     */
    private void userAccountToLogin(boolean flag) {
        mTvLoginWithoutPassword.setVisibility(flag? View.VISIBLE : View.GONE);
        mLlLoginWithAccount.setVisibility(flag? View.VISIBLE : View.GONE);
        mTvLoginWithAccount.setVisibility(flag? View.GONE : View.VISIBLE);
        mLlLoginWithoutPassword.setVisibility(flag? View.GONE : View.VISIBLE);
        CURRENT_LOGIN_MODE = flag;
    }

    /**
     * 获取验证码后倒计时
     * @return true 倒计时结束  false 倒计时未结束，不发送短信
     */
    private boolean phoneCodeCountTime() {
        mTvUserGetPhoneCode.setClickable(false);
        mTvUserGetPhoneCode.setFocusable(false);
        return true;
    }
}
