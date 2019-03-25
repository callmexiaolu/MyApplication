package com.example.myapplication.fragment.indexFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.activity.PostDetailActivity;
import com.example.myapplication.adapter.IOnRecyclerViewListener;
import com.example.myapplication.adapter.PostRecyclerViewAdapter;
import com.example.myapplication.model.Post;
import com.example.myapplication.fragment.BaseFragment;
import com.example.myapplication.presenter.PostService;
import com.example.myapplication.presenter.PostServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:帖子展示
 */
public class CategoryFragment
        extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, IOnRecyclerViewListener {

    private RecyclerView mRecyclerViewData;

    private PostRecyclerViewAdapter mRvGridLayoutAdapter, mRvLinearLayoutAdapter;

    private SwipeRefreshLayout mSrlCategory;

    private StaggeredGridLayoutManager mGridLayoutManager;

    private LinearLayoutManager mLinearLayoutManager;

    private TextView mTvNOData;

    private List<Post> mDataList = new ArrayList<>();

    private PostService postService = new PostServiceImpl();

    /** 帖子类型 */
    private String mPostCategory;

    private boolean isGridLayout = true;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void initViews(View view) {
        mRecyclerViewData = view.findViewById(R.id.rv_book_list);
        mSrlCategory = view.findViewById(R.id.srl_book_list);
        mTvNOData = view.findViewById(R.id.tv_category_no_data);
    }

    @Override
    public void initData() {
        mPostCategory = getArguments().getString(Contast.POST_CATEGORY_KEY);
        mGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        initSwipeRefreshLayout();
        initRecyclerView();

    }

    @Override
    public void initListener() {
        mSrlCategory.setOnRefreshListener(this);
        onRefresh();//进入该页面就刷新获取数据
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
        mRecyclerViewData.setHasFixedSize(true);

        mRvGridLayoutAdapter = new PostRecyclerViewAdapter(getActivity(), mDataList);
        mRvGridLayoutAdapter.setOnItemClickListener(this);
        mRvGridLayoutAdapter.setLayoutType(true);

        mRvLinearLayoutAdapter = new PostRecyclerViewAdapter(getActivity(), mDataList);
        mRvLinearLayoutAdapter.setOnItemClickListener(this);
        mRvLinearLayoutAdapter.setLayoutType(false);

        chooseLayoutManager();
    }

    /**
     * 设置布局管理器
     *         *瀑布流
     *         *列表
     */
    public void chooseLayoutManager() {
        if (Contast.isGridLayout) {
            mRecyclerViewData.setLayoutManager(mGridLayoutManager);
            mRecyclerViewData.setAdapter(mRvGridLayoutAdapter);
        } else {
            mRecyclerViewData.setLayoutManager(mLinearLayoutManager);
            mRecyclerViewData.setAdapter(mRvLinearLayoutAdapter);
        }
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
                if (postList != null && postList.size() > 0) {
                    if (mRecyclerViewData.getVisibility() == View.GONE) {
                        mRecyclerViewData.setVisibility(View.VISIBLE);
                        mTvNOData.setVisibility(View.GONE);
                    }
                    mDataList.clear();
                    mDataList.addAll(postList);
                    if (isGridLayout) {
                        mRvGridLayoutAdapter.notifyDataSetChanged();
                    } else {
                        mRvLinearLayoutAdapter.notifyDataSetChanged();
                    }
                } else {
                    mRecyclerViewData.setVisibility(View.GONE);
                    mTvNOData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void getFailed(BmobException e) {
                if (e.getErrorCode() == 9016) {
                    ToastUtil.showToast(MyApplication.getAppContext(), "无法连接网络,请检查网络", true);
                }
                Log.e(Contast.TAG, "CategoryFragment 刷新帖子异常:" + e);
                mRecyclerViewData.setVisibility(View.GONE);
                mTvNOData.setVisibility(View.VISIBLE);
            }
        }, mPostCategory);
        mSrlCategory.setRefreshing(false);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PostDetailActivity.POST_ID, mDataList.get(position));
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
            chooseLayoutManager();
        }
    }
}
