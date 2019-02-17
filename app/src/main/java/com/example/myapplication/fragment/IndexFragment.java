package com.example.myapplication.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MyViewPagerAdapter;
import com.example.myapplication.fragment.indexFragment.BuyFragment;
import com.example.myapplication.fragment.indexFragment.FindFragment;
import com.example.myapplication.fragment.indexFragment.LostFragment;
import com.example.myapplication.fragment.indexFragment.SellFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:首页Fragment
 */
public class IndexFragment extends BaseFragment{

    private ViewPager mViewPager;
    private HorizontalScrollView mScrollView;
    private LinearLayout mLinearLayout;
    private FragmentManager mFragmentManager;

    private ArrayList<Integer> moveToList = new ArrayList<>();          //viewPager滑动时标题栏跟随滑动距离
    private int mTitleMargin;                       //每个标题间的间隔

    private List<String> mTitles = new ArrayList<>();
    private List<TextView> mTextViews = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    private int currentPosition;
    private String[] mStrings = new String[]{"求购", "出售", "失物招领", "物品找回"};
    private int[] ids = new int[]{0, 1, 2, 3};

    private MyViewPagerAdapter mAdapter;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_index;
    }

    @Override
    public void initViews(View view) {
        mViewPager = view.findViewById(R.id.vp_tab);
        mScrollView = view.findViewById(R.id.horscr_tab);
        mLinearLayout = view.findViewById(R.id.ll_tab);
    }

    @Override
    public void initData() {
        mFragmentManager = getActivity().getSupportFragmentManager();
        mAdapter = new MyViewPagerAdapter(mFragmentManager);
        mFragments.add(new BuyFragment());
        mFragments.add(new SellFragment());
        mFragments.add(new LostFragment());
        mFragments.add(new FindFragment());

        for (int i = 0; i < mStrings.length; i++) {
            mTitles.add(mStrings[i]);
            addTitleLayout(mTitles.get(i), ids[i]);
        }
        mAdapter.setData(mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mTextViews.get(0).setTextColor(Color.rgb(255, 0, 0));
        currentPosition = 0;
    }


    @Override
    public void initListener() {
        mViewPager.addOnPageChangeListener(mListener);
    }

    private ViewPager.OnPageChangeListener mListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            //i为当前页面的下标
            mTextViews.get(currentPosition).setTextColor(Color.rgb(0, 0, 0));
            mTextViews.get(i).setTextColor(Color.rgb(255, 0, 0));
            currentPosition = i;
            mScrollView.scrollTo(moveToList.get(i), 0);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    /**
     * 添加标题栏
     * @param title     标题名称
     * @param position  标题索引
     */
    private void addTitleLayout(String title, int position){
        final TextView textView = (TextView) getLayoutInflater().inflate(R.layout.textview_index_title, null);
        textView.setText(title);
        textView.setTag(position);
        textView.setClickable(true);
        textView.setOnClickListener(new posOnClickListener());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = dip2px(getActivity(), mTitleMargin);
        params.rightMargin = dip2px(getActivity(), mTitleMargin);
        mLinearLayout.addView(textView, params);
        mTextViews.add(textView);
        int width;
        //如果是第一个标题则不设滑动距离
        if(position==0){
            width = 0;
            moveToList.add(width);
        }
        //第N个标题的滑动距离是上一个标题的宽度加上标题之间的间隔,这样的话滑动viewPager的时候保证当前标题栏在最左侧
        else{
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            mTextViews.get(position-1).measure(w, h);
            width = mTextViews.get(position-1).getMeasuredWidth() + mTitleMargin*4;
            moveToList.add(width+moveToList.get(moveToList.size()-1));
        }
    }

    /**
     *  点击标题栏
     */
    class posOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //点击的是当前标题什么都不做
            if((int)view.getTag() == currentPosition){
                return;
            }
            //标题栏变色且设置viewPager
            mTextViews.get(currentPosition).setTextColor(Color.rgb(0, 0, 0));
            currentPosition = (int) view.getTag();
            mTextViews.get(currentPosition).setTextColor(Color.rgb(255, 0, 0));
            mViewPager.setCurrentItem(currentPosition);
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
