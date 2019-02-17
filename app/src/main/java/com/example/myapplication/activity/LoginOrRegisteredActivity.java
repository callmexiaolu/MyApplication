package com.example.myapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;


import com.example.myapplication.R;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.bean.User;
import com.example.myapplication.db.UserTable;
import com.example.myapplication.dbutil.TableOperate;
import com.example.myapplication.fragment.LoginFragment;
import com.example.myapplication.fragment.RegisteredFragment;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.EncryptUtil;
import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.util.UserUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;


public class LoginOrRegisteredActivity extends BaseActivity{

    private String[] mPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WAKE_LOCK
    }; //申请权限

    List<String> mRefusePermissions = new ArrayList<>();//用户拒绝的权限

    private final FragmentManager mFragmentManager = getSupportFragmentManager();

    private LoginFragment mLoginFragment;

    private RegisteredFragment mRegisteredFragment;

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_login_registered;
    }

    @Override
    public void initViews() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
       permissionCheck();
       initFragment();//初始化fragment
       setDefaultFragment();//设置默认展示的fragment
    }

    private void initFragment() {
        mLoginFragment = new LoginFragment();
        mRegisteredFragment = new RegisteredFragment();
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
     * @param oldFragment 需要替换的fragment
     * @param newFragment 需要展示的fragment
     */
    private void fragmentChange(Fragment oldFragment, Fragment newFragment) {
        if (!newFragment.isAdded()) {
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.fl_login_or_registered, newFragment)
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

    private void permissionCheck() {
        mRefusePermissions.clear();
        for (int i = 0; i < mPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(LoginOrRegisteredActivity.this, mPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mRefusePermissions.add(mPermissions[i]);
            }
        }
        if (mRefusePermissions.size() > 0) {
            //拒绝权限不为空，表示某些权限没接受
            String[] mRefuse = mRefusePermissions.toArray(new String[mRefusePermissions.size()]);
            ActivityCompat.requestPermissions(LoginOrRegisteredActivity.this, mRefuse, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        boolean mPermissionDismiss = false;//有权限没通过
        switch (requestCode) {
            case 1: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        mPermissionDismiss = true;
                        break;
                    }
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
