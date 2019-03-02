package com.example.myapplication.service;

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
                    String[] picturesFilePath,
                    IDoCallBack done);

    /**
     * 上传帖子照片到后台，返回照片的链接
     * @param picturesFilePath 照片的路径
     * @param uploadDone 帖子照片上传成功回调
     * @return 照片的链接集合
     */
    void picturesUpload(String[] picturesFilePath, IUploadPost uploadDone);

    /**
     * 帖子上传回调接口
     */
    interface IUploadPost {
        /**
         * 照片上传回调
         * @param picturesUrls 照片上传成功后，在服务器的url
         */
        void uploadSucceed(List<String> picturesUrls);

        /**
         *
         *                  *上传进度
         *                   @param position 当前上传的文件下标
         *                   @param currentProgress 当前上传文件的上传进度
         *                   @param total 总的上传文件数
         *                   @param totalProgress 总进度
         *
         */
        void uploading(int position, int currentProgress, int total, int totalProgress);

        /**
         * 上传失败
         */
        void uploadFailed();
    }
}
