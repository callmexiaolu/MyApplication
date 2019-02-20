package com.example.myapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;

import com.example.myapplication.R;
import com.example.myapplication.fragment.LoginFragment;
import com.example.myapplication.fragment.RegisteredFragment;
import com.example.myapplication.util.UserUtil;

import java.util.ArrayList;
import java.util.List;


public class LoginOrRegisteredActivity extends BaseActivity {

    private final FragmentManager mFragmentManager = getSupportFragmentManager();

    private LoginFragment mLoginFragment;

    private RegisteredFragment mRegisteredFragment;

    private static final String LOGIN_FRAGMENT_KEY = "loginFragment";

    private static final String REGISTERED_FRAGMENT_KEY = "registeredFragment";

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_login_registered;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initFragment(savedInstanceState);//初始化fragment
        if (savedInstanceState == null) {
            setDefaultFragment();//设置默认展示的fragment
        }
    }

    @Override
    public void initListener() {
        mLoginFragment.setLoginInterface(new LoginFragment.LoginInterface() {
            @Override
            public void goRegistered() {
                fragmentChange(mLoginFragment, mRegisteredFragment);
            }

            @Override
            public void sureLogin(String mEtLoginNameInput, String mEtLoginPasswordInput) {
                UserUtil.userLogin(mEtLoginNameInput, mEtLoginPasswordInput, LoginOrRegisteredActivity.this);
            }
        });
        mRegisteredFragment.setRegisteredInterface(new RegisteredFragment.RegisteredInterface() {
            @Override
            public void sureRegistered(final String registeredName, final String password) {
                UserUtil.userRegisteredByName(registeredName, password, LoginOrRegisteredActivity.this);
            }

            @Override
            public void goToLogin() {
                fragmentChange(mRegisteredFragment, mLoginFragment);
            }
        });
    }

    private void initFragment(@Nullable Bundle savedInstanceState) {
        //fragment初始化，如果activity意外销毁则从保存数据中恢复。否则重新加载一个。
        mLoginFragment = savedInstanceState == null ? new LoginFragment() :
                (LoginFragment) getSupportFragmentManager().getFragment(savedInstanceState, LOGIN_FRAGMENT_KEY);
        mRegisteredFragment = savedInstanceState == null ? new RegisteredFragment() :
                (RegisteredFragment) getSupportFragmentManager().getFragment(savedInstanceState, REGISTERED_FRAGMENT_KEY);
    }

    /**
     * 设置该Activity界面默认的fragment为登录
     */
    private void setDefaultFragment() {
        if (!mLoginFragment.isAdded()) {
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.fl_login_or_registered, mLoginFragment)
                    .commit();
        }
    }

    /**
     * fragment改变
     *
     * @param oldFragment 需要替换的fragment
     * @param newFragment 需要展示的fragment
     */
    private void fragmentChange(Fragment oldFragment, Fragment newFragment) {
        if (!newFragment.isAdded()) {
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.fl_login_or_registered, newFragment)
                    .hide(oldFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            mFragmentManager
                    .beginTransaction()
                    .hide(oldFragment)
                    .show(newFragment)
                    .commit();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mLoginFragment != null) {
            getSupportFragmentManager().putFragment(outState, LOGIN_FRAGMENT_KEY, mLoginFragment);
        }
        if (mRegisteredFragment != null) {
            getSupportFragmentManager().putFragment(outState, REGISTERED_FRAGMENT_KEY, mRegisteredFragment);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * 返回键，退出当前activity，回到MainActivity
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
