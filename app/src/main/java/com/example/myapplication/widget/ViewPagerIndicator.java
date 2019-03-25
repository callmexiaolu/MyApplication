package com.example.myapplication.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by LuKaiqi
 * 首页顶部导航栏自定义控件,包括tab点击，滑动事件，以及设置导航栏对应的viewpager
 */
public class ViewPagerIndicator extends HorizontalScrollView implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private final int mDefaultTitleColor = Color.BLACK;

    private ViewPager mViewPager;

    private LinearLayout mLinearLayout;

    private int mCurrentTab;

    private ViewPager.OnPageChangeListener mPageChangeListener;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setHorizontalScrollBarEnabled(false);//是否应该绘制滚动条
        setFillViewport(true);//拉伸内容宽度以填充视图
        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        this.addView(mLinearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setViewPager(ViewPager viewPager) {
        setViewPager(viewPager, 0);
    }

    /**
     * 设置viewpager以及启动时默认展示的页面
     * @param viewPager
     * @param item 默认展示的第几个页面
     */
    public void setViewPager(ViewPager viewPager, int item) {
        if (viewPager == null) {
            return;
        }
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(this);
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter != null) {
            mLinearLayout.removeAllViews();
            for (int i = 0; i < adapter.getCount(); i++) {
                CharSequence pageTitle = adapter.getPageTitle(i);
                mLinearLayout.addView(createTitleView(i, pageTitle));
            }
        }
        mCurrentTab = item;
        setCurrentTab(item);
        requestLayout();
    }

    public void setPageChangeListener(ViewPager.OnPageChangeListener pageChangeListener) {
        this.mPageChangeListener = pageChangeListener;
    }



    /**
     * 创建顶部导航栏每一个tab
     * @param i 该tab的tag
     * @param title tab名称
     * @return
     */
    private View createTitleView(int i, CharSequence title) {
        TextView textView = new TextView(getContext());
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(20, 0, 20, 0);
        textView.setTag(i);
        textView.setTextColor(mDefaultTitleColor);
        textView.setClickable(true);
        textView.setOnClickListener(this);
        textView.setTextSize(15);
        addLayout(textView);
        return textView;
    }

    /**
     * 设置顶部导航栏tab的长宽
     * @param textView
     */
    private void addLayout(TextView textView) {
        ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        textView.setLayoutParams(layoutParams);
    }

    /**
     * 设置当前的顶部导航栏tab
     * @param item
     */
    private void setCurrentTab(int item) {
        int childCount = mLinearLayout.getChildCount();
        if (item <= childCount -1) {
            for (int i = 0; i < childCount; i++) {
                View child = mLinearLayout.getChildAt(i);
                if (item == i) {
                    setTabAnimation(child, 1.0f, 1.0f);
                    scrollToTab(item);
                    ((TextView) child).setTextColor(Color.RED);
                } else {
                    setTabAnimation(child, 0.8f, 0.5f);
                    ((TextView) child).setTextColor(Color.BLACK);
                }
            }
        }
    }

    /**
     * 顶部导航栏tab切换过渡动画
     * @param view
     * @param scaleXY
     * @param alpha
     */
    private void setTabAnimation(View view, float scaleXY, float alpha) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", scaleXY);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", scaleXY);
        ObjectAnimator fade =  ObjectAnimator.ofFloat(view, "alpha", alpha);
        AnimatorSet animatorSet = new AnimatorSet();
        //动画合集。表示切换的时候X  Y 方向以及大小会变化
        animatorSet.play(scaleX).with(scaleY).with(fade);
        animatorSet.setDuration(200);
        animatorSet.start();

    }

    /**
     * 顶部导航栏滑动到相应Tab
     * @param item 目标item
     */
    private void scrollToTab(int item) {
        View child = mLinearLayout.getChildAt(item);
        final int scrollPosition = child.getLeft() - (getWidth() - child.getWidth()) / 2;
        smoothScrollTo(scrollPosition, 0);
    }

    @Override
    public void onClick(View v) {
        int po = (int) v.getTag();
        //点击当前tab，不做任何操作
        if (po == mCurrentTab) {
            return;
        }
        mCurrentTab = po;
        setCurrentTab(po);
        if (mViewPager != null) {
            mViewPager.setCurrentItem(po);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrolled(i, v, i1);
        }
    }

    @Override
    public void onPageSelected(int i) {
       setCurrentTab(i);
       if (mPageChangeListener != null) {
           mPageChangeListener.onPageSelected(i);
       }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageScrollStateChanged(i);
        }
    }
}
