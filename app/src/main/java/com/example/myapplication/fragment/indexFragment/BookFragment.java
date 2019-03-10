package com.example.myapplication.fragment.indexFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.activity.PostDetailActivity;
import com.example.myapplication.adapter.MyRecyclerViewAdapter;
import com.example.myapplication.bean.Post;
import com.example.myapplication.fragment.BaseFragment;
import com.example.myapplication.service.PostService;
import com.example.myapplication.service.PostServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:首页-二手书
 */
public class BookFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
                                                          MyRecyclerViewAdapter.OnItemClickListener {

    private RecyclerView mRecyclerViewBook;
    private MyRecyclerViewAdapter mAdapter;

    private SwipeRefreshLayout mSrlBook;

    private List<Post> mBookList = new ArrayList<>();

    private PostService postService = new PostServiceImpl();

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
        mAdapter.setOnItemClickListener(this);
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
        mAdapter = new MyRecyclerViewAdapter(getActivity(), mBookList);
        mRecyclerViewBook.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerViewBook.setAdapter(mAdapter);
    }

    /**
     * 用户下拉刷新
     */
    @Override
    public void onRefresh() {
        //刷新获取帖子
        postService.getPostDataFromServer(new PostService.IGetPostDataListener() {
            @Override
            public void getSucceed(List<Post> postList) {
                int postUpdateCount = postList.size() - mBookList.size();
                if (postUpdateCount > 0) {
                    mBookList.clear();
                    mBookList.addAll(postList);
                    mAdapter.notifyDataSetChanged();
                }
                ToastUtil.showToast(MyApplication.getAppContext(), "本次更新帖子数:" + postUpdateCount, true);
            }

            @Override
            public void getFailed() {
                ToastUtil.showToast(MyApplication.getAppContext(), "刷新失败,稍后再试", true);
            }
        });
        mSrlBook.setRefreshing(false);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PostDetailActivity.POST_ID, mBookList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }



}
