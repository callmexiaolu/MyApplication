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
        permissionCheck();
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
}
