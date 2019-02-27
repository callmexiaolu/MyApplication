package com.example.myapplication.service;

import android.net.Uri;

import java.util.List;

/**
 * Create By LuKaiqi 2019/02/27
 * Describe:用户发布帖子
 */
public interface PublicPostService {

    /**
     * 发布帖子
     * @param title 帖子标题
     * @param content 帖子内容
     * @param price 物品价格
     * @param category 帖子种类
     * @param picturesFilePath 帖子图片本地路径 可为null(即不添加图片)
     * @param done 回调接口,发表成功后可以直接关闭界面
     */
    void publicPost(String title,
                    String content,
                    String price,
                    String category,
                    List<String> picturesFilePath,
                    IDone done);

    /**
     * 上传帖子照片到后台，返回照片的链接
     * @param picturesFilePath 照片的uri
     * @param uploadDone 帖子照片上传成功回调
     * @return 照片的链接集合
     */
    void picturesUpload(List<String> picturesFilePath, IUploadDone uploadDone);

    /**
     * 帖子上传成功回调接口
     */
    interface IUploadDone{
        /**
         * 照片上传回调
         * @param picturesUrls 照片上传成功后，在服务器的url
         */
        void uploadSucceed(List<String> picturesUrls);
    }
}
