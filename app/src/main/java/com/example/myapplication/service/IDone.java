package com.example.myapplication.service;

/**
 * Describe:为了避免传递activity，因此使用接口回调来实现activity的操作
 */
public interface IDone {

    /**
     * activity完成任务回调方法
     */
    void done();
}
