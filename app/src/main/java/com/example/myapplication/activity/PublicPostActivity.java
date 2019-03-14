package com.example.myapplication.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myapplication.MyApplication;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MyGridViewAdapter;
import com.example.myapplication.service.IDoCallBack;
import com.example.myapplication.service.PostService;
import com.example.myapplication.service.PostServiceImpl;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.FileUtils;
import com.example.myapplication.util.GifSizeFilter;
import com.example.myapplication.util.Glide4Engine;
import com.example.myapplication.util.StringUtil;
import com.example.myapplication.util.ToastUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

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

    private PostService mPostService = new PostServiceImpl();

    private EditText mEtPublicTitle, mEtPublicContent, mEtPublicPrice;

    private TextView mTvPublicDone, mTvPublicCategory, mTvProgress;

    private ImageView mIvPublicCancel, mIvPublicPicture;

    private String[] mPicturesPaths;//已选择照片的地址合集

    private GridView mGvSelectedPictures;//展示已选择照片

    private MyGridViewAdapter mGridViewAdapter;

    private ProgressBar mPbProgress;//进度条

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
                switchPostCategory();
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

            default:
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
        final String category = mTvPublicCategory.getText().toString();
        if (!StringUtil.isEmptyContainsSpace(title) &&
                !StringUtil.isEmptyContainsSpace(content) &&
                !StringUtil.isEmptyContainsSpace(price) &&
                !StringUtil.isEmpty(category)) {
            //图片压缩
            if (mSelected != null) {
                mTvPublicDone.setEnabled(false);
                mCallBack.doing(0);
                Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
                Tiny.getInstance().source(mPicturesPaths).batchAsFile().withOptions(options).batchCompress(new FileBatchCallback() {
                    @Override
                    public void callback(boolean isSuccess, String[] outfiles, Throwable t) {
                        if (isSuccess) {
                            Log.d(Contast.TAG, "调用了图片压缩回调");
                            mPostService.publicPost(title, content, price, category, outfiles, mCallBack);
                            return;
                        }
                        ToastUtil.showToast(PublicPostActivity.this, "压缩图片失败,使用原始图片发帖", true);
                        mPostService.publicPost(title, content, price, category, mPicturesPaths, mCallBack);
                    }
                });
                return;
            }
            ToastUtil.showToast(MyApplication.getAppContext(), "请添加照片", true);
        } else {
            ToastUtil.showToast(MyApplication.getAppContext(), "请添加内容", true);
        }
    }

    /**
     * 用户发布帖子时选择照片后,相应数据会回调到该方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            try {
                mSelected = Matisse.obtainResult(data);
                //图片uri转换为path
                mPicturesPaths = new String[mSelected.size()];
                for (int i = 0; i < mSelected.size(); i++) {
                    mPicturesPaths[i] = FileUtils.getRealFilePath(PublicPostActivity.this, mSelected.get(i));
                }
                //展示所选择的照片
                showSelectedPostPictures(mSelected);
                Log.d(Contast.TAG, "PublicPostActivity mSelected done" + mSelected);
            } catch (Exception e) {
                Log.e(Contast.TAG, "选择图片回调出错：" + e);
            }
        }
    }

    /**
     * 选择帖子发布板块
     */
    private void switchPostCategory() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("选择发布板块")
                .setSingleChoiceItems(Contast.POST_CATEGORY, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTvPublicCategory.setText(Contast.POST_CATEGORY[which]);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    /**
     * 展示帖子选择的照片
     * @param selected
     */
    private void showSelectedPostPictures(List<Uri> selected) {
        mGridViewAdapter = new MyGridViewAdapter(this, selected);
        mGvSelectedPictures.setAdapter(mGridViewAdapter);
        mGridViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("删除照片")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelected.remove(position);
                        mGridViewAdapter.notifyDataSetChanged();
                    }
                })
                .setMessage("确认删除？")
                .create();
        dialog.show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private Dialog mDialog;

    //帖子发布回调接口
    private IDoCallBack mCallBack = new IDoCallBack() {
        @Override
        public void done() {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            mTvPublicDone.setEnabled(true);
            ToastUtil.showToast(PublicPostActivity.this, "发表成功", true);
            PublicPostActivity.this.finish();
        }

        @Override
        public void doing(int totalProgress) {
            if (mDialog == null) {
                mDialog = new Dialog(PublicPostActivity.this);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);//去除标题栏
                LayoutInflater inflater = LayoutInflater.from(PublicPostActivity.this);
                View dialogView = inflater.inflate(R.layout.dialog_progress_bar, null);
                mPbProgress = dialogView.findViewById(R.id.pb_progress);
                mTvProgress = dialogView.findViewById(R.id.tv_progress);
                mDialog.setContentView(dialogView);//填充弹窗布局为自定义布局
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                Window window = mDialog.getWindow();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                //注意要在Dialog show之后，再将宽高属性设置进去，才有效果
                mDialog.show();
                window.setAttributes(layoutParams);
                mDialog.setCancelable(false);//点击其它地方，弹窗不会消失
            }
            String progressText = String.valueOf(totalProgress) + "%";
            mPbProgress.setProgress(totalProgress);
            mTvProgress.setText(progressText);
        }

        @Override
        public void doFailed() {
            if (mDialog != null) {
                mDialog.dismiss();
            }
            mTvPublicDone.setEnabled(true);
            ToastUtil.showToast(PublicPostActivity.this, "帖子发布失败", true);
        }
    };

}
