package com.example.myapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.myapplication.R;
import com.example.myapplication.fragment.Fragment1;
import com.example.myapplication.fragment.Fragment2;
import com.example.myapplication.fragment.Fragment3;
import com.example.myapplication.fragment.Fragment4;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private List<Fragment> mFragments;
    private RadioGroup mRadioGroup;
    private int mCurrent = 0;
    private Fragment mCurrentFragment;
    private RadioButton mRbtn1;
    private Fragment1 mFragment1;
    private Fragment2 mFragment2;
    private Fragment3 mFragment3;
    private Fragment4 mFragment4;

    private static final String FRAGMENT_1_KEY = "fragment1";

    private static final String FRAGMENT_2_KEY = "fragment2";

    private static final String FRAGMENT_3_KEY = "fragment3";

    private static final String FRAGMENT_4_KEY = "fragment4";

    private static final String CURRENT_FRAGMENT = "currentFragment";


    private String[] mPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WAKE_LOCK
    }; //申请权限

    List<String> mRefusePermissions = new ArrayList<>();//用户拒绝的权限

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        mRadioGroup = findViewById(R.id.rg_switch);
        mRbtn1 = findViewById(R.id.rbtn_1);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        permissionCheck();
        initFragment(savedInstanceState);
        //设置首页为默认展示页面
        setDefaultFragment();
    }

    @Override
    public void initListener() {
        mRadioGroup.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    private void setDefaultFragment() {
        mCurrentFragment = mFragments.get(mCurrent);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_tab, mCurrentFragment)
                .commit();
        mRbtn1.setChecked(true);
        mRbtn1.setTextColor(Color.rgb(231, 55, 62));
    }

    RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    ((RadioButton) group.getChildAt(mCurrent)).setTextColor(Color.rgb(0, 0, 0));
                    ((RadioButton) group.getChildAt(i)).setTextColor(Color.rgb(231, 55, 62));
                    mCurrent = i;
                    FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    if (!mFragments.get(i).isAdded()) {
                        if (mCurrentFragment != null) {
                            mFragmentTransaction.hide(mCurrentFragment);
                        }
                        mFragmentTransaction.add(R.id.fl_tab, mFragments.get(i))
                                .commit();
                    } else {
                        mFragmentTransaction.hide(mCurrentFragment)
                                .show(mFragments.get(i))
                                .commit();
                    }
                    mCurrentFragment = mFragments.get(i);
                    return;
                }
            }
        }
    };

    private void initFragment(Bundle savedInstanceState) {
        mFragments = new ArrayList<>();
        if (savedInstanceState == null) {
            mFragment1 = new Fragment1();
            mFragment2 = new Fragment2();
            mFragment3 = new Fragment3();
            mFragment4 = new Fragment4();
            mFragments.add(mFragment1);
            mFragments.add(mFragment2);
            mFragments.add(mFragment3);
            mFragments.add(mFragment4);
        } else {
            mCurrent = savedInstanceState.getInt(CURRENT_FRAGMENT, 0);
            mFragments.add(getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_1_KEY));
            mFragments.add(getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_2_KEY));
            mFragments.add(getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_3_KEY));
            mFragments.add(getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_4_KEY));
        }
    }

    private void permissionCheck() {
        mRefusePermissions.clear();
        for (int i = 0; i < mPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, mPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mRefusePermissions.add(mPermissions[i]);
            }
        }
        if (mRefusePermissions.size() > 0) {
            //拒绝权限不为空，表示某些权限没接受
            String[] mRefuse = mRefusePermissions.toArray(new String[mRefusePermissions.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, mRefuse, 1);
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


    /**
     * 返回键，返回到桌面进入后台，而不是退出
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_FRAGMENT, mCurrent);
        if (mFragment1 != null) {
            getSupportFragmentManager().putFragment(outState, FRAGMENT_1_KEY, mFragment1);
        }
        if (mFragment2 != null) {
            getSupportFragmentManager().putFragment(outState, FRAGMENT_2_KEY, mFragment2);
        }
        if (mFragment3 != null) {
            getSupportFragmentManager().putFragment(outState, FRAGMENT_3_KEY, mFragment3);
        }
        if (mFragment4 != null) {
            getSupportFragmentManager().putFragment(outState, FRAGMENT_4_KEY, mFragment4);
        }
        super.onSaveInstanceState(outState);
    }*/
}
