package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.activity.LoginOrSignActivity;
import com.example.myapplication.activity.PublicPostActivity;
import com.example.myapplication.adapter.MyViewPagerAdapter;
import com.example.myapplication.fragment.indexFragment.CategoryFragment;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.NetWorkUtils;
import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.widget.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:首页Fragment
 */
public class Fragment1 extends BaseFragment implements View.OnClickListener {

    private ImageView mIvAddPost;

    private ViewPagerIndicator mPagerIndicatorHome;

    private ViewPager mViewPagerHome;

    private SearchView mSearchView;

    private List<Fragment> mFragmentList;
    private FragmentManager mFragmentManager;
    private MyViewPagerAdapter mAdapter;

    private CheckBox mCbChooseLayout;



    /**
     * 二手书 ， 服饰， 电子产品， 彩妆
     */
    private CategoryFragment mBookFragment, mClothesFragment, mElectronicFragment, mCosmeticFragment;

    private boolean isGridLayout = true;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_1;
    }

    @Override
    public void initViews(View view) {
        mPagerIndicatorHome = view.findViewById(R.id.vpi_home);
        mViewPagerHome = view.findViewById(R.id.vp_home);
        mIvAddPost = view.findViewById(R.id.iv_add_post);
        mSearchView = view.findViewById(R.id.search_view_fragment1);
        mCbChooseLayout = view.findViewById(R.id.cb_fragment1_choose_layout);
    }

    @Override
    public void initData() {
        mFragmentManager = getActivity().getSupportFragmentManager();
        mAdapter = new MyViewPagerAdapter(mFragmentManager);
        initCategoryFragments();

        mFragmentList = new ArrayList<>();
        mFragmentList.add(mBookFragment);
        mFragmentList.add(mClothesFragment);
        mFragmentList.add(mElectronicFragment);
        mFragmentList.add(mCosmeticFragment);

        mAdapter.setData(mFragmentList, Contast.POST_CATEGORY);
        mViewPagerHome.setAdapter(mAdapter);
        mViewPagerHome.setOffscreenPageLimit(1);
        mPagerIndicatorHome.setViewPager(mViewPagerHome);
    }

    @Override
    public void initListener() {
        mIvAddPost.setFocusable(true);
        mIvAddPost.setClickable(true);
        mIvAddPost.setOnClickListener(this);
        mCbChooseLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_post:
                if (NetWorkUtils.isNetworkConnected()) {
                    if (BmobUser.isLogin()) {
                        startActivity(new Intent(getContext(), PublicPostActivity.class));
                    } else {
                        startActivity(new Intent(getContext(), LoginOrSignActivity.class));
                    }
                } else {
                    ToastUtil.showToast(MyApplication.getAppContext(), "无法连接网络,请检查网络", true);
                }
                break;

            case R.id.cb_fragment1_choose_layout:
                Contast.isGridLayout = !Contast.isGridLayout;
                ((CategoryFragment) mFragmentList.get(mViewPagerHome.getCurrentItem())).chooseLayoutManager();
                break;

            default:
                break;
        }
    }

    /**
     * 初始化fragment
     *          setArguments为了传递各个fragment的数据类别
     *          用于查询各个类别的帖子数据
     */
    private void initCategoryFragments() {
        Bundle bookBundle = new Bundle();
        bookBundle.putString(Contast.POST_CATEGORY_KEY, Contast.POST_CATEGORY[0]);
        mBookFragment = new CategoryFragment();
        mBookFragment.setArguments(bookBundle);

        Bundle clothesBundle = new Bundle();
        clothesBundle.putString(Contast.POST_CATEGORY_KEY, Contast.POST_CATEGORY[1]);
        mClothesFragment = new CategoryFragment();
        mClothesFragment.setArguments(clothesBundle);

        Bundle electronicBundle = new Bundle();
        electronicBundle.putString(Contast.POST_CATEGORY_KEY, Contast.POST_CATEGORY[2]);
        mElectronicFragment = new CategoryFragment();
        mElectronicFragment.setArguments(electronicBundle);

        Bundle cosmeticBundle = new Bundle();
        cosmeticBundle.putString(Contast.POST_CATEGORY_KEY, Contast.POST_CATEGORY[3]);
        mCosmeticFragment = new CategoryFragment();
        mCosmeticFragment.setArguments(cosmeticBundle);
    }

}
