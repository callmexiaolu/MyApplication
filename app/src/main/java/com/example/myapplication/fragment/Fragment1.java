package com.example.myapplication.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MyViewPagerAdapter;
import com.example.myapplication.fragment.indexFragment.BookFragment;
import com.example.myapplication.fragment.indexFragment.FindFragment;
import com.example.myapplication.fragment.indexFragment.LostFragment;
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

    private String[] mFragmentTitles = new String[]{"二手书", "失物招领", "物品找回"};

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
        mFragmentList.add(new BookFragment());
        mFragmentList.add(new LostFragment());
        mFragmentList.add(new FindFragment());
        mAdapter.setData(mFragmentList, mFragmentTitles);
        mViewPagerHome.setAdapter(mAdapter);
        mViewPagerHome.setOffscreenPageLimit(4);
        mPagerIndicatorHome.setViewPager(mViewPagerHome);
    }

    @Override
    public void initListener() {

    }
}
