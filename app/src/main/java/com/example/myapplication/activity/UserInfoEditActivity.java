package com.example.myapplication.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.MyBmobUser;
import com.example.myapplication.presenter.IDoCallBack;
import com.example.myapplication.presenter.UserService;
import com.example.myapplication.presenter.UserServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.GifSizeFilter;
import com.example.myapplication.util.Glide4Engine;
import com.example.myapplication.util.NetWorkUtils;
import com.example.myapplication.util.ToastUtil;
import com.example.myapplication.widget.RoundRectImageView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.common.FileResult;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Create by LuKaiqi on 2019/3/15.
 * function:用户信息编辑
 */
public class UserInfoEditActivity extends BaseActivity implements View.OnClickListener {

    private RoundRectImageView mIvUserInfoAvatar;

    private EditText mEtUserInfoName, mEtUserInfoSign;

    private Button mBtnUserInfoSave;

    private ImageView mIvBack;

    private static final int REQUEST_CODE_CHOOSE = 1;//头像选择回调

    private String mSelectedAvatarPath = null;

    private MyBmobUser mCurrentUser = BmobUser.getCurrentUser(MyBmobUser.class);;


    @Override
    public int setRootLayoutId() {
        return R.layout.activity_user_info_edit;
    }

    @Override
    public void initViews() {
        mIvUserInfoAvatar = findViewById(R.id.iv_info_edit_avatar);
        mEtUserInfoName = findViewById(R.id.et_info_edit_name);
        mEtUserInfoSign = findViewById(R.id.et_info_edit_sign);
        mBtnUserInfoSave = findViewById(R.id.btn_info_edit_save);
        mIvBack = findViewById(R.id.include_info_edit_back);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mEtUserInfoName.setText(mCurrentUser.getUsername());
        mEtUserInfoSign.setText(mCurrentUser.getSignature());
        if (mCurrentUser.getAvatarFile() != null) {
            Glide.with(this).load(mCurrentUser.getAvatarFile()).into(mIvUserInfoAvatar);
        } else {
            mIvUserInfoAvatar.setImageResource(R.drawable.ic_action_me);
        }
    }

    @Override
    public void initListener() {
        mIvUserInfoAvatar.setFocusable(true);
        mIvUserInfoAvatar.setClickable(true);
        mIvUserInfoAvatar.setOnClickListener(this);

        mIvBack.setFocusable(true);
        mIvBack.setClickable(true);
        mIvBack.setOnClickListener(this);

        mBtnUserInfoSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_info_edit_avatar) {
            Matisse.from(UserInfoEditActivity.this)
                    .choose(MimeType.ofImage())
                    .countable(true)
                    .capture(true)
                    .captureStrategy(new CaptureStrategy(true, "com.example.myapplication.fileprovider"))
                    .maxSelectable(1)
                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new Glide4Engine())
                    .forResult(REQUEST_CODE_CHOOSE);
        } else if (v.getId() == R.id.btn_info_edit_save) {
            if (NetWorkUtils.isNetworkConnected()) {
                saveUserInfo();
            } else {
                ToastUtil.showToast(this, "请检查网络连接", true);
            }
        } else if (v.getId() == R.id.include_info_edit_back) {
            this.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            try{
                //回调获得选择的头像的路径
                List<String> picturePath = Matisse.obtainPathResult(data);
                mSelectedAvatarPath = picturePath.get(0);
                Glide.with(this).load(mSelectedAvatarPath).into(mIvUserInfoAvatar);
                //头像图片压缩
                Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                FileResult fileResult = Tiny.getInstance().source(mSelectedAvatarPath).asFile().withOptions(options).compressSync();
                mSelectedAvatarPath = fileResult.outfile;
            } catch (Exception e) {
               Log.e(Contast.TAG, "用户信息编辑选择头像出错:", e);
            }
        }
    }

    /**
     * 保存用户信息到服务器
     */
    private void saveUserInfo() {
        final String sign = mEtUserInfoSign.getText().toString();
        final String name = mEtUserInfoName.getText().toString();
        if (mSelectedAvatarPath != null) {
            BmobFile bmobFile = new BmobFile(new File(mSelectedAvatarPath));
            bmobFile.upload(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mCurrentUser.setAvatar(bmobFile.getFileUrl());
                        mCurrentUser.setSignature(sign);
                        mCurrentUser.setUsername(name);
                        UserService service = new UserServiceImpl();
                        service.userInfoSave(mCurrentUser, new IDoCallBack() {
                            @Override
                            public void done() {
                                ToastUtil.showToast(UserInfoEditActivity.this, "保存成功", true);
                                UserInfoEditActivity.this.finish();
                            }

                            @Override
                            public void doing(int totalProgress) {

                            }

                            @Override
                            public void doFailed() {
                                ToastUtil.showToast(UserInfoEditActivity.this, "保存失败,稍后再试", true);
                            }
                        });
                    } else {
                        Log.e(Contast.TAG, "UserInfoEdit saveUserInfo() has Error:", e);
                        ToastUtil.showToast(UserInfoEditActivity.this, "保存失败,稍后再试", true);
                    }
                }
            });
        }
    }
}
