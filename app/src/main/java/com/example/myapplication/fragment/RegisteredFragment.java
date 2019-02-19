package com.example.myapplication.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.util.StringUtil;
import com.example.myapplication.util.ToastUtil;


/**
 * Create by LuKaiqi on 2019/2/1.
 * function:
 */


public class RegisteredFragment extends BaseFragment{

    private Button mBtnRegisteredSure, mBtnGoToLogin;
    private EditText mEtRegisteredName, mEtRegisteredPassword;

    private RegisteredInterface registeredInterface;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_registered;
    }

    @Override
    public void initViews(View view) {
        mBtnRegisteredSure = view.findViewById(R.id.btn_register_sure);
        mBtnGoToLogin =  view.findViewById(R.id.btn_go_to_login);
        mEtRegisteredName =  view.findViewById(R.id.et_registered_name);
        mEtRegisteredPassword =  view.findViewById(R.id.et_registered_password);
    }

    @Override
    public void initListener() {
        mBtnRegisteredSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = mEtRegisteredName.getText().toString();
                String passwordString = mEtRegisteredPassword.getText().toString();
                if (!StringUtil.isEmpty(nameString) && !StringUtil.isEmpty(passwordString)) {
                    registeredInterface.sureRegistered(nameString, passwordString);
                } else {
                    ToastUtil.showToast(MyApplication.getAppContext(), "名字或密码不能为空", true);
                }
            }
        });
        mBtnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeredInterface.goToLogin();
            }
        });
    }

    @Override
    public void initData() {

    }

    public void setRegisteredInterface(RegisteredInterface registeredInterface) {
        this.registeredInterface = registeredInterface;
    }

    public interface RegisteredInterface{
        /**
         *确认注册
         */
        void sureRegistered(final String registeredName, final String password);

        /**
         * 前往登录
         */
        void goToLogin();
    }

}
