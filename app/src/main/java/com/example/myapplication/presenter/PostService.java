package com.example.myapplication.service;

import com.example.myapplication.model.MyBmobUser;
import com.example.myapplication.model.Post;

import java.util.List;

import cn.bmob.v3.datatype.BmobRelation;

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
    void getPostDataFromServer(IGetPostDataListener listener, String category);

    /**
     * 帖子评论
     * @param postId 帖子id
     * @param discussContent 评论内容
     * @param user 评论用户
     * @param actionListener 操作回调
     */
    void publicDiscuss(String postId, String discussContent, MyBmobUser user, IPostActionListener actionListener);

    /**
     * 帖子点赞状态检查
     */
    void postThumbUpCheck(final MyBmobUser mCurrentUser, String postId, final IPostActionListener actionListener);

    /**
     * 帖子点赞
     */
    void postThumbUp(final MyBmobUser mCurrentUser,
                     BmobRelation bmobRelation,
                     Post post,
                     IPostActionListener listener);


    /**
     * 帖子评论查询
     */
    void postDiscussQuery(String postId, IPostActionListener actionListener);

    /**
     * 帖子信息更新
     */
    void postUpdate(Post post);


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

    /**
     * 帖子操作回调
     *        *评论
     *        *点赞
     *        *收藏
     */
    interface IPostActionListener{

        void doSucceed(int actionType, List<? extends Object> list);

        void doFailed(int actionType, Exception e);

    }
}
