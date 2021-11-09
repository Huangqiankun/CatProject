package com.hqk.catproject.http;

/**
 * 顶层的回调接口
 */
public interface ICallback {
    void onSuccess(String result);
    void onFailure(String e);
}
