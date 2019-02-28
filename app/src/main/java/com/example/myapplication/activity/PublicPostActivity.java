package com.example.myapplication.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MyGridViewAdapter;
import com.example.myapplication.service.IDoCallBack;
import com.example.myapplication.service.PublicPostService;
import com.example.myapplication.service.PublicPostServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.FileUtils;
import com.example.myapplication.util.GifSizeFilter;
import com.example.myapplication.util.Glide4Engine;
import com.example.myapplication.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create By LuKaiqi 2019/02/26
 * Describe:发布帖子
 *              *标题
 *              *内容
 *              *发布到哪个板块
 *              *图片
 */
public class PublicPostActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    private static final int REQUEST_CODE_CHOOSE = 1;

    private List<Uri> mSelected = null;

    private PublicPostService mPostService = new PublicPostServiceImpl();

    private EditText mEtPublicTitle, mEtPublicContent, mEtPublicPrice;

    private TextView mTvPublicDone, mTvPublicCategory;

    private ImageView mIvPublicCancel, mIvPublicPicture;

    private String[] mPicturesPaths;

    private GridView mGvSelectedPictures;

    private MyGridViewAdapter mGridViewAdapter;

    @Override
    public int setRootLayoutId() {
        return R.layout.activity_public_post;
    }

    @Override
    public void initViews() {
        mTvPublicDone = findViewById(R.id.tv_public_done);
        mTvPublicCategory = findViewById(R.id.tv_public_switch_category);

        mEtPublicTitle = findViewById(R.id.et_public_title);
        mEtPublicContent = findViewById(R.id.et_public_content);
        mEtPublicPrice = findViewById(R.id.et_public_price);

        mIvPublicCancel = findViewById(R.id.iv_public_cancel);
        mIvPublicPicture = findViewById(R.id.iv_public_add_picture);

        mGvSelectedPictures = findViewById(R.id.gv_public_show_selected);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initListener() {
        mTvPublicDone.setFocusable(true);
        mTvPublicDone.setClickable(true);
        mTvPublicDone.setOnClickListener(this);

        mTvPublicCategory.setFocusable(true);
        mTvPublicCategory.setClickable(true);
        mTvPublicCategory.setOnClickListener(this);

        mIvPublicCancel.setFocusable(true);
        mIvPublicCancel.setClickable(true);
        mIvPublicCancel.setOnClickListener(this);

        mIvPublicPicture.setFocusable(true);
        mIvPublicPicture.setClickable(true);
        mIvPublicPicture.setOnClickListener(this);

        mGvSelectedPictures.setOnItemClickListener(this);
        mGvSelectedPictures.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_public_done://发布帖子
                publicDone();
                break;

            case R.id.tv_public_switch_category://选择帖子发布的板块
                break;

            case R.id.iv_public_cancel://取消发布
                this.finish();
                break;

            case R.id.iv_public_add_picture://添加照片,这里使用了知乎的开源图片选择器
                Matisse.from(PublicPostActivity.this)
                        .choose(MimeType.ofImage())
                        .countable(true)
                        .capture(true)
                        .captureStrategy(new CaptureStrategy(true, "com.example.myapplication.fileprovider"))
                        .maxSelectable(Contast.MAX_SELECTED_NUMBER)
                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new Glide4Engine())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
        }
    }

    /**
     * 帖子添加内容完成，点击发布
     */
    private void publicDone() {
        final String title = mEtPublicTitle.getText().toString();
        final String content = mEtPublicContent.getText().toString();
        final String price = mEtPublicPrice.getText().toString();
        final String category = "二手书";
        //图片压缩
        if (mSelected != null) {
            Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(mPicturesPaths).batchAsFile().withOptions(options).batchCompress(new FileBatchCallback() {
                @Override
                public void callback(boolean isSuccess, String[] outfiles, Throwable t) {
                    if (isSuccess) {
                        mPostService.publicPost(title, content, price, category, Arrays.asList(outfiles), mCallBack);
                        return;
                    }
                    ToastUtil.showToast(PublicPostActivity.this, "压缩图片失败,使用原始图片发帖", true);
                    mPostService.publicPost(title, content, price, category, Arrays.asList(mPicturesPaths), mCallBack);
                }
            });
            return;
        }
        mPostService.publicPost(title, content, price, category, null, mCallBack);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            //图片uri转换为path
            mPicturesPaths = new String[mSelected.size()];
            for (int i = 0; i < mSelected.size(); i++) {
                mPicturesPaths[i] = FileUtils.getRealFilePath(PublicPostActivity.this, mSelected.get(i));
            }
            Log.d(Contast.TAG, "PublicPostActivity mSelected done" + mSelected);
        }
    }

    private void initGridView() {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //帖子发布回调接口
    private IDoCallBack mCallBack = new IDoCallBack() {
        @Override
        public void done() {
            ToastUtil.showToast(PublicPostActivity.this, "发表成功", true);
            PublicPostActivity.this.finish();
        }

        @Override
        public void doing() {

        }

        @Override
        public void doFailed() {
            ToastUtil.showToast(PublicPostActivity.this, "帖子发布失败", true);
        }
    };

}
