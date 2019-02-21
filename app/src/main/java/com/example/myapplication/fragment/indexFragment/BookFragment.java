package com.example.myapplication.fragment.indexFragment;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.bean.Book;
import com.example.myapplication.fragment.BaseFragment;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.ToastUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:首页-二手书
 */
public class BookFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerViewBook;

    private SwipeRefreshLayout mSrlBook;

    private List<Book> mBookList;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_book;
    }

    @Override
    public void initViews(View view) {
        mRecyclerViewBook = view.findViewById(R.id.rv_book_list);
        mSrlBook = view.findViewById(R.id.srl_book_list);
    }

    @Override
    public void initData() {
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void initListener() {
        mSrlBook.setOnRefreshListener(this);

    }

    /**
     * 初始化控件SwipeRefreshLayout
     * 设置下拉距离刷新
     * 设置刷新背景
     */
    private void initSwipeRefreshLayout() {
        mSrlBook.setColorSchemeColors(Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED);
        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSrlBook.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        mSrlBook.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.fragment1_line_color));
        // 设置圆圈的大小
        mSrlBook.setSize(SwipeRefreshLayout.DEFAULT);
    }

    /**
     * 初始化控件RecyclerView
     * 实现瀑布流布局
     */
    private void initRecyclerView() {
        mRecyclerViewBook.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //mRecyclerViewBook.setAdapter();
    }

    @Override
    public void onRefresh() {

    }
}
