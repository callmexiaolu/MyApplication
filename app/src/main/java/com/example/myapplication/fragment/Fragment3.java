package com.example.myapplication.fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activity.ChatActivity;
import com.example.myapplication.adapter.ConversationRecyclerViewAdapter;
import com.example.myapplication.adapter.IOnRecyclerViewListener;
import com.example.myapplication.adapter.base.ImutlipleItem;
import com.example.myapplication.event.RefreshEvent;
import com.example.myapplication.model.Conversation;
import com.example.myapplication.model.PrivateConversation;
import com.example.myapplication.util.Contast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Create by LuKaiqi on 2019/2/17.
 * function:
 */
public class Fragment3 extends BaseFragment {

    @BindView(R.id.rv_fragment3)
    protected RecyclerView mRvConversations;

    @BindView(R.id.swl_fragment3)
    protected SwipeRefreshLayout mSrlConversations;

    private ConversationRecyclerViewAdapter mAdapter;

    private LinearLayoutManager mLayoutManager;

    //单一布局
    ImutlipleItem<Conversation> mImutlipleItem;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_3;
    }

    @Override
    public void initViews(View view) {
        mImutlipleItem = new ImutlipleItem<Conversation>() {
            @Override
            public int getItemLayoutId(int viewtype) {
                return R.layout.rv_fragment3_conversation_item;
            }

            @Override
            public int getItemViewType(int postion, Conversation conversation) {
                return 0;
            }

            @Override
            public int getItemCount(List<Conversation> list) {
                return list.size();
            }
        };
    }

    @Override
    public void initData() {
        mAdapter = new ConversationRecyclerViewAdapter(getActivity(), mImutlipleItem, null);
        mRvConversations.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRvConversations.setLayoutManager(mLayoutManager);
        mSrlConversations.setEnabled(true);
    }

    @Override
    public void initListener() {
       mSrlConversations.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               query();
           }
       });
       mAdapter.setIOnRecyclerViewListener(new IOnRecyclerViewListener() {
           @Override
           public void onItemClick(View view, int position) {
               mAdapter.getItem(position).onClick(getActivity());
           }

           @Override
           public void onItemLongClick(View view, int position) {
               mAdapter.getItem(position).onLongClick(getActivity());
               mAdapter.removeData(position);
           }
       });
    }

    /**
     * 查询本地会话
     */
    public void query() {
        mAdapter.bindDatas(getConversations());
        mAdapter.notifyDataSetChanged();
        mSrlConversations.setRefreshing(false);
    }

    /**
     * 获取会话列表数据
     * @return
     */
    private List<Conversation> getConversations() {
        //添加会话
        List<Conversation> conversationList = new ArrayList<>();
        //sdk 查询全部会话
        List<BmobIMConversation> list = BmobIM.getInstance().loadAllConversation();
        Log.d(Contast.TAG, "会话列表数据有: " + list.size() + "条");
        if (list.size() > 0) {
            for (BmobIMConversation conversation : list) {
                switch (conversation.getConversationType()) {
                    case 1://私聊
                        conversationList.add(new PrivateConversation(conversation));
                        break;

                    default:
                        break;
                }
            }
        }
        //重新排序
        Collections.sort(conversationList);
        return conversationList;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSrlConversations.setRefreshing(true);
        query();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /**
     * EventBus订阅
     *          *注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        //会话页面接收到自定义消息
        mAdapter.bindDatas(getConversations());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * EventBus订阅
     *      *注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        //重新刷新会话列表
        mAdapter.bindDatas(getConversations());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * EventBus订阅
     *      *注册消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        //重新获取本地消息并刷新会话列表
        mAdapter.bindDatas(getConversations());
        mAdapter.notifyDataSetChanged();
    }
}
