package com.example.myapplication.service;

import android.net.Uri;
import android.util.Log;

import com.example.myapplication.MyApplication;
import com.example.myapplication.bean.Book;
import com.example.myapplication.bean.MyBmobUser;
import com.example.myapplication.util.Contast;
import com.example.myapplication.util.StringUtil;
import com.example.myapplication.util.ToastUtil;

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
                           final List<String> picturesFilePath, final IDone done) {
        if (!StringUtil.isEmptyContainsSpace(title) &&
                !StringUtil.isEmptyContainsSpace(content) &&
                !StringUtil.isEmptyContainsSpace(price) &&
                !StringUtil.isEmpty(category)) {
            if (BmobUser.isLogin()) {
                picturesUpload(picturesFilePath, new PublicPostService.IUploadDone() {
                    @Override
                    public void uploadSucceed(List<String> picturesUrls) {
                        savePostInfo(title, content, price, category, picturesUrls, done);
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
     * @param picturesFilePath 照片的uri
     * @return
     */
    @Override
    public void picturesUpload(List<String> picturesFilePath, final PublicPostService.IUploadDone uploadDone) {
        if (picturesFilePath != null && picturesFilePath.size() > 0) {
            String[] urls = new String[picturesFilePath.size()];
            for (int i = 0; i < picturesFilePath.size(); i++) {
                urls[i] = picturesFilePath.get(i);
            }
            BmobFile.uploadBatch(urls, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    //需要把list1取出来，赋值给帖子中的图片链接list
                    uploadDone.uploadSucceed(list1);
                    Log.d(Contast.TAG, "成功");
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

                }

                @Override
                public void onError(int i, String s) {
                    Log.d(Contast.TAG, "帖子照片上传失败:"+ s);
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
                              final List<String> picturesUrls, final IDone done) {
        Book book = new Book(title, content, Double.valueOf(price.trim()), category);
        book.setAuthor(BmobUser.getCurrentUser(MyBmobUser.class));
        book.setPicturesUrl(picturesUrls);
        book.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    ToastUtil.showToast(MyApplication.getAppContext(), "发表成功", true);
                    if (done != null) {
                        done.done();
                    }
                } else {
                    Log.e(Contast.TAG, "public post has error:" + e);
                    ToastUtil.showToast(MyApplication.getAppContext(), "出错了,稍后再试!", true);
                }
            }
        });
    }
}