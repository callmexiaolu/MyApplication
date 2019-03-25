package com.example.myapplication.adapter;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:
 */
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.List;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;

    private String[] mFragmentTitles;

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<Fragment> fragmentList, String[] titltes){
        this.fragmentList = fragmentList;
        mFragmentTitles = titltes;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragmentList.get(arg0);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles[position];
    }
}
