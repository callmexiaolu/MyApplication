package com.example.myapplication.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

    private FragmentManager mFragmentManager;
    private List<Fragment> mFragments;
    private RadioGroup mRadioGroup;
    private int mCurrent = 0;
    private Fragment mCurrentFragment;
    private RadioButton mRbtn1;

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        mRadioGroup = findViewById(R.id.rg_switch);
        mFragmentManager = getSupportFragmentManager();
        mRbtn1 = findViewById(R.id.rbtn_1);
    }

    @Override
    public void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(new Fragment1());
        mFragments.add(new Fragment2());
        mFragments.add(new Fragment3());
        mFragments.add(new Fragment4());
        //设置首页为默认展示页面
        mCurrentFragment = mFragments.get(0);
        mFragmentManager
                .beginTransaction()
                .add(R.id.fl_tab, mCurrentFragment)
                .commit();
        mRbtn1.setChecked(true);
        mRbtn1.setTextColor(Color.rgb(0, 204, 51));
    }

    @Override
    public void initListener() {
        mRadioGroup.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    ((RadioButton) group.getChildAt(mCurrent)).setTextColor(Color.rgb(0, 0, 0));
                    ((RadioButton) group.getChildAt(i)).setTextColor(Color.rgb(0, 204, 51));
                    mCurrent = i;
                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
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

}
