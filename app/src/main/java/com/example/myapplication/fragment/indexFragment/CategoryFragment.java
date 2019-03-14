package com.example.myapplication.fragment.indexFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.activity.PostDetailActivity;
import com.example.myapplication.adapter.MyRecyclerViewAdapter;
import com.example.myapplication.bean.Post;
import com.example.myapplication.fragment.BaseFragment;
import com.example.myapplication.fragment.Fragment1;
import com.example.myapplication.service.PostService;
import com.example.myapplication.service.PostServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:帖子展示
 */
public class CategoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
                                                          MyRecyclerViewAdapter.OnItemClickListener {
    private RecyclerView mRecyclerViewData;

    private MyRecyclerViewAdapter mAdapter;

    private SwipeRefreshLayout mSrlCategory;

    private List<Post> mBookList = new ArrayList<>();

    private PostService postService = new PostServiceImpl();

    /** 帖子类型 */
    private String mPostCategory;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void initViews(View view) {
        mRecyclerViewData = view.findViewById(R.id.rv_book_list);
        mSrlCategory = view.findViewById(R.id.srl_book_list);
    }

    @Override
    public void initData() {
        mPostCategory = getArguments().getString(Contast.POST_CATEGORY_KEY);
        initSwipeRefreshLayout();
        initRecyclerView();
        onRefresh();//进入该页面就刷新获取数据
    }

    @Override
    public void initListener() {
        mSrlCategory.setOnRefreshListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    /**
     * 初始化控件SwipeRefreshLayout
     * 设置下拉距离刷新
     * 设置刷新背景
     */
    private void initSwipeRefreshLayout() {
        mSrlCategory.setColorSchemeColors(Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.RED);
        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSrlCategory.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        mSrlCategory.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.fragment1_line_color));
        // 设置圆圈的大小
        mSrlCategory.setSize(SwipeRefreshLayout.DEFAULT);
    }

    /**
     * 初始化控件RecyclerView
     * 实现瀑布流布局
     */
    private void initRecyclerView() {
        mAdapter = new MyRecyclerViewAdapter(getActivity(), mBookList);
        chooseLayoutManager();
        mRecyclerViewData.setAdapter(mAdapter);
    }

    /**
     * 设置布局管理器
     *         *瀑布流
     *         *列表
     */
    private void chooseLayoutManager() {

        //mRecyclerViewData.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mRecyclerViewData.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                mBookList.clear();
                mBookList.addAll(postList);
                mAdapter.notifyDataSetChanged();
                ToastUtil.showToast(MyApplication.getAppContext(), "更新完毕", true);
            }

            @Override
            public void getFailed() {
                ToastUtil.showToast(MyApplication.getAppContext(), "刷新失败,稍后再试", true);
            }
        }, mPostCategory);
        mSrlCategory.setRefreshing(false);
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onRefresh();
        }
    }
}
