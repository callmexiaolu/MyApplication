package com.example.myapplication.service;

import com.example.myapplication.bean.Post;

import java.util.List;

/**
 * Create By LuKaiqi 2019/02/27
 * Describe:
 *          *发布帖子 --- 两步:
 *                          1.有图片描述，先把图片上传到服务器，得到对应图片的url
 *                          2.帖子的图片url以及其它信息保存到服务器。
 *          *获取帖子数据
 *          *帖子评论，收藏，点赞等
 */
public interface PostService {

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
     * 上传帖子照片到后台
     * @param picturesFilePath 照片的路径
     * @param uploadDone 帖子照片上传成功回调
     */
    void picturesUpload(String[] picturesFilePath, IUploadPostListener uploadDone);

    /**
     * 从服务器中获取帖子数据
     */
    void getPostDataFromServer(IGetPostDataListener listener);


    /**
     * 帖子上传回调接口
     */
    interface IUploadPostListener {
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

    /**
     * 从服务器获取帖子回调
     */
    interface IGetPostDataListener {

        void getSucceed(List<Post> postList);

        void getFailed();
    }
}
