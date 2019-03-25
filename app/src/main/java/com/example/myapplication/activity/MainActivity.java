package com.example.myapplication.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.myapplication.R;
import com.example.myapplication.event.RefreshEvent;
import com.example.myapplication.fragment.Fragment1;
import com.example.myapplication.fragment.Fragment2;
import com.example.myapplication.fragment.Fragment3;
import com.example.myapplication.fragment.Fragment4;
import com.example.myapplication.model.MyBmobUser;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * Create By LuKaiqi 2019/02/26
 * Describe:主界面
 */
public class MainActivity extends BaseActivity {

    private List<Fragment> mFragments;
    private RadioGroup mRadioGroup;
    private int mCurrent = 0;
    private Fragment mCurrentFragment;
    private RadioButton mRbtn1;
    private Fragment1 mFragment1;
    private Fragment2 mFragment2;
    private Fragment3 mFragment3;
    private Fragment4 mFragment4;

    private static final String FRAGMENT_1_KEY = "fragment1";

    private static final String FRAGMENT_2_KEY = "fragment2";

    private static final String FRAGMENT_3_KEY = "fragment3";

    private static final String FRAGMENT_4_KEY = "fragment4";

    private static final String CURRENT_FRAGMENT = "currentFragment";


    public static final String[] mPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.FOREGROUND_SERVICE
    }; //申请权限

    List<String> mRefusePermissions = new ArrayList<>();//用户拒绝的权限

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews() {
        mRadioGroup = findViewById(R.id.rg_switch);
        mRbtn1 = findViewById(R.id.rbtn_1);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        permissionCheck();
        initFragment(savedInstanceState);
        //设置首页为默认展示页面
        setDefaultFragment();
        //连接到聊天服务器
        connectServer();
    }

    @Override
    public void initListener() {
        mRadioGroup.setOnCheckedChangeListener(mCheckedChangeListener);
    }


    private void setDefaultFragment() {
        try{
            mCurrentFragment = mFragments.get(mCurrent);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fl_tab, mCurrentFragment)
                    .commit();
            mRbtn1.setChecked(true);
            mRbtn1.setTextColor(Color.rgb(231, 55, 62));
        } catch (Exception e) {
            Log.e(Contast.TAG, "MainActivity setDefaultFragment() has Error:", e);
        }

    }

    /**
     * 连接到IM服务器，用于聊天
     */
    private void connectServer() {
        if (BmobUser.isLogin()) {
            final MyBmobUser currentUser = BmobUser.getCurrentUser(MyBmobUser.class);
            //判断用户是否登录，并且连接状态不是已连接，则进行连接服务器操作
            if (!TextUtils.isEmpty(currentUser.getObjectId()) &&
                    BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
                BmobIM.connect(currentUser.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                            //更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                            BmobIM.getInstance().
                                    updateUserInfo(
                                            new BmobIMUserInfo(currentUser.getObjectId(),
                                                    currentUser.getUsername(), currentUser.getAvatarFile()));
                            EventBus.getDefault().post(new RefreshEvent());
                        } else {
                            Log.e(Contast.TAG, "ChatActivity initData has Error:", e);
                        }
                    }
                });
                //监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
                BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                    @Override
                    public void onChange(ConnectionStatus status) {
                        Log.d(Contast.TAG, "当前状态:" + status.getMsg());
                        Log.d(Contast.TAG, "连接状态:" + BmobIM.getInstance().getCurrentStatus().getMsg());
                    }
                });
            }
        }
    }

    RadioGroup.OnCheckedChangeListener mCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) {
                    ((RadioButton) group.getChildAt(mCurrent)).setTextColor(Color.rgb(0, 0, 0));
                    ((RadioButton) group.getChildAt(i)).setTextColor(Color.rgb(231, 55, 62));
                    mCurrent = i;
                    FragmentTransaction mFragmentTransaction = getSupportFragmentManager().beginTransaction();
                    if (!mFragments.get(i).isAdded()) {
                        if (mCurrentFragment != null) {
                            mFragmentTransaction.hide(mCurrentFragment);
                        }
                        mFragmentTransaction.add(R.id.fl_tab, mFragments.get(i))
                                .commit();
                    } else {
                        mFragmentTransaction.hide(mCurrentFragment)
                                .show(mFragments.get(i))
                                .commit();
                    }
                    mCurrentFragment = mFragments.get(i);
                    return;
                }
            }
        }
    };

    private void initFragment(Bundle savedInstanceState) {
        mFragments = new ArrayList<>();
        if (savedInstanceState == null) {
            mFragment1 = new Fragment1();
            mFragment2 = new Fragment2();
            mFragment3 = new Fragment3();
            mFragment4 = new Fragment4();
            mFragments.add(mFragment1);
            mFragments.add(mFragment2);
            mFragments.add(mFragment3);
            mFragments.add(mFragment4);
        } else {
            mCurrent = savedInstanceState.getInt(CURRENT_FRAGMENT, 0);
            mFragments.add(getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_1_KEY));
            mFragments.add(getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_2_KEY));
            mFragments.add(getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_3_KEY));
            mFragments.add(getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_4_KEY));
        }
    }

    private void permissionCheck() {
        mRefusePermissions.clear();
        for (int i = 0; i < mPermissions.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, mPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mRefusePermissions.add(mPermissions[i]);
            }
        }
        if (mRefusePermissions.size() > 0) {
            //拒绝权限不为空，表示某些权限没接受
            String[] mRefuse = mRefusePermissions.toArray(new String[mRefusePermissions.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, mRefuse, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        boolean mPermissionDismiss = false;//有权限没通过
        switch (requestCode) {
            case 1: {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        mPermissionDismiss = true;
                        break;
                    }
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private long mExitTime = 0;

    /**
     * 返回键，连续两次返回退出应用
     */
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            ToastUtil.showToast(this, "再按一次退出程序", true);
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    /**
     * 注册消息接收事件
     * @param event
     */
    //消息接收：通知有在线消息接收
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     * @param event
     */
    //消息接收：通知有离线消息接收
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 会话：获取全部会话的未读消息数量
     */
    private void checkRedPoint() {
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            //有未读消息,设置Fragment3出现红点
        } else {
            //没有未读消息，设置Fragment3不出现红点
        }
    }
}
