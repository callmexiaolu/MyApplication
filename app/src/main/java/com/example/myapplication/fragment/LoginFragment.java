package com.example.myapplication.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.util.ToastUtil;


/**
 * Create by LuKaiqi on 2019/2/2.
 * function:
 */
public class LoginFragment extends BaseFragment {

    private Button mBtnLoginSure, mBtnGoToRegistered;

    private EditText mEtLoginName, mEtLoginPassword;

    private LoginInterface loginInterface;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void initViews(View view) {
        mBtnLoginSure    = view.findViewById(R.id.btn_login_sure);
        mEtLoginName     = view.findViewById(R.id.et_login_name);
        mEtLoginPassword = view.findViewById(R.id.et_login_password);
        mBtnGoToRegistered = view.findViewById(R.id.btn_go_to_registered);
    }

    @Override
    public void initListener() {
        mBtnLoginSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEtLoginNameInput = mEtLoginName.getText().toString();
                String mEtLoginPasswordInput = mEtLoginPassword.getText().toString();
                if (!TextUtils.isEmpty(mEtLoginNameInput) && !TextUtils.isEmpty(mEtLoginPasswordInput)) {
                    loginInterface.sureLogin(mEtLoginNameInput, mEtLoginPasswordInput);
                } else {
                    ToastUtil.showToast(MyApplication.getAppContext(), "账号密码不能为空", true);
                }

            }
        });
        mBtnGoToRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginInterface.goRegistered();
            }
        });
    }

    @Override
    public void initData() {

    }

    public void setLoginInterface(LoginInterface loginInterface) {
        this.loginInterface = loginInterface;
    }

    public interface LoginInterface{
        /**
         * 前往注册
         */
        void goRegistered();

        /**
         * 确认登录
         * @param mEtLoginNameInput 登录名
         * @param mEtLoginPasswordInput 密码
         */
        void sureLogin(final String mEtLoginNameInput, final String mEtLoginPasswordInput);
    }

}
