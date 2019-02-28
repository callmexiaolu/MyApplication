package com.example.myapplication.service;

/**
 * Describe:为了避免传递activity，因此使用接口回调来实现activity的操作
 */
public interface IDoCallBack {

    /**
     * activity完成任务回调方法
     */
    void done();


    /**
     * activity任务进行中回调方法
     * (这里可以在activity进行任务中实现一个加载等待的动画)
     */
    void doing();


    /**
     * activity进行的任务失败回调方法
     * (比如登录失败，发帖失败等)
     */
    void doFailed();
}
