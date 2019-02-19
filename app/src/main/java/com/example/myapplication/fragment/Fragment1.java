package com.example.myapplication.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MyViewPagerAdapter;
import com.example.myapplication.fragment.indexFragment.BuyFragment;
import com.example.myapplication.fragment.indexFragment.FindFragment;
import com.example.myapplication.fragment.indexFragment.LostFragment;
import com.example.myapplication.fragment.indexFragment.SellFragment;
import com.example.myapplication.widget.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;
/**
 * Create by LuKaiqi on 2019/2/17.
 * function:首页Fragment
 */
public class Fragment1 extends BaseFragment{

    private ViewPagerIndicator mPagerIndicatorHome;

    private ViewPager mViewPagerHome;

    private String[] mFragmentTitles = new String[]{"求购", "出售", "失物招领", "物品找回"};

    private List<Fragment> mFragmentList;
    private FragmentManager mFragmentManager;
    private MyViewPagerAdapter mAdapter;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_1;
    }

    @Override
    public void initViews(View view) {
        mPagerIndicatorHome = view.findViewById(R.id.vpi_home);
        mViewPagerHome = view.findViewById(R.id.vp_home);
    }

    @Override
    public void initData() {
        mFragmentManager = getActivity().getSupportFragmentManager();
        mAdapter = new MyViewPagerAdapter(mFragmentManager);
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new BuyFragment());
        mFragmentList.add(new SellFragment());
        mFragmentList.add(new LostFragment());
        mFragmentList.add(new FindFragment());
        mAdapter.setData(mFragmentList, mFragmentTitles);
        mViewPagerHome.setAdapter(mAdapter);
        mPagerIndicatorHome.setViewPager(mViewPagerHome);
    }

    @Override
    public void initListener() {

    }
}
