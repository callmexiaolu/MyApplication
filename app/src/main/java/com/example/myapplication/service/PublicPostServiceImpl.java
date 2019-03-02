package com.example.myapplication.service;

import android.util.Log;

import com.example.myapplication.MyApplication;
import com.example.myapplication.bean.Book;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.StringUtil;
import com.example.myapplication.util.ToastUtil;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;


public class PublicPostServiceImpl implements PublicPostService {

    /**
     * 发布帖子
     * @param title 帖子标题
     * @param content 帖子内容
     * @param price 物品价格
     * @param category 帖子种类
     * @param picturesFilePath 帖子图片本地路径 可为null(即不添加图片)
     * @param done 回调接口,发表成功后可以直接关闭界面
     */
    @Override
    public void publicPost(final String title, final String content,
                           final String price, final String category,
                           final String[] picturesFilePath, final IDoCallBack done) {
        if (!StringUtil.isEmptyContainsSpace(title) &&
                !StringUtil.isEmptyContainsSpace(content) &&
                !StringUtil.isEmptyContainsSpace(price) &&
                !StringUtil.isEmpty(category)) {
            if (BmobUser.isLogin()) {
                if (picturesFilePath == null || picturesFilePath.length == 0)  {
                    savePostInfo(title, content, price, category, null, done);
                    return;
                }
                picturesUpload(picturesFilePath, new IUploadPost() {
                    @Override
                    public void uploadSucceed(List<String> picturesUrls) {
                        savePostInfo(title, content, price, category, picturesUrls, done);
                    }

                    @Override
                    public void uploading(int position, int currentProgress, int total, int totalProgress) {
                        done.doing(totalProgress);
                    }

                    @Override
                    public void uploadFailed() {
                        done.doFailed();
                    }
                });
            }
        } else {
            ToastUtil.showToast(MyApplication.getAppContext(), "内容和板块不能为空", true);
        }

    }

    /**
     * 上传文件到服务器
     *  *******
     *  list为上传文件 BmobFile类型
     *  list1为文件的服务器地址 String类型
     *  ******
     * @param picturesFilePath 照片的绝对路径(这里的照片经过了压缩)
     * @return
     */
    @Override
    public void picturesUpload(final String[] picturesFilePath, final IUploadPost upload) {
        if (picturesFilePath != null && picturesFilePath.length > 0) {
            BmobFile.uploadBatch(picturesFilePath, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    //需要把list1取出来，赋值给帖子中的图片链接list
                    //因为每上传成功一张图片就会回调一次，并且传过来的list1数据是上传成功的url合集
                    //所以这里判断一下是否全部上传完，上传完之后再保存数据
                    if (list1.size() == picturesFilePath.length) {
                        upload.uploadSucceed(list1);
                        Log.d(Contast.TAG, "图片批量上传成功");
                    }
                }

                /**
                 *批量上传进度监听
                 * @param i 当前上传的文件下标
                 * @param i1 当前上传文件的上传进度
                 * @param i2 总的上传文件数
                 * @param i3 总进度
                 */
                @Override
                public void onProgress(int i, int i1, int i2, int i3) {
                    upload.uploading(i, i1, i2, i3);
                    Log.d(Contast.TAG, "上传第" + i + "张图片,当前进度:" + i1 + "%,当前上传总图片数为:" + i2 + ",总进度:" + i3 + "%");
                }

                @Override
                public void onError(int i, String s) {
                    upload.uploadFailed();
                    Log.e(Contast.TAG, "帖子照片上传失败:"+ s);
                }
            });
        }
    }

    /**
     * 保存用户帖子信息到服务器
     * @param title
     * @param content
     * @param price
     * @param category
     * @param picturesUrls
     * @param done
     */
    private void savePostInfo(final String title, final String content,
                              final String price, final String category,
                              final List<String> picturesUrls, final IDoCallBack done) {
        Book book = new Book(title, content, Double.valueOf(price.trim()), category);
        book.setAuthor(BmobUser.getCurrentUser(MyBmobUser.class));
        book.setPicturesUrl(picturesUrls);
        book.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    if (done != null) {
                        done.done();
                    }
                } else {
                    done.doFailed();
                    Log.e(Contast.TAG, "save post info has error:" + e);
                }
            }
        });
    }
}
