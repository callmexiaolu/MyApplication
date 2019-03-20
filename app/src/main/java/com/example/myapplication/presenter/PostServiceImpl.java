package com.example.myapplication.service;

import android.util.Log;

import com.example.myapplication.activity.PostDetailActivity;
import com.example.myapplication.model.Comment;
import com.example.myapplication.model.MyBmobUser;
import com.example.myapplication.model.Post;
import com.example.myapplication.util.Contast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * Create By LuKaiqi
 * Describe:
 *          *帖子发布
 *          *帖子获取
 *          *帖子评论，点赞，收藏等
 */
public class PostServiceImpl implements PostService {

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
        picturesUpload(picturesFilePath, new IUploadPostListener() {
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
    public void picturesUpload(final String[] picturesFilePath, final IUploadPostListener upload) {
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
     * 从服务器中获取帖子数据
     * @param category 帖子类型
     */
    @Override
    public void getPostDataFromServer(final IGetPostDataListener listener, String category) {
        BmobQuery<Post> bmobQuery = new BmobQuery<>();
        bmobQuery.include("author");
        bmobQuery.addWhereEqualTo("category", category);
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> list, BmobException e) {
                if (e == null) {
                    listener.getSucceed(list);
                    Log.d(Contast.TAG, "获取到帖子数据");
                } else {
                    listener.getFailed();
                    Log.e(Contast.TAG, "CategoryFragment 刷新帖子异常:" + e);
                }
            }
        });
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
        Post post = new Post(title, content, Double.valueOf(price.trim()), category);
        post.setAuthor(BmobUser.getCurrentUser(MyBmobUser.class));
        post.setPicturesUrl(picturesUrls);
        post.save(new SaveListener<String>() {
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

    /**
     * 发布评论
     * @param postId 帖子id
     * @param discussContent 评论内容
     * @param user 评论用户
     * @param actionListener 操作回调
     */
    @Override
    public void publicDiscuss(String postId, String discussContent, MyBmobUser user, final IPostActionListener actionListener) {
        Post post = new Post();
        post.setObjectId(postId);
        final Comment comment = new Comment();
        comment.setContent(discussContent);
        comment.setPost(post);
        comment.setUser(user);
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    List<String> result = new ArrayList<>();
                    result.add(s);
                    actionListener.doSucceed(PostDetailActivity.USER_ACTION_DISCUSS, result);
                } else {
                    actionListener.doFailed(PostDetailActivity.USER_ACTION_DISCUSS, e);
                }
            }
        });
    }

    /**
     * 帖子点赞,收藏状态检查
     * @param mCurrentUser 当前用户
     * @param postId 帖子id
     * @param actionListener 帖子操作回调
     */
    @Override
    public void postThumbUpCheck(final MyBmobUser mCurrentUser, String postId, final IPostActionListener actionListener) {
        if (mCurrentUser != null) {
            BmobQuery<MyBmobUser> query = new BmobQuery<>();
            Post post = new Post();
            post.setObjectId(postId);
            query.addWhereRelatedTo("thumbUpRelation", new BmobPointer(post));
            Log.d(Contast.TAG, "检查点赞收藏状态");
            query.findObjects(new FindListener<MyBmobUser>() {
                @Override
                public void done(List<MyBmobUser> list, BmobException e) {
                    if (e == null) {
                        if (list.contains(mCurrentUser) ) {//用户点过了赞
                            actionListener.doSucceed(PostDetailActivity.USER_ACTION_THUMBUP, list);
                            Log.d(Contast.TAG, "用户点过了赞");
                            Log.d(Contast.TAG, "检查点赞状态:" + "点赞用户数量:" + list.size() + "用户:" + list);
                        }
                    } else {
                        actionListener.doFailed(PostDetailActivity.USER_ACTION_THUMBUP, e);
                    }
                }
            });
        }
    }

    /**
     * 帖子点赞
     * @param mCurrentUser 当前用户
     * @param bmobRelation 帖子点赞的用户列表
     * @param post 帖子
     * @param listener 帖子数据更新回调
     */
    @Override
    public void postThumbUp(final MyBmobUser mCurrentUser,
                            BmobRelation bmobRelation,
                            Post post,
                            final IPostActionListener listener) {
        bmobRelation.add(mCurrentUser);
        post.setThumbUpRelation(bmobRelation);
        post.setThumbUp(post.getThumbUp() + 1);
        Log.d(Contast.TAG, post.getThumbUp() + "");
        post.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    listener.doSucceed(PostDetailActivity.USER_ACTION_THUMBUP, null);
                } else {
                    listener.doFailed(PostDetailActivity.USER_ACTION_THUMBUP, e);
                }
            }
        });
    }

    /**
     * 帖子评论查询
     * @param postId
     * @param actionListener
     */
    @Override
    public void postDiscussQuery(String postId, final IPostActionListener actionListener) {
        BmobQuery<Comment> query = new BmobQuery<>();
        Post post = new Post();
        post.setObjectId(postId);
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.include("user");
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    actionListener.doSucceed(PostDetailActivity.USER_ACTION_QUERY_DISCUSS, list);
                } else {
                    actionListener.doFailed(PostDetailActivity.USER_ACTION_QUERY_DISCUSS, e);
                }
            }
        });
    }

    /**
     * 更新帖子信息
     */
    @Override
    public void postUpdate(final Post post) {
        post.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.d(Contast.TAG, "帖子浏览量+1，当前该帖子浏览量:" + post.getLookCount());
                } else {
                    Log.e(Contast.TAG, "设置帖子浏览量异常:", e);
                }
            }
        });
    }
}
